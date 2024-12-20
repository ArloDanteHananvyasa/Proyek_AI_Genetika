public class GeneticSolution {
    private Population population;

    public GeneticSolution(YinYangPuzzle puzzle) {
        this.population = new Population(100, puzzle); // population size
    }

    public void solve() {
        // init population
        population.generateInitialPopulation();

        // Example of a genetic algorithm loop
        for (int generation = 0; generation < 1; generation++) {
            population.evaluateFitness();
            // population.selection();
            // population.crossover();
            // population.mutation();

            if (population.hasSolution()) {
                System.out.println("solution found in generation " + generation);
                break;
            }
        }
    }
}
