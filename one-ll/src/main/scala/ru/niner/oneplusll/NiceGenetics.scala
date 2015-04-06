package ru.niner.oneplusll

import java.io.{File, PrintWriter}
import java.util
import java.lang.{Long => JLong}
import scala.util.Random

class NiceGenetics(val nodeNumber : Int, val edgeNumber : Int, val maximumCapacity : Int,
                    val generationSize : Int, val crossoverSize : Int, val fitnessFunction: FitnessFunction,
                    val isAcyclic : Boolean, val computationsLimit : Int, runID : Int) {
  val algorithmName = "NiceGenetics"
  var computationsCount = 0
  val parentGraphs : util.ArrayList[Graph] = new util.ArrayList[Graph]()
  val crossoverGraphs : util.ArrayList[Graph] = new util.ArrayList[Graph]()
  val childrenGraphs : util.ArrayList[Graph] = new util.ArrayList[Graph]()
  var log : PrintWriter = null

  private def currentBest() : Long = {
    var best = 0L
    for (i <- 0 until generationSize) {
       if (parentGraphs.get(i).fitnessValue > best) {
         best = parentGraphs.get(i).fitnessValue
       }
    }
    best
  }

  private def init() : Unit = {
    for (i <- 0 until generationSize) {
      parentGraphs.add(Graph.createRandom(nodeNumber,edgeNumber,maximumCapacity,isAcyclic))
      parentGraphs.get(i).computeFitnessValue(fitnessFunction, algorithmName+" parent")
      computationsCount += 1
    }

    val file = new File("logs/" + fitnessFunction.solver.getName + "_" + runID + "_" + algorithmName + ".txt")
    file.createNewFile()
    log = new PrintWriter(file,"UTF-8")

    log.println("computation fitness")
    log.println(computationsCount + " " + currentBest())
  }

  private def tourney() : util.ArrayList[Int] = {
    val pos16 = MathUtil.getChangePositions(16,generationSize)
    val pos8 = new util.ArrayList[Int]()

    for (i <- 0 until 16 by 2) {
      if (parentGraphs.get(pos16.get(i)).fitnessValue >= parentGraphs.get(pos16.get(i)).fitnessValue) {
        if (Random.nextFloat() < 0.9) pos8.add(pos16.get(i)) else pos8.add(pos16.get(i+1))
      } else {
        if (Random.nextFloat() < 0.9) pos8.add(pos16.get(i + 1)) else pos8.add(pos16.get(i))
      }
    }

    val pos4 = new util.ArrayList[Int]()

    for (i <- 0 until 8 by 2) {
      if (parentGraphs.get(pos8.get(i)).fitnessValue >= parentGraphs.get(pos8.get(i)).fitnessValue) {
        if (Random.nextFloat() < 0.9) pos4.add(pos8.get(i)) else pos4.add(pos8.get(i+1))
      } else {
        if (Random.nextFloat() < 0.9) pos4.add(pos8.get(i + 1)) else pos4.add(pos8.get(i))
      }
    }

    val pos2 = new util.ArrayList[Int]()
    for (i <- 0 until 4 by 2) {
      if (parentGraphs.get(pos4.get(i)).fitnessValue >= parentGraphs.get(pos4.get(i)).fitnessValue) {
        if (Random.nextFloat() < 0.9) pos2.add(pos4.get(i)) else pos2.add(pos4.get(i+1))
      } else {
        if (Random.nextFloat() < 0.9) pos2.add(pos4.get(i + 1)) else pos2.add(pos4.get(i))
      }
    }
    pos2
  }

  private def crossover() : Unit = {
    crossoverGraphs.clear()
    for (i <- 0 until crossoverSize) {
      val pos = tourney()
      val graphA = parentGraphs.get(pos.get(0))
      val graphB = parentGraphs.get(pos.get(1))
      val l = Random.nextInt(edgeNumber-1) + 1
      crossoverGraphs.add(Graph.singleCross(graphA,graphB,l))
      crossoverGraphs.add(Graph.singleCross(graphB,graphA,l))
    }
  }

  private def mutate() : Unit = {
    childrenGraphs.clear()
    for (i <- 0 until crossoverGraphs.size()) {
      val numberToMutate = MathUtil.getBinomial(edgeNumber,1.0/edgeNumber)
      childrenGraphs.add(Graph.mutate(crossoverGraphs.get(i),numberToMutate,isAcyclic))
      childrenGraphs.get(i).computeFitnessValue(fitnessFunction,algorithmName + " child")
      computationsCount += 1
    }
  }

  private def addSurvivors() : Unit = {
    for (i <- 0 until (generationSize - crossoverSize * 2)) {
      //get best parent - add and remove
      var best = 0L
      var bestId = -1

      for (k <- 0 until parentGraphs.size()) {
        if (parentGraphs.get(k).fitnessValue >= best) {
          best = parentGraphs.get(k).fitnessValue
          bestId = k
        }
      }

      childrenGraphs.add(parentGraphs.get(bestId))
      parentGraphs.remove(bestId)
    }
  }

  private def transfer() : Unit = {
    parentGraphs.clear()
    for (i <- 0 until generationSize) {
      parentGraphs.add(childrenGraphs.get(i))
    }
    log.println(computationsCount + " " + currentBest())
  }

  def run() : Unit = {
    init()
    while((currentBest() < JLong.MAX_VALUE)
      && (fitnessFunction.target == -1 || currentBest() < fitnessFunction.target)
      && (computationsCount < computationsLimit)) {
      crossover()
      mutate()
      addSurvivors()
      transfer()
    }
    log.close()
    fitnessFunction.dumpResults(runID)
  }
}
