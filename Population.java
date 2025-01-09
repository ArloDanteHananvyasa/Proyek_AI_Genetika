import java.util.*;

public class Population {
    private int size;
    private List<Chromosome> chromosomes;
    private YinYangPuzzle puzzle;

    public Population(int size, YinYangPuzzle puzzle) {
        this.size = size;
        this.puzzle = puzzle;
        this.chromosomes = new ArrayList<>();
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
        for (Chromosome chromosome : chromosomes) {
            // chromosome.itprints();
            chromosome.evaluateFitness();
        }
    }

    public void selection() { // rank selection, select 50% dari populasi jadi parent

        chromosomes.sort((c1, c2) -> Double.compare(c2.getFitnessValue(), c1.getFitnessValue()));

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

    public void crossover() {
        Random random = new Random();
        while (chromosomes.size() < size) {
            Chromosome parent1 = chromosomes.get(random.nextInt(chromosomes.size()));
            Chromosome parent2 = chromosomes.get(random.nextInt(chromosomes.size()));

            // mastiin ga sama
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

            chromosomes.add(new Chromosome(puzzle, offspring1Genes));
            chromosomes.add(new Chromosome(puzzle, offspring2Genes));
        }

    }

    public void mutation() {

    }

    public boolean hasSolution() {
        return false;
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
            for(int i = 0 ; i< genes.length;i++){
                System.out.print(genes[i]+" ");
            }
            System.out.println();
        }

        public void randomize() {
            Random rand = new Random();
            for (int i = 0; i < this.genes.length; i++) {
                if (!puzzle.isLockedPosition(i)) {
                    int randomChoice = rand.nextInt(2) + 1;
                    System.out.printf("%d ", randomChoice);
                    if (randomChoice == 1) {
                        this.genes[i] = 'W';
                    } else {
                        this.genes[i] = 'B';
                    }
                    // this.genes[i] = (randomChoice == 1) ? 'W' : 'B';
                    // System.out.println(randomChoice);
                }
            }
            System.out.println();  
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
            int islandPenalty = 0;
            int violationPenalty = 0;
            int bobotCP = 5;
            int bobotVP = 10;
            int bobotIP = 5;
            
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
                    System.out.printf("%c ", current);
                }
                System.out.println();
            }
            System.out.println();
            System.out.println();

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
            System.out.println(value);
            
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
