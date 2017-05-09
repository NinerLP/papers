package ru.niner.oneplusll.matrix


import java.util
import java.io.File
import collection.JavaConversions._
import ru.ifmo.ctd.ngp.demo.testgen.flows.solvers.{ImprovedShortestPath, DinicSlow, Dinic}

case class Config(dinic : Int = 1, isp : Int = 1, maxV : Int = 100, maxC : Int = 8191,
                  maxE : Int = 5000, lambda : Seq[Int] = Seq(8,16,25), acyclic : Boolean = true,
                  cLimit : Int = 500000, adaptationC : Double = 1.5, beta : Double = 1.5,
                  popSize : Int = 100, crossSize : Int = 45)

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
      opt[Double]('k',"adaptation") action { (x,c) =>
        c.copy(adaptationC = x) } text("adaptation coefficient")
      opt[Double]('b',"beta") action { (x,c) =>
        c.copy(beta = x) } text("beta power distribution parameter")
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


        val runs = new util.ArrayList[Runnable]()
        val idAssigner = new RunIDAssigner()

        //dinic

        for (i <- 0 until config.dinic) {
          runs.add(new MatrixFastOnePlusOneRunnable(config, new NGPMatrixFitness(new Dinic), idAssigner.getNextID))
          runs.add(new MatrixOnePlusOneRunnable(config, new NGPMatrixFitness(new Dinic), idAssigner.getNextID))
          runs.add(new MatrixFastNGRunnable(config, new NGPMatrixFitness(new Dinic), idAssigner.getNextID))
          runs.add(new MatrixNiceGeneticsRunnable(config, new NGPMatrixFitness(new Dinic), idAssigner.getNextID))
          //runs.add(new TestGeneticsRunnable(config, new NGPMatrixFitness(new Dinic), idAssigner.getNextID))
          //runs.add(new TestFastGeneticsRunnable(config, new NGPMatrixFitness(new Dinic), idAssigner.getNextID))
          //for (lambda <- config.lambda) {
            //runs.add(new MatrixOnePlusLambdaLambdaPathsRunnable(config,lambda,new NGPMatrixFitness(new Dinic),idAssigner.getNextID))
            //runs.add(new MatrixOnePlusLLBSRunnable(config,lambda,new NGPMatrixFitness(new Dinic),idAssigner.getNextID))
          //}
        }

        for (i <- 0 until config.isp) {
          runs.add(new MatrixFastOnePlusOneRunnable(config, new NGPMatrixFitness(new ImprovedShortestPath), idAssigner.getNextID))
          runs.add(new MatrixOnePlusOneRunnable(config, new NGPMatrixFitness(new ImprovedShortestPath), idAssigner.getNextID))
          runs.add(new MatrixFastNGRunnable(config, new NGPMatrixFitness(new ImprovedShortestPath), idAssigner.getNextID))
          runs.add(new MatrixNiceGeneticsRunnable(config, new NGPMatrixFitness(new ImprovedShortestPath), idAssigner.getNextID))
          //runs.add(new TestGeneticsRunnable(config, new NGPMatrixFitness(new ImprovedShortestPath), idAssigner.getNextID))
          //runs.add(new TestFastGeneticsRunnable(config, new NGPMatrixFitness(new ImprovedShortestPath), idAssigner.getNextID))
          //for (lambda <- config.lambda) {
            //runs.add(new MatrixOnePlusLLBSRunnable(config,lambda,new NGPMatrixFitness(new ImprovedShortestPath),idAssigner.getNextID))
          //}
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
