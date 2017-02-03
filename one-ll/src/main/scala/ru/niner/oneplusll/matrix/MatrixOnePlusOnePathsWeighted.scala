package ru.niner.oneplusll.matrix

import java.io.{File, PrintWriter}
import java.lang.{Long => JLong}

import scala.util.Random

class MatrixOnePlusOnePathsWeighted(val nodeNumber : Int, val maximumCapacity : Int,
                                    val fitnessFunction : NGPMatrixFitness,
                                    val computationsLimit : Int, runID : Int) {
  val algorithmName = "1+1 PathsWeighted"
  var computationsCount = 0
  var parentGraph : MatrixGraph = null
  var mutantGraph : MatrixGraph = null
  var mutantLength : Int = 0
  var log : PrintWriter = null

  private def init() : Unit = {
    parentGraph = MatrixGraph.createRandom(nodeNumber, maximumCapacity)
    parentGraph.computeFitnessValue(fitnessFunction, algorithmName + " parent")
    computationsCount += 1

    val file = new File("logs/"+ fitnessFunction.solver.getName + "_" + runID + "_" + algorithmName +".txt")
    file.createNewFile()
    log = new PrintWriter(file,"UTF-8")

    log.println("computation fitness length")
    log.println(computationsCount + " " + parentGraph.fitnessValue)
  }

  var smallCount = 0
  var smallSucc = 0
  var bigCount = 0
  var bigSucc = 0
  def lengthFun() : Int = {
    val smallP = 1.0 * smallSucc / smallCount
    val bigP = 1.0 * bigSucc / bigCount

    if (smallP > bigP) {
      smallCount += 1
      Random.nextInt(10)+1
    } else if (smallP < bigP) {
      bigCount += 1
      Random.nextInt(9)+90
    } else {
      if (Random.nextBoolean()) {
        smallCount += 1
        Random.nextInt(10)+1
      } else {
        bigCount += 1
        Random.nextInt(9)+90
      }
    }
  }

  def lengthUCB1() : Int = {
    if (smallCount == 0) {
      smallCount += 1
      Random.nextInt(10)+1
    } else if (bigCount == 0) {
      bigCount += 1
      Random.nextInt(9)+90
    } else {
      val totalCount = smallCount+bigCount
      val smallUCB = 1.0 * smallSucc / smallCount + Math.sqrt(2.0 * Math.log(totalCount) / smallCount)
      val bigUCB = 1.0 * bigSucc / bigCount + Math.sqrt(2.0 * Math.log(totalCount) / bigCount)

      if (smallUCB > bigUCB) {
        smallCount += 1
        Random.nextInt(10)+1
      } else if (smallUCB < bigUCB) {
        bigCount += 1
        Random.nextInt(9)+90
      } else {
        if (Random.nextBoolean()) {
          smallCount += 1
          Random.nextInt(10)+1
        } else {
          bigCount += 1
          Random.nextInt(9)+90
        }
      }
    }

  }

  private def mutate() : Unit = {
    val numberToMutate = 1
    val res = MatrixGraph.pathMutate(parentGraph, numberToMutate,lengthUCB1)
    mutantGraph = res._1
    mutantLength = res._2.get(0).edgeIndices.size()
    mutantGraph.computeFitnessValue(fitnessFunction, algorithmName + " mutant")
    computationsCount += 1
  }

  private def select(): Unit = {
    if (mutantGraph.fitnessValue > parentGraph.fitnessValue) {
      log.println(computationsCount + " " + mutantGraph.fitnessValue + " " + mutantLength)
      if (mutantLength < 50) {
        smallSucc += 1
      } else {
        bigSucc += 1
      }
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
        println("Run " + runID + " " + computationsCount + " computations: " + parentGraph.fitnessValue + s"/$smallSucc/$smallCount//$bigSucc/$bigCount")
      }
    }
    log.println(computationsCount + " " + parentGraph.fitnessValue)
    log.println(s"Small: $smallSucc over $smallCount")
    log.println(s"Big: $bigSucc over $bigCount")
    log.close()
    fitnessFunction.dumpResults(runID)
  }
}
