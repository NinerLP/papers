package ru.niner.oneplusll

import java.io.{File, PrintWriter}
import java.lang.{Long => JLong}

class OnePlusOne(val nodeNumber : Int, val edgeNumber : Int, val maximumCapacity : Int,
                 val fitnessFunction : FitnessFunction, val isAcyclic : Boolean,
                 val computationsLimit : Int, runID : Int) {
  val algorithmName = "1+1"
  var computationsCount = 0
  var parentGraph : Graph = null
  var mutantGraph : Graph = null
  var log : PrintWriter = null

  private def init() : Unit = {
    parentGraph = Graph.createRandom(nodeNumber, edgeNumber, maximumCapacity, isAcyclic)
    parentGraph.computeFitnessValue(fitnessFunction, algorithmName + " parent")
    computationsCount += 1

    val file = new File("logs/"+ fitnessFunction.solver.getName + "_" + runID + "_" + algorithmName +".txt")
    file.createNewFile()
    log = new PrintWriter(file,"UTF-8")

    log.println("computation fitness")
    log.println(computationsCount + " " + parentGraph.fitnessValue)
  }

  private def mutate() : Unit = {
    val numberToMutate = MathUtil.getBinomial(edgeNumber, 1.0/edgeNumber)
    mutantGraph = Graph.mutate(parentGraph, numberToMutate, isAcyclic)
    mutantGraph.computeFitnessValue(fitnessFunction, algorithmName + " mutant")
    computationsCount += 1
  }

  private def select(): Unit = {
    if (mutantGraph.fitnessValue > parentGraph.fitnessValue) log.println(computationsCount + " " + mutantGraph.fitnessValue)
    if (mutantGraph.fitnessValue >= parentGraph.fitnessValue) parentGraph = mutantGraph
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
