public class GeneticSolution {
    private Population population;

    public GeneticSolution(YinYangPuzzle puzzle) {
        this.population = new Population(100, puzzle); // Set population size
    }

    public void solve() {
        // Initial population generation
        population.generateInitialPopulation();

        // Example of a genetic algorithm loop
        for (int generation = 0; generation < 1; generation++) {
            population.evaluateFitness();
            // population.selection();
            // population.crossover();
            // population.mutation();

            // Check for solution or stopping condition
            if (population.hasSolution()) {
                System.out.println("Solution found in generation " + generation);
                break;
            }
        }
    }
}
