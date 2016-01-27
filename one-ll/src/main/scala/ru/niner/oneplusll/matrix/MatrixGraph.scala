package ru.niner.oneplusll.matrix

import java.util
import java.util.ArrayList

import ru.ifmo.ctd.ngp.demo.testgen.flows.EdgeRec
import ru.ifmo.ctd.ngp.demo.testgen.flows.solvers.{Dinic, ImprovedShortestPath}
import ru.niner.oneplusll.MathUtil

import scala.util.Random

class MatrixGraph private (val nodeNumber : Int, val maximumCapacity : Int) {
  val capacities = new ArrayList[Int]()
  var fitnessValue : Long = 0

  def computeFitnessValue(fitnessFunction: NGPMatrixFitness, algorithmName : String) : Unit = {
    fitnessValue = fitnessFunction.apply(this, algorithmName)
    //println("Computed")

  }

}

object MatrixGraph {

  def createRandom(nodeNumber : Int, maximumCapacity : Int): MatrixGraph = {
    // what exactly
    val mgraph = new MatrixGraph(nodeNumber, maximumCapacity)
    for (i <- 0 until (nodeNumber*(nodeNumber+1)/2)) {
      mgraph.capacities.add(Random.nextInt(maximumCapacity))
    }
    mgraph
  }

  def mutate(source : MatrixGraph, numberToMutate : Int) : MatrixGraph = {
    val mgraph = new MatrixGraph(source.nodeNumber, source.maximumCapacity)
    val positions = MathUtil.getChangePositions(numberToMutate, source.capacities.size)

    /*for (i <- 0 until source.capacities.size) {
      mgraph.capacities.add(source.capacities.get(i))
    }*/
    mgraph.capacities.addAll(source.capacities)

    for (i <- 0 until positions.size) {
      mgraph.capacities.set(positions.get(i),Random.nextInt(source.maximumCapacity))
    }

    mgraph
  }

  def uniformCross(mgraphA : MatrixGraph, mgraphB : MatrixGraph, probA : Double) : MatrixGraph = {
    val mgraphC = new MatrixGraph(mgraphA.nodeNumber,mgraphA.maximumCapacity)

    for (i <- 0 until mgraphA.capacities.size()) {
      val tempC = if (Random.nextDouble() < probA) mgraphA.capacities.get(i) else mgraphB.capacities.get(i)
      mgraphC.capacities.add(tempC)
    }

    mgraphC
  }

  def xorProbCross(mgraphA : MatrixGraph, mgraphB : MatrixGraph, probA : Double) : MatrixGraph = {
    val mgraphC = new MatrixGraph(mgraphA.nodeNumber,mgraphA.maximumCapacity)

    for (i <- 0 until mgraphA.capacities.size()) {
      val tempC = if (Random.nextDouble() < probA) mgraphA.capacities.get(i) else Math.max(0, Math.min(mgraphA.maximumCapacity,mgraphA.capacities.get(i) ^ mgraphB.capacities.get(i) ))
      mgraphC.capacities.add(tempC)
    }

    mgraphC
  }

  def xorCross(mgraphA : MatrixGraph, mgraphB : MatrixGraph, probA : Double) : MatrixGraph = {
    val mgraphC = new MatrixGraph(mgraphA.nodeNumber,mgraphA.maximumCapacity)

    for (i <- 0 until mgraphA.capacities.size()) {
      val tempC = Math.max(0, Math.min(mgraphA.maximumCapacity,mgraphA.capacities.get(i) ^ mgraphB.capacities.get(i) ))
      mgraphC.capacities.add(tempC)
    }

    mgraphC
  }

  def singleCross(mgraphA : MatrixGraph, mgraphB : MatrixGraph, l : Int): MatrixGraph = {
    val mgraphC = new MatrixGraph(mgraphA.nodeNumber, mgraphA.maximumCapacity)

    /*for (i <- 0 until l) {
      mgraphC.capacities.add(mgraphA.capacities.get(i))
    }*/

    mgraphC.capacities.addAll(mgraphA.capacities.subList(0,l))
    /*for (i <- l until mgraphA.nodeNumber) {
      mgraphC.capacities.add(mgraphB.capacities.get(i))
    }*/
    mgraphC.capacities.addAll(mgraphB.capacities.subList(l,mgraphB.capacities.size()))

    mgraphC
  }
}

object Test extends App {

  //new MatrixOnePlusOne(100,10000,new NGPMatrixFitness(new Dinic), 500, 1).run()
  new MatrixOnePlusLambdaLambdaXOR(100,10000, 8, 1.0*8/5050, 1.0/8, new NGPMatrixFitness(new Dinic), 20000, 1).run()
  /*runs.add(new OnePlusLambdaLambdaRunnable(config.maxV,config.maxE, config.maxC, lambda,
              1.0*lambda/config.maxE, 1.0/lambda, new NGPAlgorithmFitness(new Dinic()),
              config.acyclic, config.cLimit, idAssigner.getNextID))*/
}