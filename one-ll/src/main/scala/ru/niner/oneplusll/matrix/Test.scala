package ru.niner.oneplusll.matrix

import java.io.{File, PrintWriter}
import java.util

import ru.ifmo.ctd.ngp.demo.testgen.flows.solvers.{Dinic, ImprovedShortestPath}
import ru.niner.oneplusll.MathUtil

import scala.util.Random

object Test extends App {

}

class PathInfo(val graph : Int, val length : Int, val phase : Int, val initialF : Long, val achievedF : Long) {
  override def toString: String = {
    s"$graph $length $phase $initialF $achievedF"
  }
}


object TestPaths extends App {

  val data = new util.ArrayList[PathInfo]()
  val fitness = new NGPMatrixFitness(new ImprovedShortestPath)
  val pSize = Range(1,21) ++ Range(21,80,2) ++ Range(80,99)

  for (g <- 0 until 3) {
    var graph = MatrixGraph.createRandom(100, 8192)
    graph.computeFitnessValue(fitness, "")

    for (trial <- 0 until 1000) {
      println(s"Testing graph number ${g + 1}, trial ${trial+1}, phase 0")


      for (pathSize <- pSize) {
        //println(s"${pathSize + 1} edges")
        val parall: List[MatrixGraph] = List.fill(10)(graph)
        val fvals = parall.par.map(x => MatrixGraph.applyNewPath(x, MathUtil.getPath(pathSize, 100)).computeFitnessValue(fitness, ""))
        for (i <- parall.indices) {
          data.add(new PathInfo(g, pathSize, 0, graph.fitnessValue, fvals(i)))
        }
      }

    }

    for (i <- 0 until 100000) {
      val ng = MatrixGraph.applyNewPath(graph,MathUtil.getPath(Random.nextInt(20)+1,100))
      if (ng.computeFitnessValue(fitness,"") >= graph.fitnessValue) {
        graph = ng
      }
    }

    for (trial <- 0 until 1000) {
      println(s"Testing graph number ${g+1}, trial ${trial + 1}, phase 100k")


      for (pathSize <- pSize) {
        //println(s"${pathSize + 1} edges")
        val parall: List[MatrixGraph] = List.fill(10)(graph)
        val fvals = parall.par.map(x => MatrixGraph.applyNewPath(x, MathUtil.getPath(pathSize, 100)).computeFitnessValue(fitness, ""))
        for (i <- parall.indices) {
          data.add(new PathInfo(g, pathSize, 100, graph.fitnessValue, fvals(i)))
        }
      }

    }

    for (i <- 0 until 150000) {
      val ng = MatrixGraph.applyNewPath(graph,MathUtil.getPath(Random.nextInt(20)+1,100))
      if (ng.computeFitnessValue(fitness,"") >= graph.fitnessValue) {
        graph = ng
      }
    }

    for (trial <- 0 until 1000) {
      println(s"Testing graph number ${g+1}, trial ${trial + 1}, phase 250k")


      for (pathSize <- pSize) {
        //println(s"${pathSize + 1} edges")
        val parall: List[MatrixGraph] = List.fill(10)(graph)
        val fvals = parall.par.map(x => MatrixGraph.applyNewPath(x, MathUtil.getPath(pathSize, 100)).computeFitnessValue(fitness, ""))
        for (i <- parall.indices) {
          data.add(new PathInfo(g, pathSize, 250, graph.fitnessValue, fvals(i)))
        }
      }

    }

    for (i <- 0 until 150000) {
      val ng = MatrixGraph.applyNewPath(graph,MathUtil.getPath(Random.nextInt(20)+1,100))
      if (ng.computeFitnessValue(fitness,"") >= graph.fitnessValue) {
        graph = ng
      }
    }

    for (trial <- 0 until 1000) {
      println(s"Testing graph number ${trial + 1}, phase 400k")


      for (pathSize <- pSize) {
        //println(s"${pathSize + 1} edges")
        val parall: List[MatrixGraph] = List.fill(10)(graph)
        val fvals = parall.par.map(x => MatrixGraph.applyNewPath(x, MathUtil.getPath(pathSize, 100)).computeFitnessValue(fitness, ""))
        for (i <- parall.indices) {
          data.add(new PathInfo(g, pathSize, 400, graph.fitnessValue, fvals(i)))
        }
      }

    }

    val pw = new PrintWriter(new File(s"paths_isp_hug_${g}.txt"))
    pw.write("graph length phase initial achieved\n")
    for (i <- 0 until data.size()) {
      pw.write(data.get(i).toString + "\n")
    }
    pw.close()
    data.clear()

  }


}