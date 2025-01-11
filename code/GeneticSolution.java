package code;

import java.util.Random;

import code.Population.Chromosome;

public class GeneticSolution {
    private Population population;
    private int generationSize;

    public GeneticSolution(YinYangPuzzle puzzle, Random random, int populationSize, int generationSize) { //init genetic solution
        this.population = new Population(populationSize, puzzle, random);
        this.generationSize = generationSize;
    }

    public void solve() {
        // Initialize the population
        population.generateInitialPopulation();

        int noImprovementCount = 0;
        double lastBestFitness = Double.NEGATIVE_INFINITY;
        double bestFitness = Double.NEGATIVE_INFINITY;

        for (int generation = 0; generation < generationSize; generation++) {
            // Evaluate the fitness of the current population
            population.evaluateFitness();

            // Find the best chromosome in the current population
            for (Chromosome chromosome : population.getChromosomes()) {
                if (chromosome.getFitnessValue() > bestFitness) {
                    population.mutateGenes(chromosome.getGenes());
                    bestFitness = chromosome.getFitnessValue();
                }
            }

            // Log the best fitness value for monitoring
            System.out.println("Generation " + generation + ": Best Fitness = " + bestFitness);
            System.out.println();

            if (population.hasSolution()) { //jika ditemukan best solution, hentikan
                System.out.printf("Best solution found.\n\n");
                break;
            }

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
                System.out.println();
                break;
            }

            // Perform selection, crossover
            population.simpleTournamentSelection();
            population.twoPointCrossover();
        }
    }
}
