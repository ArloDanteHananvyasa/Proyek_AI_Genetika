import java.util.*;

public class Population {
    private int size;
    private List<Chromosome> chromosomes;
    private YinYangPuzzle puzzle;
    private double bestFitness;

    public Population(int size, YinYangPuzzle puzzle) {
        this.size = size;
        this.puzzle = puzzle;
        this.chromosomes = new ArrayList<>();
        this.bestFitness = Double.NEGATIVE_INFINITY;
    }

    public void generateInitialPopulation() {
        for (int i = 0; i < size; i++) {
            Chromosome chromosome = new Chromosome(puzzle);
            chromosome.randomize();
            this.chromosomes.add(chromosome);
            // chromosome.itprints();
        }
    }

    public void evaluateFitness() {
        Chromosome bestChromosome = null;
        double highestFitness = Double.NEGATIVE_INFINITY;

        for (Chromosome chromosome : chromosomes) {
            // chromosome.itprints();
            chromosome.evaluateFitness();
            if (chromosome.getFitnessValue() > highestFitness) {
                highestFitness = chromosome.getFitnessValue(); // Update highest fitness value
                bestChromosome = chromosome; // Update best chromosome
            }
        }
        if (bestChromosome != null) {
            bestChromosome.itprints();
            System.out.println("Fitness: " + highestFitness);
            System.out.println();
        }
        if (highestFitness > bestFitness) {
            bestFitness = highestFitness;
        }
    }

    public void rankSelection() { //select 50% dari populasi jadi parent

        chromosomes.sort((c2, c1) -> Double.compare(c2.getFitnessValue(), c1.getFitnessValue()));

        int[] ranks = new int[chromosomes.size()];
        for (int i = 0; i < chromosomes.size(); i++) {
            ranks[i] = i + 1;
        }

        int totalRankSum = Arrays.stream(ranks).sum();

        List<Chromosome> parents = new ArrayList<>();
        Random random = new Random();

        while (parents.size() < size/2) {

            int randomValue = random.nextInt(totalRankSum) + 1;
            int cumulativeSum = 0;

            for (int i = 0; i < chromosomes.size(); i++) {
                cumulativeSum += ranks[i];
                if (cumulativeSum >= randomValue) {
                    parents.add(chromosomes.get(i));
                    break;
                }
            }
        }
        chromosomes = parents;
    }

    public void simpleTournamentSelection() {
        Random random = new Random();
        List<Chromosome> parents = new ArrayList<>();
        while (parents.size() < size / 5) {
            Chromosome parent1 = chromosomes.get(random.nextInt(chromosomes.size()));
            Chromosome parent2 = chromosomes.get(random.nextInt(chromosomes.size()));
            while (parent1 == parent2) {
                parent2 = chromosomes.get(random.nextInt(chromosomes.size()));
            }

            Chromosome winner = (parent1.getFitnessValue() > parent2.getFitnessValue()) ? parent1 : parent2;
            if (!parents.contains(winner)) {
                parents.add(winner);
            }
        }
        chromosomes = parents;
    }

    public void tournamentSelection() {
        int tournamentSize = 4;
        Random random = new Random();
        List<Chromosome> selectedParents = new ArrayList<>();
    
        while (selectedParents.size() < size / 2) {
            List<Chromosome> tournamentContestants = new ArrayList<>();
            for (int i = 0; i < tournamentSize; i++) {
                Chromosome contestant = chromosomes.get(random.nextInt(chromosomes.size()));
                tournamentContestants.add(contestant);
            }
            
            Chromosome best = tournamentContestants.stream()
                    .max(Comparator.comparingDouble(Chromosome::getFitnessValue))
                    .orElseThrow();
            selectedParents.add(best);
        }
    
        chromosomes = selectedParents;
    }
    
    public void rouletteWheelSelection() {
        double totalFitness = chromosomes.stream()
                .mapToDouble(Chromosome::getFitnessValue)
                .sum();
    
        List<Double> probabilities = new ArrayList<>();
        for (Chromosome chromosome : chromosomes) {
            probabilities.add(chromosome.getFitnessValue() / totalFitness);
        }
    
        List<Chromosome> selectedParents = new ArrayList<>();
        Random random = new Random();
    
        while (selectedParents.size() < size / 2) {
            double r = random.nextDouble();
            double cumulativeProbability = 0.0;
    
            for (int i = 0; i < chromosomes.size(); i++) {
                cumulativeProbability += probabilities.get(i);
                if (r <= cumulativeProbability) {
                    selectedParents.add(chromosomes.get(i));
                    break;
                }
            }
        }
        chromosomes = selectedParents;
    }

    public void crossover() { // single point
        Random random = new Random();
        while (chromosomes.size() < size) {
            Chromosome parent1 = chromosomes.get(random.nextInt(chromosomes.size()));
            Chromosome parent2 = chromosomes.get(random.nextInt(chromosomes.size()));

            while (parent1 == parent2) {
                parent2 = chromosomes.get(random.nextInt(chromosomes.size()));
            }

            int crossoverPoint = random.nextInt(parent1.genes.length);

            char[] offspring1Genes = new char[parent1.genes.length];
            char[] offspring2Genes = new char[parent2.genes.length];

            for (int i = 0; i < parent1.genes.length; i++) {
                if (i < crossoverPoint) {
                    offspring1Genes[i] = parent1.genes[i];
                    offspring2Genes[i] = parent2.genes[i];
                } else {
                    offspring1Genes[i] = parent2.genes[i];
                    offspring2Genes[i] = parent1.genes[i];
                }
            }

            mutateGenes(offspring1Genes, random);
            mutateGenes(offspring2Genes, random);

            chromosomes.add(new Chromosome(puzzle, offspring1Genes));
            chromosomes.add(new Chromosome(puzzle, offspring2Genes));
        }
    }

