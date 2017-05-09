package ru.niner.oneplusll.matrix

import java.io.{File, PrintWriter}
import java.lang.{Long => JLong}

import ru.niner.oneplusll.{MathUtil}

class MatrixFastOnePlusOne(val nodeNumber : Int, val maximumCapacity : Int,
                       val fitnessFunction : NGPMatrixFitness,
                       val computationsLimit : Int, val beta : Double,
                           runID : Int) {
  val algorithmName = s"Fast1+1+$beta"
  var computationsCount = 0
  var parentGraph : MatrixGraph = null
  var mutantGraph : MatrixGraph = null
  var log : PrintWriter = null

  private def init() : Unit = {
    parentGraph = MatrixGraph.createRandom(nodeNumber, maximumCapacity)

    parentGraph.computeFitnessValue(fitnessFunction, algorithmName + " parent")
    computationsCount += 1

    val file = new File("logs/"+ fitnessFunction.solver.getName + "_" + runID + "_" + algorithmName +".txt")
    file.createNewFile()
    log = new PrintWriter(file,"UTF-8")

    log.println("computation fitness")
    log.println(computationsCount + " " + parentGraph.fitnessValue)
  }

  private def mutate() : Unit = {
    mutantGraph = MatrixGraph.fastMutate(parentGraph, beta)
    mutantGraph.computeFitnessValue(fitnessFunction, algorithmName + " mutant")
    computationsCount += 1
  }

  private def select(): Unit = {
    if (mutantGraph.fitnessValue > parentGraph.fitnessValue) {
      log.println(computationsCount + " " + mutantGraph.fitnessValue)
      println("Run " + runID + " got improvement!")
    }
    if (mutantGraph.fitnessValue >= parentGraph.fitnessValue) parentGraph = mutantGraph
  }

  def run(): Unit = {
    init()
    while((parentGraph.fitnessValue < JLong.MAX_VALUE)
      && (fitnessFunction.target == -1 || parentGraph.fitnessValue < fitnessFunction.target)
      && (computationsCount < computationsLimit)) {
      mutate()
      select()
      if (computationsCount % 10000 < 1) {
        println("Run " + runID + " " + computationsCount + " computations")
      }
    }
    log.println(computationsCount + " " + parentGraph.fitnessValue)
    log.close()
    fitnessFunction.dumpResults(runID)
  }
}
