package ru.ifmo.ctd.ngp.opt.misc.earl

import scala.language.higherKinds

import ru.ifmo.ctd.ngp.opt.event.{TerminationStartedEvent, IterationFinishedEvent, InitializationFinishedEvent}
import ru.ifmo.ctd.ngp.opt.types.{WorkingSetType, CodomainType, DomainType}
import ru.ifmo.ctd.ngp.learning.reinforce.{EnvironmentPrinter => OEP, Environment => OEnv, Agent => OAgent}

object EARLConfiguration {
  class Detected[D: DomainType, C: CodomainType, W[+_D, +_C]: WorkingSetType] {
    /**
     * Registers a configuration for EA+RL based on a 'universal' algorithm for
     * co-optimal helpers.
     */
    def registerUniversalCoOptimal()(
      implicit initializationFinished: InitializationFinishedEvent[W[D, C]],
               iterationFinished: IterationFinishedEvent[W[D, C]],
               earlInterface: EARLInterface
    ) {
      var remained = 1L
      var level = 0L
      InitializationFinishedEvent().addListener { _ =>
        level = 0
        remained = 1
        earlInterface.currentChoice = 0
      }
      IterationFinishedEvent().addListener {_ =>
        remained -= 1
        if (remained == 0) {
          val ch = earlInterface.currentChoice + 1
          earlInterface.currentChoice = if (ch == earlInterface.choices) {
            level += 1
            0
          } else ch
          remained = 1L << level
        }
      }
    }
    /**
     * Registers a configuration for EA+RL constructed from the old-fashioned RL agent, the state function
     * and the implicitly available EARLCodomainComparator, EvaluationFinishedEvent,
     * InitializationFinishedEvent and TerminationStartedEvent.
     *
     * This is a long ugly story of coping with the non-reactive design of ru.ifmo.ctd.ngp.learning.reinforce.Agent.
     *
     * @param agent the reinforcement learning agent.
     * @param stateFunction the function returning the RL state for the current working set.
     * @param rewardFunction the function returning the RL reward for the consecutive working sets.
     */
    def registerOldWay[S](
               agent: OAgent[S, Int],
               stateFunction: W[D, C] => S,
               rewardFunction: (W[D, C], W[D, C]) => Double
    )(
      implicit iterationFinished: IterationFinishedEvent[W[D, C]],
               initializationFinished: InitializationFinishedEvent[W[D, C]],
               terminationStarted: TerminationStartedEvent,
               earlInterface: EARLInterface
    ) {
      val AgentInterruption = new RuntimeException("Agent is interrupted")
      class MyEnv extends OEnv[S, Int] {
        var lastWorkingSet: W[D, C] = _
        var lastState: S = _
        var lastReward: Double = _
        var lastAction: Int = _

        val sync = new Object
        var isAgentRunning = true
        var isTerminating = false
        var agentThread: Thread = _

        def addPrinter(printer: OEP[S, Int]) {}
        def applyAction(action: Int) = {
          lastAction = action
          sync.synchronized {
            isAgentRunning = false
            sync.notify()
            while (!isAgentRunning && !isTerminating) {
              sync.wait()
            }
            if (isTerminating) {
              throw AgentInterruption
            }
          }
          lastReward
        }
        def getCurrentState = lastState
        val getActions = java.util.Arrays.asList(0 until earlInterface.choices :_*)
        def actionsCount() = getActions.size()
        def firstAction() = getActions.get(0)
        def isInTerminalState = false
        def getLastReward = lastReward

        def kill() {
          sync.synchronized {
            isTerminating = true
            sync.notify()
          }
          agentThread.join()
        }

        def waitForApplyAction() {
          sync.synchronized {
            isAgentRunning = true
            sync.notify()
            while (isAgentRunning) {
              sync.wait()
            }
          }
        }
      }

      var previousWS: Option[W[D, C]] = None
      val myAgent = agent.makeClone()
      val environment = new MyEnv

      InitializationFinishedEvent().addListener { ws =>
        myAgent.refresh()
        previousWS = Some(ws)
        environment.lastWorkingSet = ws
        environment.lastState = stateFunction(ws)
        environment.agentThread = new Thread(
          new Runnable {
            def run() {
              try {
                myAgent.learn(environment)
              } catch {
                case AgentInterruption =>
                case th: Throwable => throw th
              }
            }
          }, "Agent Runner"
        )
        environment.agentThread.start()
        environment.waitForApplyAction()
        earlInterface.currentChoice = environment.lastAction
      }

      IterationFinishedEvent().addListener { ws =>
        val currentState = stateFunction(ws)
        val currentReward = rewardFunction(environment.lastWorkingSet, ws)
        environment.lastWorkingSet = ws
        environment.lastState = currentState
        environment.lastReward = currentReward
        environment.waitForApplyAction()
        earlInterface.currentChoice = environment.lastAction
      }

      TerminationStartedEvent().addListener { _ =>
        environment.kill()
      }
    }
  }
  /**
   * Detects the domain, codomain, `Evaluated` object type and the working set type, and allows to select more options.
   * @tparam D the domain type.
   * @tparam C the codomain type.
   * @tparam W the working set type.
   * @return the object for more options to build an `Iteration` object.
   */
  def apply[D: DomainType, C: CodomainType, W[+_D, +_C]: WorkingSetType]() = new Detected[D, C, W]
}
