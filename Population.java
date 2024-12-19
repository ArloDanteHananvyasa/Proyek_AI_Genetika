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
            chromosomes.add(chromosome);
        }
    }

    public void evaluateFitness() {
        for (Chromosome chromosome : chromosomes) {
            chromosome.evaluateFitness();
        }
    }

    public void selection() {
        // Example: Implement selection (e.g., tournament or roulette selection)
    }

    public void crossover() {
        // Example: Implement crossover between chromosomes
    }

    public void mutation() {
        // Example: Implement mutation logic for chromosomes
    }

    public boolean hasSolution() {
        // Example: Implement a check to see if an ideal solution has been found
        return false;
    }

    class Chromosome {
        private char[] genes;
        private Fitness fitness;

        public Chromosome(YinYangPuzzle puzzle) {
            this.genes = new char[puzzle.getBoardSize()];
            this.genes = puzzle.getBoard();
            this.fitness = new Fitness(this);
        }

        public void randomize() {
            Random rand = new Random();
            for (int i = 0; i < genes.length; i++) {
                if (!puzzle.isLockedPosition(i)) {
                    genes[i] = rand.nextBoolean() ? 'W' : 'B'; // Example gene assignment
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
            int islandPenalty = 0;
            int violationPenalty = 0;
            int bobotCP = 5;
            int bobotVP = 10;
            int bobotIP = 5;
            
            // Penalti Checkerboard Pattern
            for (int row = 0; row < size - 1; row++) {
                for (int col = 0; col < size - 1; col++) {
                    char current = board[row * size + col];
                    char right = board[row * size + col + 1];
                    char below = board[(row + 1) * size + col];
                    char diagonal = board[(row + 1) * size + col + 1];

                    // Penalti jika mengikuti pola checkerboard
                    if ((current == 'W' && right == 'B' && below == 'B' && diagonal == 'W') ||
                        (current == 'B' && right == 'W' && below == 'W' && diagonal == 'B')) {
                        checkerboardPenalty++;
                    }
                    // Penalti 4 kotak warna sama
                    if (current == right && current == below && current == diagonal) {
                        violationPenalty++;
                    }
                }
            }

            // Itung ada berapa island
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
    
            // Penalty kalau island berlebih
            islandPenalty = (whiteIslands > 1 ? whiteIslands - 1 : 0) + 
                            (blackIslands > 1 ? blackIslands - 1 : 0);
    
            // Calculate total fitness
            value = -(bobotCP * checkerboardPenalty) - (bobotVP * violationPenalty) - (bobotIP * islandPenalty);
            
        }
    
        public double getValue() {
            return value;
        }
    
        // Explore island dengan DFS
        private void exploreIsland(char[] board, int size, int index, char color, boolean[] visited) {
            if (index < 0 || index >= board.length || visited[index] || board[index] != color) {
                return;
            }
    
            visited[index] = true;
    
            int row = index / size;
            int col = index % size;
    
            // Check 4 tetangganya
            if (row > 0) exploreIsland(board, size, index - size, color, visited); // up
            if (row < size - 1) exploreIsland(board, size, index + size, color, visited); // down
            if (col > 0) exploreIsland(board, size, index - 1, color, visited); // left
            if (col < size - 1) exploreIsland(board, size, index + 1, color, visited); // right
        }
    }
}
