package ru.niner.oneplusll

import java.io.{File, PrintWriter}
import java.util
import java.lang.{Long => JLong}

class OnePlusTwoLambda(val nodeNumber : Int, val edgeNumber : Int, val maximumCapacity : Int,
                       val lambda : Int, val mutationProbability : Double, val fitnessFunction : FitnessFunction,
                       val isAcyclic : Boolean, val computationsLimit : Int, runID : Int) {
  val algorithmName : String = "1+2*" + lambda + "_" + mutationProbability.formatted("%.4f")
  var computationsCount = 0
  var parentGraph : Graph = null
  val mutantGraphs : util.ArrayList[Graph] = new util.ArrayList[Graph]()
  var log : PrintWriter = null

  private def init(): Unit = {
    parentGraph = Graph.createRandom(nodeNumber,edgeNumber,maximumCapacity,isAcyclic)
    parentGraph.computeFitnessValue(fitnessFunction, algorithmName + " parent")
    computationsCount += 1

    val file = new File("logs/"+ fitnessFunction.solver.getName + "_" + runID + "_" + algorithmName + ".txt")
    file.createNewFile()
    log = new PrintWriter(file,"UTF-8")

    log.println("computation fitness")
    log.println(computationsCount + " " + parentGraph.fitnessValue)
  }

  private def mutate(): Unit = {
    val numberToMutate = MathUtil.getBinomial(edgeNumber,mutationProbability)
    mutantGraphs.clear()
    for (i <- 0 until 2*lambda) {
      mutantGraphs.add(Graph.mutate(parentGraph,numberToMutate,isAcyclic))
      mutantGraphs.get(i).computeFitnessValue(fitnessFunction,algorithmName + " mutant")
      computationsCount += 1
    }
  }

  private def select(): Unit = {
    var bestFitnessValue = 0L
    var bestMutantGraph : Graph = null

    for (i <- 0 until 2*lambda) {
      val fitnessValue = mutantGraphs.get(i).fitnessValue

      if (fitnessValue >= bestFitnessValue) {
        bestMutantGraph = mutantGraphs.get(i)
        bestFitnessValue = fitnessValue
      }
    }

    if (bestMutantGraph.fitnessValue > parentGraph.fitnessValue) log.println(computationsCount + " " + bestMutantGraph.fitnessValue)
    if (bestMutantGraph.fitnessValue >= parentGraph.fitnessValue) parentGraph = bestMutantGraph
  }

  def run(): Unit = {
    init()
    while((parentGraph.fitnessValue < JLong.MAX_VALUE)
      && (fitnessFunction.target == -1 || parentGraph.fitnessValue < fitnessFunction.target)
      && (computationsCount < computationsLimit)) {
      mutate()
      select()
    }
    log.println(computationsCount + " " + parentGraph.fitnessValue)
    log.close()
    fitnessFunction.dumpResults(runID)
  }

}
