package ru.niner.oneplusll.matrix

import java.io.{File, PrintWriter}
import java.lang.{Long => JLong}
import java.util

import ru.niner.oneplusll.matrix.MatrixGraph.MutationPath

class MatrixOnePlusLambdaLambdaPathsAdaptive(val nodeNumber : Int, val maximumCapacity : Int,
                                             val lambda : Int, val mutationProbability : Double, val crossoverProbabilityForA : Double,
                                             val fitnessFunction : NGPMatrixFitness, val computationsLimit : Int,
                                             runID : Int) {
    val algorithmName : String = "1+LL_PathsAdaptive"
    var computationsCount = 0
    var parentGraph : MatrixGraph = _
    val mutantGraphs : util.ArrayList[MatrixGraph] = new util.ArrayList[MatrixGraph]()
    val mutantPaths : util.ArrayList[util.ArrayList[MutationPath]] = new util.ArrayList[util.ArrayList[MutationPath]]()
    val crossoverGraphs : util.ArrayList[MatrixGraph] = new util.ArrayList[MatrixGraph]()
    var log : PrintWriter = _
    var lambdaPar = 1.0
    val adaptationCoefficient = 1.5

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
      //val len = parentGraph.capacities.size()
      val numberToMutate = Math.max(lambdaPar.toInt,1)// MathUtil.getBinomial(len, mutationProbability)

      mutantGraphs.clear()
      mutantPaths.clear()
      for (i <- 0 until numberToMutate) {
        val res = MatrixGraph.pathMutate(parentGraph,numberToMutate)
        mutantGraphs.add(res._1)
        mutantPaths.add(res._2)
        mutantGraphs.get(i).computeFitnessValue(fitnessFunction,algorithmName + " mutant")
        computationsCount += 1
      }
    }

    private def cross(): Unit = {
      var bestFitnessValue = 0L
      var bestMutantGraph : MatrixGraph = null
      var bestMutantPaths : util.ArrayList[MutationPath] = null

      for (i <- 0 until Math.max(lambdaPar.toInt,1)) {
        val fitnessValue = mutantGraphs.get(i).fitnessValue

        if (fitnessValue >= bestFitnessValue) {
          bestMutantGraph = mutantGraphs.get(i)
          bestMutantPaths = mutantPaths.get(i)
          bestFitnessValue = fitnessValue
        }
      }

      //MatrixGraph.debugMatOut(bestMutantGraph)
      //println(s"${bestMutantGraph.fitnessValue} fitness")

      crossoverGraphs.clear()
      for (i <- 0 until Math.max(lambdaPar.toInt,1)) {
        crossoverGraphs.add(MatrixGraph.applyPath(bestMutantGraph,bestMutantPaths.get(i)))
        //crossoverGraphs.add(MatrixGraph.uniformCross(bestMutantGraph, parentGraph, crossoverProbabilityForA))
        crossoverGraphs.get(i).computeFitnessValue(fitnessFunction, algorithmName + " crossover")
        computationsCount += 1

        //println()
        //MatrixGraph.printPath(bestMutantPaths.get(i))
        //MatrixGraph.debugMatOut(crossoverGraphs.get(i))
        //println(s"${crossoverGraphs.get(i).fitnessValue} fitness")

        //val a = io.StdIn.readLine()
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
        lambdaPar = Math.min(Math.pow(adaptationCoefficient,0.25) * lambdaPar,100.0)
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
          println("Run " + runID + " " + computationsCount + " computations")
          progress += 10000
        }
      }
      log.println(computationsCount + " " + parentGraph.fitnessValue + " " + lambdaPar + " " + Math.max(lambdaPar.toInt,1))
      log.close()
      fitnessFunction.dumpResults(runID)
    }

  }
