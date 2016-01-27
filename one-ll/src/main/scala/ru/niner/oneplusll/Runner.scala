package ru.niner.oneplusll

import java.util
import java.io.File
import collection.JavaConversions._
import ru.ifmo.ctd.ngp.demo.testgen.flows.solvers.{ImprovedShortestPath, DinicSlow, Dinic}

case class Config(dinic : Int = 1, isp : Int = 1, maxV : Int = 100, maxC : Int = 10000,
                   maxE : Int = 5000, lambda : Seq[Int] = Seq(8,16,25), acyclic : Boolean = true,
                   cLimit : Int = 500000)

object Runner {

  def main(args : Array[String]): Unit = {
    val parser = new scopt.OptionParser[Config]("runner") {
      opt[Int]('d',"dinic") action { (x,c) =>
        c.copy(dinic = x) } text("number of dinic runs")
      opt[Int]('i',"isp") action { (x,c) =>
        c.copy(isp = x) } text("number of isp runs")
      opt[Int]('v',"maxV") action { (x,c) =>
        c.copy(maxV = x) } text("number of vertices")
      opt[Int]('c',"maxC") action { (x,c) =>
        c.copy(maxC = x) } text("maximum capacity")
      opt[Int]('e',"maxE") action { (x,c) =>
        c.copy(maxE = x) } text("number of edges")
      opt[Boolean]('a',"acyclic") action { (x,c) =>
       c.copy(acyclic = x)} text("generate acyclic graphs")
      opt[Int]('l',"limit") action { (x,c) =>
        c.copy(cLimit = x)} text("fitness computations limit")
      opt[Seq[Int]]("lambda") valueName("<lambda1>,<lambda2>...") action { (x,c) =>
        c.copy(lambda = x)} text("lambda values")
      help("help") text("prints usage text")
    }

    parser.parse(args, Config()) match {
      case Some(config) =>
        println("Launching " + config.dinic + " Dinic runs and " + config.isp + " ISP runs")

        val logRoot = new File("logs")
        logRoot.mkdirs()
        val testRoot = new File("tests")
        testRoot.mkdirs()
        val adaptationCoefficient = 1.5

        val runs = new util.ArrayList[Runnable]()
        val idAssigner = new RunIDAssigner()

        //dinic
        for (i <- 0 until config.dinic) {
          runs.add(new OnePlusOneRunnable(config.maxV,config.maxE, config.maxC, new NGPAlgorithmFitness(new Dinic),
          config.acyclic, config.cLimit, idAssigner.getNextID))
          runs.add(new NiceGeneticsRunnable(config.maxV, config.maxE, config.maxC, 100, 45, new NGPAlgorithmFitness(new Dinic),
          config.acyclic, config.cLimit, idAssigner.getNextID))
          runs.add(new OnePlusLambdaLambdaAdaptiveRunnable(config.maxV, config.maxE, config.maxC, adaptationCoefficient,
            new NGPAlgorithmFitness(new Dinic()), config.acyclic, config.cLimit, idAssigner.getNextID))
          for (lambda <- config.lambda) {
            runs.add(new OnePlusLambdaLambdaRunnable(config.maxV,config.maxE, config.maxC, lambda,
              1.0*lambda/config.maxE, 1.0/lambda, new NGPAlgorithmFitness(new Dinic()),
              config.acyclic, config.cLimit, idAssigner.getNextID))
            runs.add(new OnePlusTwoLambdaRunnable(config.maxV,config.maxE, config.maxC, lambda,
              1.0/config.maxE, new NGPAlgorithmFitness(new Dinic()), config.acyclic,
              config.cLimit, idAssigner.getNextID ))
          }
        }

        //isp
        for (i <- 0 until config.isp) {
          runs.add(new OnePlusOneRunnable(config.maxV,config.maxE, config.maxC, new NGPAlgorithmFitness(new ImprovedShortestPath),
            config.acyclic, config.cLimit, idAssigner.getNextID))
          runs.add(new NiceGeneticsRunnable(config.maxV, config.maxE, config.maxC, 100, 45, new NGPAlgorithmFitness(new ImprovedShortestPath),
            config.acyclic, config.cLimit, idAssigner.getNextID))
          runs.add(new OnePlusLambdaLambdaAdaptiveRunnable(config.maxV, config.maxE, config.maxC, adaptationCoefficient,
            new NGPAlgorithmFitness(new ImprovedShortestPath), config.acyclic, config.cLimit, idAssigner.getNextID))
          for (lambda <- config.lambda) {
            runs.add(new OnePlusLambdaLambdaRunnable(config.maxV,config.maxE, config.maxC, lambda,
              1.0*lambda/config.maxE, 1.0/lambda, new NGPAlgorithmFitness(new ImprovedShortestPath),
              config.acyclic, config.cLimit, idAssigner.getNextID))
            runs.add(new OnePlusTwoLambdaRunnable(config.maxV,config.maxE, config.maxC, lambda,
              1.0/config.maxE, new NGPAlgorithmFitness(new ImprovedShortestPath), config.acyclic,
              config.cLimit, idAssigner.getNextID ))
          }
        }


        println("Total Runs: " + runs.size())
        util.Collections.shuffle(runs)

        for (i : Runnable <- runs) {
          new Thread(i).start
        }

      case None =>
        println("Error")
    }
  }
}

