package ru.niner.oneplusll.matrix

import java.io.{File, PrintWriter}
import java.util
import java.lang.{Long => JLong}
import ru.niner.oneplusll.MathUtil

import scala.util.Random

class DiffEvo(val nodeNumber : Int, val maximumCapacity : Int,
              val generationSize : Int, val fitnessFunction : NGPMatrixFitness,
              val computationsLimit : Int, runID : Int) {
      val algorithmName = "DiffEvo10"
      var computationsCount = 0
      val parentGraphs : util.ArrayList[MatrixGraph] = new util.ArrayList[MatrixGraph]()
      val childrenGraphs : util.ArrayList[MatrixGraph] = new util.ArrayList[MatrixGraph]()
      val transferGraphs : util.ArrayList[MatrixGraph] = new util.ArrayList[MatrixGraph]()
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
          parentGraphs.add(MatrixGraph.createRandom(nodeNumber, maximumCapacity))
          parentGraphs.get(i).computeFitnessValue(fitnessFunction, algorithmName+" parent")
          computationsCount += 1
        }

        val file = new File("logs/" + fitnessFunction.solver.getName + "_" + runID + "_" + algorithmName + ".txt")
        file.createNewFile()
        log = new PrintWriter(file, "UTF-8")

        log.println("computation fitness")
        log.println(computationsCount + " " + currentBest())
      }

      private def generateNext() : Unit = {
        childrenGraphs.clear()

        for (i <- 0 until generationSize) {
          val pos = MathUtil.getChangePositions(3,generationSize)
          val prob = 1.0/parentGraphs.get(0).capacities.size
          childrenGraphs.add(MatrixGraph.flipMutate(MatrixGraph.tripleXor(parentGraphs.get(pos.get(0)),parentGraphs.get(pos.get(1)),parentGraphs.get(pos.get(2))), prob))
          childrenGraphs.get(i).computeFitnessValue(fitnessFunction, algorithmName + " child")
          computationsCount += 1
        }

      }

      private def transfer() : Unit = {
        transferGraphs.clear()

        for (i <- 0 until generationSize) {
          if (childrenGraphs.get(i).fitnessValue >= parentGraphs.get(i).fitnessValue) {
            transferGraphs.add(childrenGraphs.get(i))
          } else {
            transferGraphs.add(parentGraphs.get(i))
          }
        }

        parentGraphs.clear()
        for (i <- 0 until generationSize) {
          parentGraphs.add(transferGraphs.get(i))
        }

        log.println(computationsCount + " " + currentBest)
      }

      def run() : Unit = {
        init()
        while((currentBest() < JLong.MAX_VALUE)
          && (fitnessFunction.target == -1 || currentBest() < fitnessFunction.target)
          && (computationsCount < computationsLimit)) {
          generateNext()
          transfer()
        }
        log.close()
        fitnessFunction.dumpResults(runID)
      }

}
