package ru.niner.oneplusll

import java.io.{PrintWriter, File}
import java.lang.{Long => JLong}
import java.util
import collection.JavaConversions._

import ru.ifmo.ctd.ngp.demo.testgen.flows.{EdgeRec, MaxFlowSolver}

class NGPAlgorithmFitness(override val solver : MaxFlowSolver) extends FitnessFunction {
  var bestValues : util.Map[String, JLong] = new util.HashMap[String, JLong]()
  var bestGraph : Graph = null
  var bestAlgorithmName : String = null
  override val target = -1
  val timelimit = 0
  val OPTIMIZEON = "edgeCount";
  {
    bestValues.put(OPTIMIZEON,0L)
  }

  def apply(graph : Graph, algorithmName : String) : Long = {
    //var tempFitnessValue = 0L;
    //convert to ngp repr
    val ngpedges = new util.ArrayList[EdgeRec]()

    for (i <- 0 until graph.edgeNumber) {
      val temp = graph.getEdge(i)
      ngpedges.add(new EdgeRec(temp.start, temp.end, temp.capacity))
    }

    val results : util.Map[String, JLong] = solver.solve(ngpedges, 0, graph.nodeNumber - 1, timelimit)

    if (results.get(OPTIMIZEON) >= bestValues.get(OPTIMIZEON)) {
      bestValues = results
      bestGraph = graph
      bestAlgorithmName = algorithmName
    }

    results.get(OPTIMIZEON)
  }

  def dumpResults(runID : Int) : Unit = {
    val out = new File("tests/"+ bestGraph.fitnessValue + "_" + solver.getName + "_" + runID + "_"  + bestAlgorithmName + ".txt")
    out.createNewFile()
    val pw = new PrintWriter(out,"UTF-8")

    pw.println(solver.getName)
    pw.println(bestAlgorithmName)

    for (entry : util.Map.Entry[String,JLong] <- bestValues.entrySet()) {
      pw.println(entry.getKey + " " + entry.getValue)
    }
    pw.println()

    pw.println(bestGraph.nodeNumber + " " + bestGraph.edgeNumber)
    for (i <- 0 until bestGraph.edgeNumber) {
      val tempEdge = bestGraph.getEdge(i)
      pw.println(tempEdge.start + " " + tempEdge.end + " " + tempEdge.capacity)
    }
    pw.close()
  }
}
