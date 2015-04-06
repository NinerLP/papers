package ru.niner.oneplusll

import java.util.ArrayList

import scala.util.Random

class Graph private (val nodeNumber : Int, val edgeNumber : Int, val maximumCapacity : Int){
  val edges = new ArrayList[Edge]()
  var fitnessValue : Long = 0

  def computeFitnessValue(fitnessFunction : FitnessFunction, AlgorithmName : String): Unit = {
    fitnessValue = fitnessFunction.apply(this, AlgorithmName)
  }

  def getEdge(i : Int) : Edge = edges.get(i)
}

object Graph {
  def createRandom(nodeNumber : Int, edgeNumber : Int, maximumCapacity : Int, isAcyclic : Boolean) : Graph = {
    val graph = new Graph(nodeNumber,edgeNumber,maximumCapacity)
    for (i <- 0 until edgeNumber) graph.edges.add(Edge.randomEdge(nodeNumber,maximumCapacity,isAcyclic))
    graph
  }

  def mutate(source : Graph, numberToMutate : Int, isAcyclic : Boolean) : Graph = {
    val graph = new Graph(source.nodeNumber,source.edgeNumber,source.maximumCapacity)
    val positions = MathUtil.getChangePositions(numberToMutate,graph.edgeNumber)

    for (i <- 0 until graph.edgeNumber) {
      val tempEdge = source.edges.get(i)
      graph.edges.add(new Edge(tempEdge.start, tempEdge.end, tempEdge.capacity))
    }

    for (i <- 0 until positions.size()) graph.edges.get(positions.get(i)).mutate(graph.nodeNumber,graph.maximumCapacity,isAcyclic)
    graph
  }

  def cross(graphA : Graph, graphB : Graph, probA : Double): Graph = {
    val graphC = new Graph(graphA.nodeNumber,graphA.edgeNumber,graphA.maximumCapacity)
    for (i <- 0 until graphA.edgeNumber) {
      val tempEdge = if (Random.nextDouble() < probA) graphA.edges.get(i) else graphB.edges.get(i)
      graphC.edges.add(new Edge(tempEdge.start,tempEdge.end,tempEdge.capacity))
    }
    graphC
  }

  def singleCross(graphA : Graph, graphB : Graph, l : Int) : Graph = {
    val graphC = new Graph(graphA.nodeNumber, graphA.edgeNumber, graphA.maximumCapacity)
    for (i <- 0 until l) {
      val tempEdge = graphA.edges.get(i)
      graphC.edges.add(new Edge(tempEdge.start,tempEdge.end,tempEdge.capacity))
    }
    for (i <- l until graphA.edgeNumber) {
      val tempEdge = graphB.edges.get(i)
      graphC.edges.add(new Edge(tempEdge.start,tempEdge.end,tempEdge.capacity))
    }
    graphC
  }
}



