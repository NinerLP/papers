package ru.niner.oneplusll

import java.util
import scala.util.Random

object MathUtil {
  def getChangePositions(neededAmount : Int, size : Int) : util.List[Int] = {
    val positions = new util.ArrayList[Int]()
    for (i <- 0 until size) positions.add(i)
    util.Collections.shuffle(positions)
    positions.subList(0,neededAmount)
  }

  def getBitChangePositions(neededAmount : Int, size : Int) : util.List[(Int,Int)] = {
    val positions = new util.ArrayList[(Int,Int)]()
    for (i <- 0 until size) {
      for (j <- 0 until 13) {
        positions.add((i,j))
      }
    }
    util.Collections.shuffle(positions)
    positions.subList(0,neededAmount)
  }


  def getBinomial(n : Int, p : Double): Int = {
    var result = 0
    for (i <- 0 until n) if (Random.nextDouble() < p) result+=1
    Math.max(result,1)
  }

  def nextSuccess(p : Double) : Int = {
    val q = 1-p
    val s = Random.nextDouble()*p
    //println(s)
    // (1-p)^n <= s <= (1-p)^(n-1)
    Math.floor(Math.log(s/p)/Math.log(q)).toInt + 1
  }

  def getFlipPositions(length : Int, p : Double) : util.ArrayList[Int] =  {
    val positions = new util.ArrayList[Int]()
    var pos = nextSuccess(p)
    while (pos < length) {
      positions.add(pos)
      pos = pos + nextSuccess(p)
    }
    if (positions.isEmpty()) {
      // at least one change
      positions.add(Random.nextInt(length))
    }
    positions
  }

  def getPairSet(total : Int, target : Int) : List[List[Int]] = {
      Random.shuffle(List.range(0,total).combinations(2)).take(target).toList
  }

  def getPath(pLength : Int, vertexNum : Int): List[Int] = {
    val steps = List.range(1,vertexNum-1)
    List(0) ++ Random.shuffle(steps).take(pLength).sorted ++ List(vertexNum-1)

  }
}
