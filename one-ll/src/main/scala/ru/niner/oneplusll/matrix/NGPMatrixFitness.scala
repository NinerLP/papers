package ru.niner.oneplusll.matrix

import java.io.{File, PrintWriter}
import java.lang.{Long => JLong}
import java.util

import ru.ifmo.ctd.ngp.demo.testgen.flows.{EdgeRec, MaxFlowSolver}

import scala.collection.JavaConversions._

class NGPMatrixFitness (val solver : MaxFlowSolver) {
  var bestValues : util.Map[String, JLong] = new util.HashMap[String, JLong]()
  var bestGraph : MatrixGraph = null
  var bestAlgorithmName : String = null
  val target = -1
  val timelimit = 0
  val OPTIMIZEON = "edgeCount";
  {
    bestValues.put(OPTIMIZEON,0L)
  }

  def apply(mgraph : MatrixGraph, algorithmName : String) : Long = {
    //var tempFitnessValue = 0L;
    //convert to ngp repr
    val ngpedges = new util.ArrayList[EdgeRec]()

    var processed = 0
    for (start <- 0 until mgraph.nodeNumber) {
      //println("Start: " + start + ", Processed: " + processed)
      for (i <- 0 until mgraph.nodeNumber - start) {
        val cap = mgraph.capacities.get(processed+i)
        if (cap > 0) {
          ngpedges.add(new EdgeRec(start,start+i,cap))
        }
      }
      processed += mgraph.nodeNumber - start
    }



    val results : util.Map[String, JLong] = solver.solve(ngpedges, 0, mgraph.nodeNumber - 1, timelimit)

    if (results.get(OPTIMIZEON) >= bestValues.get(OPTIMIZEON)) {
      bestValues = results
      bestGraph = mgraph
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


    var processed = 0
    for (start <- 0 until bestGraph.nodeNumber) {
      //println("Start: " + start + ", Processed: " + processed)
      for (i <- 0 until bestGraph.nodeNumber - start) {
        val cap = bestGraph.capacities.get(processed+i)
        if (cap > 0) {
          //ngpedges.add(new EdgeRec(start,start+i,cap))
          pw.println(start + " " + (start+i) + " " + cap)
        }
      }
      processed += bestGraph.nodeNumber - start
    }
    pw.close()

    /*pw.println(bestGraph.nodeNumber + " " + bestGraph.edgeNumber)
    for (i <- 0 until bestGraph.edgeNumber) {
      val tempEdge = bestGraph.getEdge(i)
      pw.println(tempEdge.start + " " + tempEdge.end + " " + tempEdge.capacity)
    }
    pw.close()*/
  }
}
