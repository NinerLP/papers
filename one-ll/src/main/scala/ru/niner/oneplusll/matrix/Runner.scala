package ru.niner.oneplusll.matrix


import java.util
import java.io.File
import collection.JavaConversions._
import ru.ifmo.ctd.ngp.demo.testgen.flows.solvers.{ImprovedShortestPath, DinicSlow, Dinic}

case class Config(dinic : Int = 1, isp : Int = 1, maxV : Int = 100, maxC : Int = 8192,
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
          //runs.add(new DiffEvoRunnable(config.maxV, config.maxC, 10, new NGPMatrixFitness(new Dinic), config.cLimit, idAssigner.getNextID))
          //runs.add(new DiffEvoRunnable(config.maxV, config.maxC, 10, new NGPMatrixFitness(new Dinic), config.cLimit, idAssigner.getNextID))

          //runs.add(new DiffEvoPlusRunnable(config.maxV, config.maxC, 100, 50, new NGPMatrixFitness(new Dinic), config.cLimit, idAssigner.getNextID))
          //runs.add(new DiffEvoPlusRandomRunnable(config.maxV, config.maxC, 100, 50, new NGPMatrixFitness(new Dinic), config.cLimit, idAssigner.getNextID))
          //runs.add(new MatrixOnePlusOneRunnable(config.maxV, config.maxC, new NGPMatrixFitness(new Dinic),
          // config.cLimit, idAssigner.getNextID))
          //runs.add(new MatrixNiceGeneticsRunnable(config.maxV, config.maxC, 10, 3, new NGPMatrixFitness(new Dinic),
          //  config.cLimit, idAssigner.getNextID))
          //runs.add(new OnePlusLambdaLambdaAdaptiveRunnable(config.maxV, config.maxE, config.maxC, adaptationCoefficient,
          //  new NGPMatrixFitness(new Dinic()), config.acyclic, config.cLimit, idAssigner.getNextID))*/
          for (lambda <- config.lambda) {
            val len = config.maxV * (config.maxV - 1) / 2
            //runs.add(new MatrixOnePlusLLBSRunnable(config.maxV, config.maxC, lambda,
            //  1.0*lambda/len, 1.0/lambda, new NGPMatrixFitness(new Dinic()), config.cLimit, idAssigner.getNextID))
            runs.add(new MatrixOnePlusLambdaLambdaRunnable(config.maxV, config.maxC, lambda,
              1.0*lambda/len, 1.0/lambda, new NGPMatrixFitness(new Dinic()), config.cLimit, idAssigner.getNextID))
            /*runs.add(new MatrixOnePlusXORRunnable(config.maxV, config.maxC, lambda,
              1.0*lambda/len, 1.0/lambda, new NGPMatrixFitness(new Dinic()), config.cLimit, idAssigner.getNextID))
            runs.add(new MatrixOnePlusTwoLambdaRunnable(config.maxV, config.maxC, lambda,
              1.0/len, new NGPMatrixFitness(new Dinic()), config.cLimit, idAssigner.getNextID ))*/
          }
        }

        //isp
        for (i <- 0 until config.isp) {
            //runs.add(new DiffEvoRunnable(config.maxV, config.maxC, 10,new NGPMatrixFitness(new ImprovedShortestPath), config.cLimit, idAssigner.getNextID))

            //runs.add(new DiffEvoRunnable(config.maxV, config.maxC, 10,new NGPMatrixFitness(new ImprovedShortestPath), config.cLimit, idAssigner.getNextID))
            //runs.add(new DiffEvoPlusRunnable(config.maxV, config.maxC, 100, 50, new NGPMatrixFitness(new ImprovedShortestPath), config.cLimit, idAssigner.getNextID))
            //runs.add(new DiffEvoPlusRandomRunnable(config.maxV, config.maxC, 100, 50, new NGPMatrixFitness(new ImprovedShortestPath), config.cLimit, idAssigner.getNextID))

            //runs.add(new MatrixOnePlusOneRunnable(config.maxV,config.maxC, new NGPMatrixFitness(new ImprovedShortestPath),
            //  config.cLimit, idAssigner.getNextID))
            //runs.add(new MatrixNiceGeneticsRunnable(config.maxV, config.maxC, 10, 3, new NGPMatrixFitness(new ImprovedShortestPath),
            //  config.cLimit, idAssigner.getNextID))
            //runs.add(new OnePlusLambdaLambdaAdaptiveRunnable(config.maxV, config.maxE, config.maxC, adaptationCoefficient,
            //  new NGPMatrixFitness(new ImprovedShortestPath), config.acyclic, config.cLimit, idAssigner.getNextID))*/
            for (lambda <- config.lambda) {
              val len = config.maxV * (config.maxV - 1) / 2
              //runs.add(new MatrixOnePlusLLBSRunnable(config.maxV, config.maxC, lambda,
              //    1.0*lambda/len, 1.0/lambda, new NGPMatrixFitness(new ImprovedShortestPath), config.cLimit, idAssigner.getNextID))
            //  runs.add(new MatrixOnePlusLambdaLambdaRunnable(config.maxV, config.maxC, lambda,
            //    1.0*lambda/len, 1.0/lambda, new NGPMatrixFitness(new ImprovedShortestPath), config.cLimit, idAssigner.getNextID))
            //  runs.add(new MatrixOnePlusXORRunnable(config.maxV, config.maxC, lambda,
            //    1.0*lambda/len, 1.0/lambda, new NGPMatrixFitness(new ImprovedShortestPath), config.cLimit, idAssigner.getNextID))
          //    runs.add(new MatrixOnePlusTwoLambdaRunnable(config.maxV, config.maxC, lambda,
            //    1.0/len, new NGPMatrixFitness(new ImprovedShortestPath), config.cLimit, idAssigner.getNextID ))
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

class MatrixOnePlusOneRunnable(nodeNumber : Int, maximumCapacity : Int,
                         fitnessFunction : NGPMatrixFitness, computationsLimit : Int, runID : Int)  extends Runnable {
  def run(): Unit = {
    println("Started 1+1 RunID: " + runID)
    new MatrixOnePlusOnePaths(nodeNumber, maximumCapacity, fitnessFunction,
      computationsLimit, runID).run()
    println("Completed 1+1 RunID: " + runID)
  }
}

class MatrixOnePlusTwoLambdaRunnable(nodeNumber : Int, maximumCapacity : Int,
                               lambda : Int, mutationProbability : Double, fitnessFunction : NGPMatrixFitness,
                               computationsLimit : Int, runID : Int) extends Runnable {
  def run(): Unit = {
    println("Started 1+2L RunID: " + runID)
    new MatrixOnePlusTwoLambda(nodeNumber, maximumCapacity, lambda, mutationProbability,
      fitnessFunction, computationsLimit, runID).run()
    println("Completed 1+2L RunID: " + runID)
  }
}

class MatrixOnePlusLambdaLambdaRunnable(nodeNumber : Int, maximumCapacity : Int,
                                  lambda : Int, mutationProbability : Double, crossoverProbabilityForA : Double,
                                  fitnessFunction : NGPMatrixFitness, computationsLimit : Int,
                                  runID : Int) extends Runnable {
  def run(): Unit = {
    println("Started 1+LL RunID: " + runID)
    new MatrixOnePlusLambdaLambdaPaths(nodeNumber, maximumCapacity, lambda, mutationProbability,
      crossoverProbabilityForA, fitnessFunction, computationsLimit, runID).run()
    println("Completed 1+LL RunID: " + runID)
  }
}

class MatrixOnePlusLLBSRunnable(nodeNumber : Int, maximumCapacity : Int,
                                  lambda : Int, mutationProbability : Double, crossoverProbabilityForA : Double,
                                  fitnessFunction : NGPMatrixFitness, computationsLimit : Int,
                                  runID : Int) extends Runnable {
  def run(): Unit = {
    println("Started 1+LLBS RunID: " + runID)
    new MatrixOnePlusLLBS(nodeNumber, maximumCapacity, lambda, mutationProbability,
      crossoverProbabilityForA, fitnessFunction, computationsLimit, runID).run()
    println("Completed 1+LLBS RunID: " + runID)
  }
}

class MatrixOnePlusLambdaLambdaXORRunnable(nodeNumber : Int, maximumCapacity : Int,
                                        lambda : Int, mutationProbability : Double, crossoverProbabilityForA : Double,
                                        fitnessFunction : NGPMatrixFitness, computationsLimit : Int,
                                        runID : Int) extends Runnable {
  def run(): Unit = {
    println("Started 1+LL XOR RunID: " + runID)
    new MatrixOnePlusLambdaLambdaXOR(nodeNumber, maximumCapacity, lambda, mutationProbability,
      crossoverProbabilityForA, fitnessFunction, computationsLimit, runID).run()
    println("Completed 1+LL XOR RunID: " + runID)
  }
}

class MatrixOnePlusXORRunnable(nodeNumber : Int, maximumCapacity : Int,
                                        lambda : Int, mutationProbability : Double, crossoverProbabilityForA : Double,
                                        fitnessFunction : NGPMatrixFitness, computationsLimit : Int,
                                        runID : Int) extends Runnable {
  def run(): Unit = {
    println("Started 1+LL XOR RunID: " + runID)
    new MatrixOnePlusXOR(nodeNumber, maximumCapacity, lambda, mutationProbability,
      crossoverProbabilityForA, fitnessFunction, computationsLimit, runID).run()
    println("Completed 1+LL FULL XOR RunID: " + runID)
  }
}
/*
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
*/
class MatrixNiceGeneticsRunnable(nodeNumber : Int, maximumCapacity : Int,
                           val generationSize : Int, val crossoverSize : Int, val fitnessFunction: NGPMatrixFitness,
                           val computationsLimit : Int, runID : Int) extends  Runnable {
  def run() : Unit = {
    println("Started NiceGenetics RunID: " + runID)
    new MatrixNiceGenetics(nodeNumber,maximumCapacity,generationSize,crossoverSize,fitnessFunction,computationsLimit,runID).run()
    println("Completed NiceGenetics RunID: " + runID)
  }
}

class DiffEvoRunnable(nodeNumber : Int, maximumCapacity : Int,
                    generationSize : Int, fitnessFunction : NGPMatrixFitness,
                    computationsLimit : Int, runID : Int) extends Runnable {
    def run() : Unit = {
      println("Started DiffEvo10 RunID: " + runID)
      new DiffEvo(nodeNumber, maximumCapacity, generationSize, fitnessFunction, computationsLimit, runID).run()
      println("Completed DiffEvo RunID: " + runID)
    }
}

class DiffEvoPlusRunnable(nodeNumber : Int, maximumCapacity : Int,
                    generationSize : Int, intermediateGenerationSize : Int, fitnessFunction : NGPMatrixFitness,
                    computationsLimit : Int, runID : Int) extends Runnable {
    def run() : Unit = {
      println("Started DiffEvoPlus RunID: " + runID)
      new DiffEvoPlus(nodeNumber, maximumCapacity, generationSize, intermediateGenerationSize, fitnessFunction, computationsLimit, runID).run()
      println("Completed DiffEvoPlus RunID: " + runID)
    }
}


class DiffEvoPlusRandomRunnable(nodeNumber : Int, maximumCapacity : Int,
                    generationSize : Int, intermediateGenerationSize : Int, fitnessFunction : NGPMatrixFitness,
                    computationsLimit : Int, runID : Int) extends Runnable {
    def run() : Unit = {
      println("Started DiffEvoPlusRandom RunID: " + runID)
      new DiffEvoPlusRandom(nodeNumber, maximumCapacity, generationSize, intermediateGenerationSize, fitnessFunction, computationsLimit, runID).run()
      println("Completed DiffEvoPlusRandom RunID: " + runID)
    }
}
