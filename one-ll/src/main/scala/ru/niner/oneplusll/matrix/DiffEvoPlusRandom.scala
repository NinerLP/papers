/*
  DiffEvo with improvements
  Make N paired xor crosses from initial population and mutate them
  Choose the best one of them
  XOR cross it with initial population and replace if better
  NOTE: take N different pairs (non repeatable)
*/

package ru.niner.oneplusll.matrix

import java.io.{File, PrintWriter}
import java.util
import java.lang.{Long => JLong}
import ru.niner.oneplusll.MathUtil

import scala.util.Random

class DiffEvoPlusRandom(val nodeNumber : Int, val maximumCapacity : Int,
              val generationSize : Int, val intermediateGenerationSize : Int,  val fitnessFunction : NGPMatrixFitness,
              val computationsLimit : Int, runID : Int) {
      val algorithmName = "DiffEvoPlusRandom"
      var computationsCount = 0
      val parentGraphs : util.ArrayList[MatrixGraph] = new util.ArrayList[MatrixGraph]()
      val intermediateGraphs : util.ArrayList[MatrixGraph] = new util.ArrayList[MatrixGraph]()
      //val finalGraphs : util.ArrayList[MatrixGraph] = new util.ArrayList[MatrixGraph]()
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

      private def getBestIntermediate() : MatrixGraph = {
        var bestGraph : MatrixGraph = intermediateGraphs.get(0)
        var best = intermediateGraphs.get(0).fitnessValue
        for (i <- 1 until intermediateGenerationSize) {
          if (intermediateGraphs.get(i).fitnessValue > best) {
            bestGraph = intermediateGraphs.get(i)
            best = bestGraph.fitnessValue
          }
        }
        bestGraph
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

      private def generateIntermediate() : Unit = {
        intermediateGraphs.clear()
        val positions = MathUtil.getPairSet(generationSize, intermediateGenerationSize)
        val prob = 1.0/parentGraphs.get(0).capacities.size

        for (pos <- positions) {
          intermediateGraphs.add(MatrixGraph.flipMutate(MatrixGraph.xorCross(parentGraphs.get(pos(0)),parentGraphs.get(pos(1))), prob))
        }


        for (i <- 0 until intermediateGraphs.size) {
          intermediateGraphs.get(i).computeFitnessValue(fitnessFunction, algorithmName + " intermediate")
          computationsCount += 1
        }
      }

      private def crossBestWithParents() : Unit = {

        for (i <- 0 until generationSize) {
          val newGraph = MatrixGraph.xorCross(parentGraphs.get(i), intermediateGraphs.get(Random.nextInt(intermediateGenerationSize)))
          newGraph.computeFitnessValue(fitnessFunction, algorithmName + " parentCross")
          computationsCount += 1
          if (newGraph.fitnessValue >= parentGraphs.get(i).fitnessValue) {
            parentGraphs.set(i,newGraph)
          }
        }

        log.println(computationsCount + " " + currentBest)
      }

      def run() : Unit = {
        init()
        while((currentBest() < JLong.MAX_VALUE)
          && (fitnessFunction.target == -1 || currentBest() < fitnessFunction.target)
          && (computationsCount < computationsLimit)) {
          generateIntermediate()
          crossBestWithParents()
        }
        log.close()
        fitnessFunction.dumpResults(runID)
      }

}
