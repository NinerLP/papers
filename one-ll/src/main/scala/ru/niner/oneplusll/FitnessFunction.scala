package ru.niner.oneplusll

trait FitnessFunction {
  val target = -1
  val solver : ru.ifmo.ctd.ngp.demo.testgen.flows.MaxFlowSolver = null
  def apply(graph : Graph, AlgorithmName : String) : Long
  def dumpResults(runid: Int): Unit
}
