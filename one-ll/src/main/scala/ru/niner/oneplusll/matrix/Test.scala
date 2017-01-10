package ru.niner.oneplusll.matrix

import ru.niner.oneplusll.MathUtil

object Test extends App {
  val a = MatrixGraph.createRandom(5,100)
  MatrixGraph.debugMatOut(a)
  val b = MatrixGraph.pathMutate(a,5)
  MatrixGraph.debugMatOut(b._1)
}
