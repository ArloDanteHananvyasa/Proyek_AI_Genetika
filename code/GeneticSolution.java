package code;

import java.util.Random;

import code.Population.Chromosome;

public class GeneticSolution {
    private Population population;
    private int generationSize;
    private double elitism;
    private double crossoverRate = 0.85;

    public GeneticSolution(YinYangPuzzle puzzle, Random random, int populationSize, int generationSize,
            double elitism) { // init genetic solution
        this.population = new Population(populationSize, puzzle, random);
        this.generationSize = generationSize;
        this.elitism = elitism;
    }

    public String solve() {
        String report = "";
        // Initialize the population
        population.generateInitialPopulation();

        int lastGeneration = 0; // last gen untuk report
        String lastBoard = ""; // lastboard untuk report
        int noImprovementCount = 0;
        double lastBestFitness = Double.NEGATIVE_INFINITY;
        double bestFitness = Double.NEGATIVE_INFINITY;

        for (int generation = 0; generation < generationSize; generation++) {
            lastGeneration = generation;
            // evaluasi nilai fitness untuk populasi yang sekarang
            String board = population.evaluateFitness();
            lastBoard = board;

            // Find the best chromosome in the current population
            for (Chromosome chromosome : population.getChromosomes()) {
                if (chromosome.getFitnessValue() > bestFitness) {
                    population.mutateGenes(chromosome.getGenes());
                    bestFitness = chromosome.getFitnessValue();
                }
            }

            System.out.println("Generation " + generation + ": Best Fitness = " + bestFitness);
            System.out.println();

            if (population.hasSolution()) { // jika ditemukan best solution, hentikan
                report += "Best solution found.\n";
                System.out.printf("Best solution found.\n");
                break;
            }

            // cek apakah terjadi peningkatan nilai fitness
            if (bestFitness == lastBestFitness) {
                noImprovementCount++;
            } else {
                noImprovementCount = 0;
                lastBestFitness = bestFitness;
            }

            // early termination ketika tidak ada perubahan fitness selama X (bisa diubah) generasi, sebagai contoh menggunakan 50
            if (noImprovementCount >= 50) {
                report += "Stopped early due to no improvement for 50 generations.\n";
                System.out.println("Stopping early due to no improvement for 50 generations.");
                System.out.println();
                break;
            }

            // lakukan selection, lalu crossover
            population.simpleTournamentSelection(elitism);
            population.twoPointCrossover(crossoverRate);

        }
        // tambahkan board, generation, dan fitness terakhir ke report file
        report += lastBoard;
        report += "Generation " + lastGeneration + ": Best Fitness = " + bestFitness;
        return report;
    }
}
