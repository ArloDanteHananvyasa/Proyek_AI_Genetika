public class GeneticSolution {
    private Population population;

    public GeneticSolution(YinYangPuzzle puzzle) {
        this.population = new Population(1000, puzzle); // population size
    }

    public void solve() {
        // init population
        population.generateInitialPopulation();

        for (int generation = 0; generation < 200; generation++) {
            System.out.printf("best chromosome of generation %s: \n", generation);
            population.evaluateFitness();
            population.tournamentSelection();
            // population.rankSelection();
            // population.rouletteWheelSelection();
            population.crossover();

            if (population.hasSolution()) {
                System.out.println("solution found in generation " + generation);
                break;
            }
        }
    }
}
