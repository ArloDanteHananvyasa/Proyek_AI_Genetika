package code;

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

    public List<Chromosome> getChromosomes() {
        return chromosomes;
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

    public void rankSelection() { // select 50% dari populasi jadi parent

        chromosomes.sort((c2, c1) -> Double.compare(c2.getFitnessValue(), c1.getFitnessValue()));

        int[] ranks = new int[chromosomes.size()];
        for (int i = 0; i < chromosomes.size(); i++) {
            ranks[i] = i + 1;
        }

        int totalRankSum = Arrays.stream(ranks).sum();

        List<Chromosome> parents = new ArrayList<>();
        Random random = new Random(12345);

        while (parents.size() < size / 2) {

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
        Random random = new Random(12345);
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
        int tournamentSize = 5; // ganti di sini -arlo (tadinya 4)
        Random random = new Random(12345);
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
        Random random = new Random(12345);

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
        Random random = new Random(12345);
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

    public void twoPointCrossover() {
        Random random = new Random(12345);
        while (chromosomes.size() < size) {
            Chromosome parent1 = chromosomes.get(random.nextInt(chromosomes.size()));
            Chromosome parent2 = chromosomes.get(random.nextInt(chromosomes.size()));

            while (parent1 == parent2) {
                parent2 = chromosomes.get(random.nextInt(chromosomes.size()));
            }

            int point1 = random.nextInt(parent1.genes.length);
            int point2 = random.nextInt(parent1.genes.length);

            if (point1 > point2) {
                int temp = point1;
                point1 = point2;
                point2 = temp;
            }

            char[] offspring1Genes = parent1.genes.clone();
            char[] offspring2Genes = parent2.genes.clone();

            for (int i = point1; i < point2; i++) {
                offspring1Genes[i] = parent2.genes[i];
                offspring2Genes[i] = parent1.genes[i];
            }

            mutateGenes(offspring1Genes, random);
            mutateGenes(offspring2Genes, random);

            chromosomes.add(new Chromosome(puzzle, offspring1Genes));
            chromosomes.add(new Chromosome(puzzle, offspring2Genes));
        }
    }

    public void uniformCrossover() {
        Random random = new Random(12345);
        while (chromosomes.size() < size) {
            Chromosome parent1 = chromosomes.get(random.nextInt(chromosomes.size()));
            Chromosome parent2 = chromosomes.get(random.nextInt(chromosomes.size()));

            while (parent1 == parent2) {
                parent2 = chromosomes.get(random.nextInt(chromosomes.size()));
            }

            char[] offspring1Genes = new char[parent1.genes.length];
            char[] offspring2Genes = new char[parent2.genes.length];

            for (int i = 0; i < parent1.genes.length; i++) {
                if (random.nextBoolean()) {
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

    public void mutateGenes(char[] genes, Random random) {
        for (int i = 0; i < genes.length; i++) {
            if (!puzzle.isLockedPosition(i)) {
                if (random.nextDouble() < 0.03) { // gw ganti (tadinya 0.05)
                    genes[i] = (genes[i] == 'W') ? 'B' : 'W';
                }
            }
        }
    }

    public boolean hasSolution() {
        return bestFitness == 0;
    }

    public class Chromosome {
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

        public char[] getGenes() {
            return genes;
        }

        public void itprints() {
            double len = Math.sqrt(genes.length);
            System.out.println(len);
            for (int i = 0; i < len; i++) {
                for (int j = 0; j < len; j++) {
                    System.out.print(genes[i * (int) len + j] + " ");
                }
                System.out.println();
            }
        }

        public void randomize() {
            Random rand = new Random(12345);
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
            this.value = calculate(); // Calculate fitness immediately upon initialization
        }

        public double calculate() {
            char[] board = chromosome.genes;
            int size = (int) Math.sqrt(board.length);
            int checkerboardPenalty = 0;
            int violationPenalty = 0;
            int islandPenalty = 0;

            int bobotCP = 3;
            int bobotVP = 6;
            int bobotIP = 4;

            // Combined loop for 2x2 blocks and checkerboard patterns
            for (int row = 0; row < size - 1; row++) {
                for (int col = 0; col < size - 1; col++) {
                    char current = board[row * size + col];
                    char right = board[row * size + col + 1];
                    char below = board[(row + 1) * size + col];
                    char diagonal = board[(row + 1) * size + col + 1];

                    // Penalize 2x2 blocks of the same color
                    if (current == right && current == below && current == diagonal) {
                        violationPenalty++;
                    }

                    // Penalize checkerboard patterns
                    if ((current == 'W' && right == 'B' && below == 'B' && diagonal == 'W') ||
                            (current == 'B' && right == 'W' && below == 'W' && diagonal == 'B')) {
                        checkerboardPenalty++;
                    }
                }
            }

            // Use Union-Find to count islands
            UnionFind uf = new UnionFind(board.length);
            for (int i = 0; i < board.length; i++) {
                if (i % size != size - 1 && board[i] == board[i + 1]) {
                    uf.union(i, i + 1); // Union with right neighbor
                }
                if (i < board.length - size && board[i] == board[i + size]) {
                    uf.union(i, i + size); // Union with bottom neighbor
                }
            }

            int whiteIslands = 0, blackIslands = 0;
            for (int i = 0; i < board.length; i++) {
                if (board[i] == 'W' && uf.find(i) == i) {
                    whiteIslands++;
                } else if (board[i] == 'B' && uf.find(i) == i) {
                    blackIslands++;
                }
            }

            // Penalize excess islands
            islandPenalty = (whiteIslands > 1 ? whiteIslands - 1 : 0) +
                    (blackIslands > 1 ? blackIslands - 1 : 0);

            // Calculate total fitness
            return -(bobotCP * checkerboardPenalty) - (bobotVP * violationPenalty) - (bobotIP * islandPenalty);
        }

        public double getValue() {
            return value;
        }

        // Union-Find data structure for efficient island counting
        private static class UnionFind {
            private int[] parent;
            private int[] rank;

            public UnionFind(int size) {
                parent = new int[size];
                rank = new int[size];
                for (int i = 0; i < size; i++) {
                    parent[i] = i; // Each element is its own parent
                    rank[i] = 0; // Initial rank is zero
                }
            }

            public int find(int p) {
                if (parent[p] != p) {
                    parent[p] = find(parent[p]); // Path compression
                }
                return parent[p];
            }

            public void union(int p, int q) {
                int rootP = find(p);
                int rootQ = find(q);
                if (rootP != rootQ) {
                    // Union by rank
                    if (rank[rootP] < rank[rootQ]) {
                        parent[rootP] = rootQ;
                    } else if (rank[rootP] > rank[rootQ]) {
                        parent[rootQ] = rootP;
                    } else {
                        parent[rootQ] = rootP;
                        rank[rootP]++;
                    }
                }
            }
        }
    }
}