    // crossover lain...


    // crossover lain...

    private void mutateGenes(char[] genes, Random random) {
        for (int i = 0; i < genes.length; i++) {
            if (!puzzle.isLockedPosition(i)) {
                if (random.nextDouble() < 0.01) {
                    genes[i] = (genes[i] == 'W') ? 'B' : 'W';
                }
            }
        }
    }

    public boolean hasSolution() {
        return bestFitness == 0;
    }

    class Chromosome {
        private char[] genes;
        private Fitness fitness;

        public Chromosome(YinYangPuzzle puzzle) {
            this.genes = new char[puzzle.getBoardSize()];
            this.genes = Arrays.copyOf(puzzle.getBoard(), puzzle.getBoard().length);
            this.fitness = new Fitness(this);
        }

        public Chromosome(YinYangPuzzle puzzle, char[] genes) {
            this.genes = genes;
            this.fitness = new Fitness(this);
        }

        public void itprints(){
            for(int i = 0 ; i< 6;i++){
                for (int j = 0 ; j< 6;j++) {
                    System.out.print(genes[i * 6 + j]+" ");
                }
                System.out.println();
            }
        }

        public void randomize() {
            Random rand = new Random();
            for (int i = 0; i < this.genes.length; i++) {
                if (!puzzle.isLockedPosition(i)) {
                    int randomChoice = rand.nextInt(2) + 1;
                    if (randomChoice == 1) {
                        this.genes[i] = 'W';
                    } else {
                        this.genes[i] = 'B';
                    }
                    // this.genes[i] = (randomChoice == 1) ? 'W' : 'B';
                    // System.out.println(randomChoice);
                }
            }
        }

        public void evaluateFitness() {
            this.fitness.calculate();
        }

        public double getFitnessValue() {
            return fitness.getValue();
        }
    }

    class Fitness {
        private double value;
        private Chromosome chromosome;
    
        public Fitness(Chromosome chromosome) {
            this.chromosome = chromosome;
        }
    
        public void calculate() {
            char[] board = chromosome.genes;
            int size = (int) Math.sqrt(board.length);
            int checkerboardPenalty = 0;
            int violationPenalty = 0;
            int islandPenalty = 0;
            int bobotCP = 1;
            int bobotVP = 2;
            int bobotIP = 3;
            
            // penalti checkerboard pattern
            for (int row = 0; row < size - 1; row++) {
                for (int col = 0; col < size - 1; col++) {
                    char current = board[row * size + col];
                    char right = board[row * size + col + 1];
                    char below = board[(row + 1) * size + col];
                    char diagonal = board[(row + 1) * size + col + 1];

                    // penalti jika mengikuti pola checkerboard
                    if ((current == 'W' && right == 'B' && below == 'B' && diagonal == 'W') ||
                        (current == 'B' && right == 'W' && below == 'W' && diagonal == 'B')) {
                        checkerboardPenalty++;
                    }
                    // penalti 4 kotak warna sama
                    if (current == right && current == below && current == diagonal) {
                        violationPenalty++;
                    }
                }
            }

            // itung ada berapa island
            boolean[] visited = new boolean[board.length];
            int whiteIslands = 0, blackIslands = 0;
    
            for (int i = 0; i < board.length; i++) {
                if (!visited[i]) {
                    if (board[i] == 'W') {
                        whiteIslands++;
                        exploreIsland(board, size, i, 'W', visited);
                    } else if (board[i] == 'B') {
                        blackIslands++;
                        exploreIsland(board, size, i, 'B', visited);
                    }
                }
            }
    
            // penalty kalau island berlebih
            islandPenalty = (whiteIslands > 1 ? whiteIslands - 1 : 0) + 
                            (blackIslands > 1 ? blackIslands - 1 : 0);
    
            // palculate total fitness
            value = -(bobotCP * checkerboardPenalty) - (bobotVP * violationPenalty) - (bobotIP * islandPenalty);
            // System.out.println(value);
            
        }
    
        public double getValue() {
            return value;
        }
    
        // explore island dengan DFS
        private void exploreIsland(char[] board, int size, int index, char color, boolean[] visited) {
            if (index < 0 || index >= board.length || visited[index] || board[index] != color) {
                return;
            }
    
            visited[index] = true;
    
            int row = index / size;
            int col = index % size;
    
            // check 4 tetangganya
            if (row > 0) exploreIsland(board, size, index - size, color, visited); // up
            if (row < size - 1) exploreIsland(board, size, index + size, color, visited); // down
            if (col > 0) exploreIsland(board, size, index - 1, color, visited); // left
            if (col < size - 1) exploreIsland(board, size, index + 1, color, visited); // right
        }
    }
}
