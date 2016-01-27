package ru.niner.oneplusll.matrix

import java.io.{File, PrintWriter}
import java.lang.{Long => JLong}

import ru.niner.oneplusll.{MathUtil}

class MatrixOnePlusOne(val nodeNumber : Int, val maximumCapacity : Int,
                 val fitnessFunction : NGPMatrixFitness,
                 val computationsLimit : Int, runID : Int) {
  val algorithmName = "1+1"
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
    //println("Mutating")
    val len = parentGraph.capacities.size()// * (parentGraph.capacities.size() + 1) / 2
    val numberToMutate = MathUtil.getBinomial(len, 1.0/len)
    //println(len + " " + numberToMutate)
    mutantGraph = MatrixGraph.mutate(parentGraph, numberToMutate)
    mutantGraph.computeFitnessValue(fitnessFunction, algorithmName + " mutant")
    computationsCount += 1
  }

  private def select(): Unit = {
    //println("Selecting")
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
