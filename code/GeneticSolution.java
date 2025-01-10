package code;

import java.util.Random;

import code.Population.Chromosome;

public class GeneticSolution {
    private Population population;

    public GeneticSolution(YinYangPuzzle puzzle) {
        this.population = new Population(5000, puzzle); // population size
    }

    public void solve() {
        // Initialize the population
        population.generateInitialPopulation();

        int noImprovementCount = 0;
        double lastBestFitness = Double.NEGATIVE_INFINITY;
        double bestFitness = Double.NEGATIVE_INFINITY;
        Chromosome bestChromosome = null;

        for (int generation = 0; generation < 500; generation++) {
            // Evaluate the fitness of the current population
            population.evaluateFitness();

            // Find the best chromosome in the current population
            for (Chromosome chromosome : population.getChromosomes()) {
                if (chromosome.getFitnessValue() > bestFitness) {
                    Random random = new Random(12345);
                    population.mutateGenes(chromosome.getGenes(), random);
                    bestFitness = chromosome.getFitnessValue();
                    bestChromosome = chromosome;
                }
            }

            // Log the best fitness value for monitoring
            System.out.println("Generation " + generation + ": Best Fitness = " + bestFitness);

            // Check for improvement
            if (bestFitness == lastBestFitness) {
                noImprovementCount++;
            } else {
                noImprovementCount = 0;
                lastBestFitness = bestFitness;
            }

            // Stop if no improvement for 50 generations
            if (noImprovementCount >= 50) {
                System.out.println("Stopping early due to no improvement for 50 generations.");
                break;
            }

            // Perform selection, crossover, and mutation
            population.tournamentSelection();
            population.twoPointCrossover();
        }

        // Output the best solution found
        if (bestChromosome != null) {
            System.out.println("Best Solution Found:");
            bestChromosome.itprints();
        } else {
            System.out.println("No solution found.");
        }
    }
}
