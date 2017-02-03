package ru.niner.oneplusll.matrix

class MatrixOnePlusOneRunnable(config: Config, fitness: NGPMatrixFitness, runID : Int) extends Runnable {
  override def run() : Unit = {
    println("Started 1+1 RunID: " + runID)
    new MatrixOnePlusOne(config.maxV, config.maxC, fitness, config.cLimit, runID).run();
    println("Completed 1+1 RunID: " + runID)
  }
}


class MatrixOnePlusOnePathsRunnable(config: Config, fitness : NGPMatrixFitness, runID : Int) extends Runnable {
  override def run() : Unit = {
    println("Started 1+1 RunID: " + runID)
    new MatrixOnePlusOnePaths(config.maxV, config.maxC, fitness, config.cLimit, runID).run();
    println("Completed 1+1 RunID: " + runID)
  }
}

class MatrixOnePlusLLBSRunnable(config: Config, lambda : Int, fitness : NGPMatrixFitness, runID : Int) extends Runnable {
  override def run() : Unit = {
    println("Started 1+LLBS RunID: " + runID)
    val len = 13 * config.maxV * (config.maxV - 1) / 2
    new MatrixOnePlusLLBS(config.maxV, config.maxC, lambda, 1.0 * lambda / len,
      1.0 / lambda, fitness, config.cLimit, runID).run()
    println("Completed 1+LLBS RunID: " + runID)
  }
}

class MatrixOnePlusLLBSAdaptiveRunnable(config : Config, fitness : NGPMatrixFitness, runID : Int) extends Runnable {
  override def run() : Unit = {
    println(s"Started 1+LLBS+ADA RunID: $runID")
    new MatrixOnePlusLLBSAdaptive(config.maxV, config.maxC, fitness, config.cLimit, config.adaptationC, runID).run()
    println(s"Completed 1+LLBS+ADA RunID: $runID")
  }
}

class MatrixOnePlusLambdaLambdaPathsRunnable(config : Config, lambda : Int, fitness : NGPMatrixFitness, runID : Int) extends Runnable {
  override def run() : Unit = {
    println(s"Started 1+LL+Paths RunID: $runID")
    new MatrixOnePlusLambdaLambdaPaths(config.maxV,config.maxC,lambda,fitness,config.cLimit,runID).run()
    println(s"Completed 1+LL+Paths RunID: $runID")
  }
}

class MatrixOnePlusOnePathsWeightedRunnable(config : Config, fitness : NGPMatrixFitness, runID : Int) extends Runnable {
  override def run() : Unit = {
    println(s"Started 1+1+PW RunID: $runID")
    new MatrixOnePlusOnePathsWeighted(config.maxV,config.maxC,fitness,config.cLimit,runID).run()
    println(s"Completed 1+1+PW RunID: $runID")
  }
}

/* cleaned up to here */




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
    new MatrixOnePlusLambdaLambdaPathsAdaptive(nodeNumber, maximumCapacity, lambda, mutationProbability,
      crossoverProbabilityForA, fitnessFunction, computationsLimit, runID).run()
    println("Completed 1+LL RunID: " + runID)
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
