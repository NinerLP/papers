package ru.niner.oneplusll.matrix

import java.io.{File, PrintWriter}
import java.lang.{Long => JLong}
import java.util

import ru.niner.oneplusll.MathUtil

class MatrixOnePlusXOR(val nodeNumber : Int, val maximumCapacity : Int,
                                   val lambda : Int, val mutationProbability : Double, val crossoverProbabilityForA : Double,
                                   val fitnessFunction : NGPMatrixFitness, val computationsLimit : Int,
                                   runID : Int) {
  val algorithmName = "1+XOR"
  var computationsCount = 0
  var parentGraph : MatrixGraph = null
  val mutantGraphs : util.ArrayList[MatrixGraph] = new util.ArrayList[MatrixGraph]()
  val crossoverGraphs : util.ArrayList[MatrixGraph] = new util.ArrayList[MatrixGraph]()
  var log : PrintWriter = null

  private def init(): Unit = {
    parentGraph = MatrixGraph.createRandom(nodeNumber,maximumCapacity)
    parentGraph.computeFitnessValue(fitnessFunction,algorithmName + " parent")
    computationsCount += 1

    val file = new File("logs/" + fitnessFunction.solver.getName + "_" + runID + "_" + algorithmName + ".txt")
    file.createNewFile()
    log = new PrintWriter(file,"UTF-8")

    log.println("computation fitness")
    log.println(computationsCount + " " + parentGraph.fitnessValue)
  }

  private def mutate(): Unit = {
    val len = parentGraph.capacities.size()
    val numberToMutate = MathUtil.getBinomial(len, mutationProbability)

    mutantGraphs.clear()
    for (i <- 0 until lambda) {
      mutantGraphs.add(MatrixGraph.mutate(parentGraph,numberToMutate))
      mutantGraphs.get(i).computeFitnessValue(fitnessFunction,algorithmName + " mutant")
      computationsCount += 1
    }
  }

  private def cross(): Unit = {
    var bestFitnessValue = 0L
    var bestMutantGraph : MatrixGraph = null

    for (i <- 0 until lambda) {
      val fitnessValue = mutantGraphs.get(i).fitnessValue

      if (fitnessValue >= bestFitnessValue) {
        bestMutantGraph = mutantGraphs.get(i)
        bestFitnessValue = fitnessValue
      }
    }

    crossoverGraphs.clear()
    for (i <- 0 until lambda) {
      crossoverGraphs.add(MatrixGraph.xorCross(bestMutantGraph, parentGraph))
      crossoverGraphs.get(i).computeFitnessValue(fitnessFunction, algorithmName + " crossover")
      computationsCount += 1
    }
  }

  private def select(): Unit = {
    var bestFitnessValue = 0L
    var bestCrossoverGraph : MatrixGraph = null

    for (i <- 0 until lambda) {
      val fitnessValue = crossoverGraphs.get(i).fitnessValue

      if (fitnessValue >= bestFitnessValue) {
        bestCrossoverGraph = crossoverGraphs.get(i)
        bestFitnessValue = fitnessValue
      }
    }

    if (bestCrossoverGraph.fitnessValue > parentGraph.fitnessValue) {
      log.println(computationsCount + " " + bestCrossoverGraph.fitnessValue)
      println("Run " + runID + " got improvement!")
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
      if (computationsCount % 10000 < 2*lambda) {
        println("Run " + runID + " " + computationsCount + " computations")
      }
    }
    log.println(computationsCount + " " + parentGraph.fitnessValue)
    log.close()
    fitnessFunction.dumpResults(runID)
  }

}
