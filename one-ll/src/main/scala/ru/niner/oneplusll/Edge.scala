package ru.niner.oneplusll

import scala.util.Random

class Edge(var start : Int , var end : Int, var capacity : Int) {
  def mutate(nodeNumber : Int, maxCapacity : Int, isAcyclic : Boolean) = {
    /*start = if (isAcyclic) Random.nextInt(nodeNumber - 1) else Random.nextInt(nodeNumber)
    end = if (isAcyclic) Random.nextInt(nodeNumber - start - 1) + start + 1 else Random.nextInt(nodeNumber); */
    val (_start, _end) = Edge.getStartEnd(nodeNumber,isAcyclic)
    start = _start
    end = _end
    capacity = Random.nextInt(maxCapacity) + 1
  }
}

object Edge {
  def randomEdge(nodeNumber : Int, maxCapacity : Int, isAcyclic : Boolean) : Edge = {
    //val start = if (isAcyclic) Random.nextInt(nodeNumber - 1) else Random.nextInt(nodeNumber - 1)
    //val end = if (isAcyclic) Random.nextInt(nodeNumber - start - 1) + start + 1 else Random.nextInt(nodeNumber);
    val (start, end) = getStartEnd(nodeNumber,isAcyclic)
    val capacity = Random.nextInt(maxCapacity) + 1
    new Edge(start,end,capacity)
  }

  def getStartEnd(nodeNumber : Int, isAcyclic : Boolean) : (Int, Int) = {
    val a = Random.nextInt(nodeNumber)
    val b = Random.nextInt(nodeNumber)
    if (isAcyclic) (Math.min(a,b),Math.max(a,b)) else (a,b)
  }
}
