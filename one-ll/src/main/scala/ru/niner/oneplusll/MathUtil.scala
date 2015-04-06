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

  def getBinomial(n : Int, p : Double): Int = {
    var result = 0
    for (i <- 0 until n) if (Random.nextDouble() < p) result+=1
    Math.max(result,1)
  }
}
