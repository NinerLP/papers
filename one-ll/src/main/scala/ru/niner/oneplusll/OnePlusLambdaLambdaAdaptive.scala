package ru.niner.oneplusll

import java.io.{File, PrintWriter}
import java.lang.{Long => JLong}
import java.util

class OnePlusLambdaLambdaAdaptive(val nodeNumber : Int, val edgeNumber : Int, val maximumCapacity : Int,
                                      val adaptationCoefficient : Double, val fitnessFunction : FitnessFunction,
                                      val isAcyclic : Boolean, val computationsLimit : Int, runID : Int) {
  val algorithmName = "1+LL Adaptive"
  var computationsCount = 0
  var parentGraph : Graph = null
  val mutantGraphs : util.ArrayList[Graph] = new util.ArrayList[Graph]()
  val crossoverGraphs : util.ArrayList[Graph] = new util.ArrayList[Graph]()
  var log : PrintWriter = null
  var lambdaPar = 1.0


  private def init(): Unit = {
    parentGraph = Graph.createRandom(nodeNumber, edgeNumber, maximumCapacity, isAcyclic)
    parentGraph.computeFitnessValue(fitnessFunction, algorithmName + " parent")
    computationsCount += 1

    val file = new File("logs/" + fitnessFunction.solver.getName + "_" + runID + "_" + algorithmName + ".txt")
    file.createNewFile()
    log = new PrintWriter(file,"UTF-8")

    log.println("computation fitness lambda usedlambda")
    log.println(computationsCount + " " + parentGraph.fitnessValue + " " + lambdaPar + " " + Math.max(lambdaPar.toInt,1))
  }

  private def mutate(): Unit = {
    val numberToMutate = MathUtil.getBinomial(edgeNumber, 1.0 * Math.max(lambdaPar.toInt,1) / edgeNumber)

    mutantGraphs.clear()
    for (i <- 0 until Math.max(lambdaPar.toInt,1)) {
      mutantGraphs.add(Graph.mutate(parentGraph,numberToMutate,isAcyclic))
      mutantGraphs.get(i).computeFitnessValue(fitnessFunction,algorithmName + " mutant")
      computationsCount += 1
    }
  }

  private def cross(): Unit = {
    var bestFitnessValue = 0L
    var bestMutantGraph : Graph = null

    for (i <- 0 until Math.max(lambdaPar.toInt,1)) {
      val fitnessValue = mutantGraphs.get(i).fitnessValue

      if (fitnessValue >= bestFitnessValue) {
        bestMutantGraph = mutantGraphs.get(i)
        bestFitnessValue = fitnessValue
      }
    }

    crossoverGraphs.clear()
    for (i <- 0 until Math.max(lambdaPar.toInt,1)) {
      crossoverGraphs.add(Graph.cross(bestMutantGraph, parentGraph, 1.0 / Math.max(lambdaPar.toInt,1)))
      crossoverGraphs.get(i).computeFitnessValue(fitnessFunction, algorithmName + " crossover")
      computationsCount += 1
    }
  }

  private def select(): Unit = {
    var bestFitnessValue = 0L
    var bestCrossoverGraph : Graph = null

    for (i <- 0 until Math.max(lambdaPar.toInt,1)) {
      val fitnessValue = crossoverGraphs.get(i).fitnessValue

      if (fitnessValue >= bestFitnessValue) {
        bestCrossoverGraph = crossoverGraphs.get(i)
        bestFitnessValue = fitnessValue
      }
    }

    if (bestCrossoverGraph.fitnessValue > parentGraph.fitnessValue) {
      log.println(computationsCount + " " + bestCrossoverGraph.fitnessValue + " " + lambdaPar + " " + Math.max(lambdaPar.toInt,1))
      lambdaPar = lambdaPar / adaptationCoefficient
    } else {
      lambdaPar = Math.pow(adaptationCoefficient,0.25) * lambdaPar
    }
    if (bestCrossoverGraph.fitnessValue >= parentGraph.fitnessValue) parentGraph = bestCrossoverGraph
  }

  def run(): Unit = {
    init()
    while((parentGraph.fitnessValue < JLong.MAX_VALUE)
      && (fitnessFunction.target == -1 || parentGraph.fitnessValue < fitnessFunction.target)
      && (computationsCount < computationsLimit)) {
      mutate()
      cross()
      select()
    }
    log.println(computationsCount + " " + parentGraph.fitnessValue + " " + lambdaPar + " " + Math.max(lambdaPar.toInt,1))
    log.close()
    fitnessFunction.dumpResults(runID)
  }

}
