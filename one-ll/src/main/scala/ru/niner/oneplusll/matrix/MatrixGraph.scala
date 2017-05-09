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

  def computeFitnessValue(fitnessFunction: NGPMatrixFitness, algorithmName : String) : Long = {
    fitnessValue = fitnessFunction.apply(this, algorithmName)
    fitnessValue
  }

}

object MatrixGraph {

  def createRandom(nodeNumber : Int, maximumCapacity : Int): MatrixGraph = {

    val mgraph = new MatrixGraph(nodeNumber, maximumCapacity)
    for (i <- 0 until (nodeNumber*(nodeNumber-1)/2)) {
      mgraph.capacities.add(Random.nextInt(maximumCapacity))
    }
    mgraph
  }

  def mutate(source : MatrixGraph, numberToMutate : Int) : MatrixGraph = {
    val mgraph = new MatrixGraph(source.nodeNumber, source.maximumCapacity)
    val positions = MathUtil.getChangePositions(numberToMutate, source.capacities.size)

    mgraph.capacities.addAll(source.capacities)

    for (i <- 0 until positions.size) {
      mgraph.capacities.set(positions.get(i),Random.nextInt(source.maximumCapacity))
    }

    mgraph
  }

  def fastMutate(source : MatrixGraph, beta : Double) : MatrixGraph = {
    val mgraph = new MatrixGraph(source.nodeNumber, source.maximumCapacity)

    val len = source.capacities.size()
    val fastNumber = MathUtil.getZipf(1, len, beta)
    val numberToMutate = MathUtil.getBinomial(len, 1.0*fastNumber/len)
    val positions = MathUtil.getChangePositions(numberToMutate, source.capacities.size)
    //println(numberToMutate + " " + 1.0*fastNumber/len)

    mgraph.capacities.addAll(source.capacities)

    for (i <- 0 until positions.size) {
      mgraph.capacities.set(positions.get(i),Random.nextInt(source.maximumCapacity))
    }

    mgraph
  }

  def flipMutate(source : MatrixGraph, prob : Double) : MatrixGraph = {
    val mgraph = new MatrixGraph(source.nodeNumber, source.maximumCapacity)
    val positions = MathUtil.getFlipPositions(source.capacities.size, prob)

    mgraph.capacities.addAll(source.capacities)

    for (i <- 0 until positions.size) {
      mgraph.capacities.set(positions.get(i), source.capacities.get(i) ^ (1 << Random.nextInt(13)))
    }
    mgraph
  }

  def flipMutateFixed(source : MatrixGraph, amount : Int) : MatrixGraph = {
    val mgraph = new MatrixGraph(source.nodeNumber, source.maximumCapacity)
    val positions = MathUtil.getBitChangePositions(amount,source.capacities.size)

    mgraph.capacities.addAll(source.capacities)

    for (i <- 0 until positions.size) {
      val pos = positions.get(i)._1
      val offset = positions.get(i)._2
      mgraph.capacities.set(pos, source.capacities.get(pos) ^ (1 << offset))
    }
    mgraph
  }


  def uniformCross(mgraphA : MatrixGraph, mgraphB : MatrixGraph, probA : Double) : (MatrixGraph, MatrixGraph) = {
    val mgraphC = new MatrixGraph(mgraphA.nodeNumber,mgraphA.maximumCapacity)
    val mgraphD = new MatrixGraph(mgraphA.nodeNumber,mgraphA.maximumCapacity)

    for (i <- 0 until mgraphA.capacities.size()) {
      val tempA = mgraphA.capacities.get(i)
      val tempB = mgraphB.capacities.get(i)

      if (Random.nextDouble() < probA) {
        mgraphC.capacities.add(tempA)
        mgraphD.capacities.add(tempB)
      } else {
        mgraphC.capacities.add(tempB)
        mgraphD.capacities.add(tempA)
      }

    }

    (mgraphC, mgraphD)
  }

  def uniformBSCross(a : String, b : String, probA : Double) : Int = {
    val builder = StringBuilder.newBuilder
    val aA = "0"*(13-a.length())+a
    val bB = "0"*(13-b.length())+b
    for (i <- 0 until aA.length) {
      builder.append(if (Random.nextDouble() < probA) aA.charAt(i) else bB.charAt(i))
    }

    Integer.parseInt(builder.toString(),2)
  }

