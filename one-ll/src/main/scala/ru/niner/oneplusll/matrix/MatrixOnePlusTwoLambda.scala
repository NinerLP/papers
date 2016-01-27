package ru.niner.oneplusll.matrix


import java.io.{File, PrintWriter}
import java.util
import java.lang.{Long => JLong}

import ru.niner.oneplusll.MathUtil

class MatrixOnePlusTwoLambda(val nodeNumber : Int, val maximumCapacity : Int,
                             val lambda : Int, val mutationProbability : Double, val fitnessFunction : NGPMatrixFitness,
                             val computationsLimit : Int, runID : Int) {
  val algorithmName : String = "1+2*" + lambda + "_" + mutationProbability.formatted("%.4f")
  var computationsCount = 0
  var parentGraph : MatrixGraph = null
  val mutantGraphs : util.ArrayList[MatrixGraph] = new util.ArrayList[MatrixGraph]()
  var log : PrintWriter = null

  private def init(): Unit = {
    parentGraph = MatrixGraph.createRandom(nodeNumber,maximumCapacity)
    parentGraph.computeFitnessValue(fitnessFunction, algorithmName + " parent")
    computationsCount += 1

    val file = new File("logs/"+ fitnessFunction.solver.getName + "_" + runID + "_" + algorithmName + ".txt")
    file.createNewFile()
    log = new PrintWriter(file,"UTF-8")

    log.println("computation fitness")
    log.println(computationsCount + " " + parentGraph.fitnessValue)
  }

  private def mutate(): Unit = {
    val len = parentGraph.capacities.size()
    val numberToMutate = MathUtil.getBinomial(len,mutationProbability)
    mutantGraphs.clear()
    for (i <- 0 until 2*lambda) {
      mutantGraphs.add(MatrixGraph.mutate(parentGraph,numberToMutate))
      mutantGraphs.get(i).computeFitnessValue(fitnessFunction,algorithmName + " mutant")
      computationsCount += 1
    }
  }

  private def select(): Unit = {
    var bestFitnessValue = 0L
    var bestMutantGraph : MatrixGraph = null

    for (i <- 0 until 2*lambda) {
      val fitnessValue = mutantGraphs.get(i).fitnessValue

      if (fitnessValue >= bestFitnessValue) {
        bestMutantGraph = mutantGraphs.get(i)
        bestFitnessValue = fitnessValue
      }
    }

    if (bestMutantGraph.fitnessValue > parentGraph.fitnessValue) {
      log.println(computationsCount + " " + bestMutantGraph.fitnessValue)
      println("Run " + runID + " got improvement!")
    }
    if (bestMutantGraph.fitnessValue >= parentGraph.fitnessValue) parentGraph = bestMutantGraph
  }

  def run(): Unit = {
    init()
    while((parentGraph.fitnessValue < JLong.MAX_VALUE)
      && (fitnessFunction.target == -1 || parentGraph.fitnessValue < fitnessFunction.target)
      && (computationsCount < computationsLimit)) {
      mutate()
      select()
      if (computationsCount % 10000 < 2*lambda) {
        println("Run " + runID + " " + computationsCount + " computations")
      }
    }
    log.println(computationsCount + " " + parentGraph.fitnessValue)
    log.close()
    fitnessFunction.dumpResults(runID)
  }

}