class RunIDAssigner() {
  var ID = 1
  def getNextID : Int = {
    val id = ID
    ID += 1
    //println("Assigning ID " + id)
    id
  }
}

class OnePlusOneRunnable(nodeNumber : Int, edgeNumber : Int, maximumCapacity : Int,
                         fitnessFunction : FitnessFunction, isAcyclic : Boolean,
                         computationsLimit : Int, runID : Int)  extends Runnable {
  def run(): Unit = {
    println("Started 1+1 RunID: " + runID)
    new OnePlusOne(nodeNumber, edgeNumber, maximumCapacity, fitnessFunction,
      isAcyclic, computationsLimit, runID).run()
    println("Completed 1+1 RunID: " + runID)
  }
}

class OnePlusTwoLambdaRunnable(nodeNumber : Int, edgeNumber : Int, maximumCapacity : Int,
                               lambda : Int, mutationProbability : Double, fitnessFunction : FitnessFunction,
                               isAcyclic : Boolean, computationsLimit : Int, runID : Int) extends Runnable {
  def run(): Unit = {
    println("Started 1+2L RunID: " + runID)
    new OnePlusTwoLambda(nodeNumber, edgeNumber, maximumCapacity, lambda, mutationProbability,
      fitnessFunction, isAcyclic, computationsLimit, runID).run()
    println("Completed 1+2L RunID: " + runID)
  }
}

class OnePlusLambdaLambdaRunnable(nodeNumber : Int, edgeNumber : Int, maximumCapacity : Int,
                                  lambda : Int, mutationProbability : Double, crossoverProbabilityForA : Double,
                                  fitnessFunction : FitnessFunction, isAcyclic : Boolean, computationsLimit : Int,
                                  runID : Int) extends Runnable {
  def run(): Unit = {
    println("Started 1+LL RunID: " + runID)
    new OnePlusLambdaLambda(nodeNumber, edgeNumber, maximumCapacity, lambda, mutationProbability,
      crossoverProbabilityForA, fitnessFunction, isAcyclic, computationsLimit, runID).run()
    println("Completed 1+LL RunID: " + runID)
  }
}

class OnePlusLambdaLambdaAdaptiveRunnable(nodeNumber : Int, edgeNumber : Int, maximumCapacity : Int,
                                          adaptationCoefficient : Double, fitnessFunction : FitnessFunction,
                                          isAcyclic : Boolean, computationsLimit : Int, runID : Int) extends Runnable {
  def run(): Unit = {
    println("Started 1+LL Adaptive RunID: " + runID)
    new OnePlusLambdaLambdaAdaptive(nodeNumber, edgeNumber, maximumCapacity, adaptationCoefficient,
      fitnessFunction, isAcyclic, computationsLimit, runID).run()
    println("Completed 1+LL Adaptive RunID: " + runID)
  }
}

class NiceGeneticsRunnable(nodeNumber : Int, edgeNumber : Int, maximumCapacity : Int,
                            val generationSize : Int, val crossoverSize : Int, val fitnessFunction: FitnessFunction,
                            val isAcyclic : Boolean, val computationsLimit : Int, runID : Int) extends  Runnable {
  def run() : Unit = {
    println("Started NiceGenetics RunID: " + runID)
    new NiceGenetics(nodeNumber,edgeNumber,maximumCapacity,generationSize,crossoverSize,fitnessFunction,isAcyclic,computationsLimit,runID).run()
    println("Completed NiceGenetics RunID: " + runID)
  }
}