  def uniformBitCross(mgraphA : MatrixGraph, mgraphB : MatrixGraph, probA : Double) : MatrixGraph = {
   val mgraphC = new MatrixGraph(mgraphA.nodeNumber, mgraphA.maximumCapacity)

   for (i <- 0 until mgraphA.capacities.size()) {
   	if (mgraphA.capacities.get(i) != mgraphB.capacities.get(i)) {
      val aBin = Integer.toBinaryString(mgraphA.capacities.get(i))
      val bBin = Integer.toBinaryString(mgraphB.capacities.get(i))
      val cBin = uniformBSCross(aBin, bBin, probA)
      mgraphC.capacities.add(cBin)

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

    mgraphC.capacities.addAll(mgraphA.capacities.subList(0,l))
    mgraphC.capacities.addAll(mgraphB.capacities.subList(l,mgraphB.capacities.size()))

    mgraphC
  }


  /*
    paths addons
   */
  def pathLength() : Int = {
    if (Random.nextBoolean()) {
      Random.nextInt(10)+1
    } else {
      Random.nextInt(9)+90
    }

  }

  class MutationPath(val edgeIndices : ArrayList[Int], val delta : Int)

  def pathMutate(source : MatrixGraph, amount : Int, lengthFun : () => Int = pathLength): (MatrixGraph, ArrayList[MutationPath]) = {
    val paths = new ArrayList[MutationPath]()
    val result = new MatrixGraph(source.nodeNumber,source.maximumCapacity)

    for (i <- 0 until source.capacities.size) {
      result.capacities.add(source.capacities.get(i))
    }

    var k = 0
    while (k < amount) {
      val path = MathUtil.getPath(lengthFun(),source.nodeNumber)
      //println(path)
      val edgeIndices = new util.ArrayList[Int]()
      for (i <- 0 until path.size - 1) {
        val from = path(i)
        val to = path(i+1)
        edgeIndices.add(from*(result.nodeNumber-1) - from*(from-1)/2 + to - from - 1)
      }

      var max = -1
      var min = result.maximumCapacity+1
      for (i <- 0 until edgeIndices.size) {
        min = Math.min(result.capacities.get(edgeIndices.get(i)),min)
        max = Math.max(result.capacities.get(edgeIndices.get(i)),max)
      }
      min = -min
      max = result.maximumCapacity - max

      val delta = Random.nextInt(Math.max(max - min,1)) + min
      //println(s"$delta in [$min, $max]")

      if (delta != 0) {
        for (i <- 0 until edgeIndices.size) {
          result.capacities.set(edgeIndices.get(i),result.capacities.get(edgeIndices.get(i))+delta)
        }
        paths.add(new MutationPath(edgeIndices,delta))
        k+=1
      }
    }
    //println(s"did $k paths")
    (result,paths)
  }

  def applyPath(source : MatrixGraph, path: MutationPath): MatrixGraph = {
    val result = new MatrixGraph(source.nodeNumber, source.maximumCapacity)
    for (i <- 0 until source.capacities.size) {
      result.capacities.add(source.capacities.get(i))
    }

    var max = -1
    var min = result.maximumCapacity
    for (i <- 0 until path.edgeIndices.size) {
      min = Math.min(result.capacities.get(path.edgeIndices.get(i)),min)
      max = Math.max(result.capacities.get(path.edgeIndices.get(i)),max)
    }
    min = -min
    max = result.maximumCapacity - max

    val delta = Math.min(max, Math.max(min, path.delta))
    //println(s"$delta in [$min, $max]")

    for (i <- 0 until path.edgeIndices.size) {
      result.capacities.set(path.edgeIndices.get(i),result.capacities.get(path.edgeIndices.get(i))+delta)
    }

    result
  }

  def applyNewPath(source : MatrixGraph, edgeIndices: List[Int]): MatrixGraph = {
    val result = new MatrixGraph(source.nodeNumber, source.maximumCapacity)
    for (i <- 0 until source.capacities.size) {
      result.capacities.add(source.capacities.get(i))
    }

    var max = -1
    var min = result.maximumCapacity
    for (i <- 0 until edgeIndices.size) {
      min = Math.min(result.capacities.get(edgeIndices(i)),min)
      max = Math.max(result.capacities.get(edgeIndices(i)),max)
    }
    min = -min
    max = result.maximumCapacity - max

    val delta = Random.nextInt(Math.max(max - min,1)) + min
    //println(s"$delta in [$min, $max]")

    for (i <- 0 until edgeIndices.size) {
      result.capacities.set(edgeIndices(i),result.capacities.get(edgeIndices(i))+delta)
    }

    result
  }


  def printPath(path : util.ArrayList[Int]) : Unit ={
    for (i <- 0 until path.size) {
      print(s"${path.get(i)} ")
    }
    println()
  }


  def debugMatOut(mgraph : MatrixGraph) : Unit = {
    var processed = 0
    for (start <- 0 until mgraph.nodeNumber-1) {

      for (i <- 1 until mgraph.nodeNumber - start) {
        val cap = mgraph.capacities.get(processed+i-1)
        if (cap >= 0) {
          println(s"$start ${start+i} $cap")
        }
      }
      processed += mgraph.nodeNumber - start - 1
    }
  }


}
