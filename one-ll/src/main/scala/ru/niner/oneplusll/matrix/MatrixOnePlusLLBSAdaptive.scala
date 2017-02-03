package ru.niner.oneplusll.matrix

import java.io.{File, PrintWriter}
import java.lang.{Long => JLong}
import java.util

import ru.niner.oneplusll.MathUtil

class MatrixOnePlusLLBSAdaptive(val nodeNumber : Int, val maximumCapacity : Int,
                                val fitnessFunction : NGPMatrixFitness, val computationsLimit : Int,
                                val adaptationCoefficient : Double,
                                runID : Int) {
    val algorithmName : String = "1+LLBS_Adaptive"
    var computationsCount = 0
    var parentGraph : MatrixGraph = _
    val mutantGraphs : util.ArrayList[MatrixGraph] = new util.ArrayList[MatrixGraph]()
    val crossoverGraphs : util.ArrayList[MatrixGraph] = new util.ArrayList[MatrixGraph]()
    var log : PrintWriter = _
    var lambdaPar = 1.0

    private def init(): Unit = {
      parentGraph = MatrixGraph.createRandom(nodeNumber,maximumCapacity)
      parentGraph.computeFitnessValue(fitnessFunction,algorithmName + " parent")
      computationsCount += 1

      val file = new File("logs/" + fitnessFunction.solver.getName + "_" + runID + "_" + algorithmName + ".txt")
      file.createNewFile()
      log = new PrintWriter(file,"UTF-8")

      log.println("computation fitness lambda usedlambda")
      log.println(computationsCount + " " + parentGraph.fitnessValue + " " + lambdaPar + " " + Math.max(lambdaPar.toInt,1))
    }

    private def mutate(): Unit = {

      val len = 13 * nodeNumber * (nodeNumber-1) / 2
      val numberToMutate = MathUtil.getBinomial(len, 1.0 * Math.max(lambdaPar.toInt,1) / len)// MathUtil.getBinomial(len, mutationProbability)

      mutantGraphs.clear()
      for (i <- 0 until Math.max(lambdaPar.toInt,1)) {
        val res = MatrixGraph.flipMutateFixed(parentGraph,numberToMutate)
        mutantGraphs.add(res)
        mutantGraphs.get(i).computeFitnessValue(fitnessFunction,algorithmName + " mutant")
        computationsCount += 1
      }
    }

    private def cross(): Unit = {
      var bestFitnessValue = 0L
      var bestMutantGraph : MatrixGraph = null

      for (i <- 0 until Math.max(lambdaPar.toInt,1)) {
        val fitnessValue = mutantGraphs.get(i).fitnessValue

        if (fitnessValue >= bestFitnessValue) {
          bestMutantGraph = mutantGraphs.get(i)
          bestFitnessValue = fitnessValue
        }
      }

      crossoverGraphs.clear()
      for (i <- 0 until Math.max(lambdaPar.toInt,1)) {
        crossoverGraphs.add(MatrixGraph.uniformBitCross(bestMutantGraph,parentGraph,  1.0 / Math.max(lambdaPar.toInt,1)))
        crossoverGraphs.get(i).computeFitnessValue(fitnessFunction, algorithmName + " crossover")
        computationsCount += 1
      }
    }

    private def select(): Unit = {
      var bestFitnessValue = 0L
      var bestCrossoverGraph : MatrixGraph = null

      for (i <- 0 until Math.max(lambdaPar.toInt,1)) {
        val fitnessValue = crossoverGraphs.get(i).fitnessValue

        if (fitnessValue >= bestFitnessValue) {
          bestCrossoverGraph = crossoverGraphs.get(i)
          bestFitnessValue = fitnessValue
        }
      }

      if (bestCrossoverGraph.fitnessValue > parentGraph.fitnessValue) {
        log.println(computationsCount + " " + bestCrossoverGraph.fitnessValue + " " + lambdaPar + " " + Math.max(lambdaPar.toInt,1))
        lambdaPar = lambdaPar / adaptationCoefficient
      } else {
        lambdaPar = Math.min(Math.pow(adaptationCoefficient,0.25) * lambdaPar,200.0)
      }

      if (bestCrossoverGraph.fitnessValue >= parentGraph.fitnessValue) parentGraph = bestCrossoverGraph
    }


    def run(): Unit = {
      init()
      var progress = 10000
      while((parentGraph.fitnessValue < JLong.MAX_VALUE)
        && (fitnessFunction.target == -1 || parentGraph.fitnessValue < fitnessFunction.target)
        && (computationsCount < computationsLimit)) {
        mutate()
        cross()
        select()

        if (computationsCount > progress) {
          println("Run " + runID + " " + computationsCount + " computations " + parentGraph.fitnessValue)
          progress += 10000
        }
      }
      log.println(computationsCount + " " + parentGraph.fitnessValue + " " + lambdaPar + " " + Math.max(lambdaPar.toInt,1))
      log.close()
      fitnessFunction.dumpResults(runID)
    }

  }
