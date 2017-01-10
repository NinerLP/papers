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

  def debugMatOut(mgraph : MatrixGraph) : Unit = {
    var processed = 0
    for (start <- 0 until mgraph.nodeNumber-1) {
      //println(s"Start: $start Processed: $processed Total: ${mgraph.capacities.size()}")
      for (i <- 1 until mgraph.nodeNumber - start) {
        val cap = mgraph.capacities.get(processed+i-1)
        if (cap >= 0) {
          println(s"$start ${start+i} $cap")
        }
      }
      processed += mgraph.nodeNumber - start - 1
    }
  }

  def createRandom(nodeNumber : Int, maximumCapacity : Int): MatrixGraph = {
    // what exactly
    val mgraph = new MatrixGraph(nodeNumber, maximumCapacity)
    for (i <- 0 until (nodeNumber*(nodeNumber-1)/2)) {
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

  def flipMutate(source : MatrixGraph, prob : Double) : MatrixGraph = {
    val positions = MathUtil.getFlipPositions(source.capacities.size, prob)
    //println(positions)
    //println(source.capacities.get(positions.get(0)))
    for (i <- 0 until positions.size) {
      source.capacities.set(positions.get(i), source.capacities.get(i) ^ (1 << Random.nextInt(13)))
    }
    //println(source.capacities.get(positions.get(0)))
    source
  }

  def flipMutateFixed(source : MatrixGraph, amount : Int) : MatrixGraph = {
    val positions = MathUtil.getChangePositions(amount, source.capacities.size)
    for (i <- 0 until positions.size) {
      source.capacities.set(positions.get(i), source.capacities.get(i) ^ (1 << Random.nextInt(13)))
    }
    source
  }

  def pathLength(vertices : Int): Int = {
    var size = 1
    while (size < vertices) {
      if (Random.nextBoolean()) {
        return size
      } else {
        size += 1
      }
    }
    size
  }


  def pathMutate(source : MatrixGraph, amount : Int): (MatrixGraph, ArrayList[ArrayList[Int]]) = {
    val paths = new ArrayList[ArrayList[Int]]()
    val result = new MatrixGraph(source.nodeNumber,source.maximumCapacity)

    for (i <- 0 until source.capacities.size) {
      result.capacities.add(source.capacities.get(i))
    }

    for (i <- 0 until amount) {
      val path = MathUtil.getPath(pathLength(source.nodeNumber),source.nodeNumber)
      //println(path)
      val edgeIndices = new util.ArrayList[Int]()
      for (i <- 0 until path.size - 1) {
        val from = path(i)
        val to = path(i+1)
        edgeIndices.add(from*(result.nodeNumber-1) - from*(from-1)/2 + to - from - 1)
      }

      var max = -1
      var min = result.maximumCapacity
      for (i <- 0 until edgeIndices.size) {
        min = Math.min(result.capacities.get(edgeIndices.get(i)),min)
        max = Math.max(result.capacities.get(edgeIndices.get(i)),max)
      }
      min = -min
      max = result.maximumCapacity - max
      val delta = Random.nextInt(max - min) + min
      //println(s"$delta in [$min, $max]")

      for (i <- 0 until edgeIndices.size) {
        result.capacities.set(edgeIndices.get(i),result.capacities.get(edgeIndices.get(i))+delta)
      }

      paths.add(edgeIndices)
    }
    (result,paths)
  }

  def applyPath(source : MatrixGraph, edgeIndices : util.ArrayList[Int]): MatrixGraph = {
    val result = new MatrixGraph(source.nodeNumber, source.maximumCapacity)
    for (i <- 0 until source.capacities.size) {
      result.capacities.add(source.capacities.get(i))
    }

    var max = -1
    var min = result.maximumCapacity
    for (i <- 0 until edgeIndices.size) {
      min = Math.min(result.capacities.get(edgeIndices.get(i)),min)
      max = Math.max(result.capacities.get(edgeIndices.get(i)),max)
    }
    min = -min
    max = result.maximumCapacity - max
    val delta = Random.nextInt(max - min) + min
    //println(s"$delta in [$min, $max]")

    for (i <- 0 until edgeIndices.size) {
      result.capacities.set(edgeIndices.get(i),result.capacities.get(edgeIndices.get(i))+delta)
    }

    result
  }

  def printPath(path : util.ArrayList[Int]) : Unit ={
    for (i <- 0 until path.size) {
      print(s"${path.get(i)} ")
    }
    println()
  }

  def uniformCross(mgraphA : MatrixGraph, mgraphB : MatrixGraph, probA : Double) : MatrixGraph = {
    val mgraphC = new MatrixGraph(mgraphA.nodeNumber,mgraphA.maximumCapacity)

    for (i <- 0 until mgraphA.capacities.size()) {
      val tempC = if (Random.nextDouble() < probA) mgraphA.capacities.get(i) else mgraphB.capacities.get(i)
      mgraphC.capacities.add(tempC)
    }

    mgraphC
  }

  def uniformBSCross(a : String, b : String, probA : Double) : Int = {
    val builder = StringBuilder.newBuilder

    for (i <- 0 until a.length) {
      builder.append(if (Random.nextDouble() < probA) a.charAt(i) else b.charAt(i))
    }

    Integer.parseInt(builder.toString(),2)
  }

  def uniformBitCross(mgraphA : MatrixGraph, mgraphB : MatrixGraph, probA : Double) : MatrixGraph = {
   val mgraphC = new MatrixGraph(mgraphA.nodeNumber, mgraphA.maximumCapacity)

   for (i <- 0 until mgraphA.capacities.size()) {
   	if (mgraphA.capacities.get(i) != mgraphB.capacities.get(i)) {
      val aBin = Integer.toBinaryString(mgraphA.capacities.get(i))
      val bBin = Integer.toBinaryString(mgraphB.capacities.get(i))

      mgraphC.capacities.add(mgraphA.capacities.get(i))

    } else {
      mgraphC.capacities.add(mgraphA.capacities.get(i))
    }

   }
    mgraphC
  }

  def tripleXor(mgraphA : MatrixGraph, mgraphB : MatrixGraph, mgraphC : MatrixGraph) : MatrixGraph = {
    val mgraphD = new MatrixGraph(mgraphA.nodeNumber, mgraphA.maximumCapacity)

    for (i <- 0 until mgraphA.capacities.size()) {
      val tempD = Math.max(0, Math.min(mgraphA.maximumCapacity,mgraphA.capacities.get(i) ^ mgraphB.capacities.get(i) ^ mgraphC.capacities.get(i)))
      mgraphD.capacities.add(tempD)
    }

    mgraphD
  }


  def xorProbCross(mgraphA : MatrixGraph, mgraphB : MatrixGraph, probA : Double) : MatrixGraph = {
    val mgraphC = new MatrixGraph(mgraphA.nodeNumber,mgraphA.maximumCapacity)

    for (i <- 0 until mgraphA.capacities.size()) {
      val tempC = if (Random.nextDouble() < probA) mgraphA.capacities.get(i) else Math.max(0, Math.min(mgraphA.maximumCapacity,mgraphA.capacities.get(i) ^ mgraphB.capacities.get(i) ))
      mgraphC.capacities.add(tempC)
    }

    mgraphC
  }

  def xorCross(mgraphA : MatrixGraph, mgraphB : MatrixGraph) : MatrixGraph = {
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
