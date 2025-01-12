package code;

import java.util.*;

public class Population {
    private int size;
    private List<Chromosome> chromosomes;
    private YinYangPuzzle puzzle;
    private double bestFitness;
    private Random random;

    public Population(int size, YinYangPuzzle puzzle, Random random) {
        this.size = size;
        this.puzzle = puzzle;
        this.chromosomes = new ArrayList<>();
        this.bestFitness = Double.NEGATIVE_INFINITY;
        this.random = random;
    }

    public List<Chromosome> getChromosomes() {
        return chromosomes;
    }

    public void generateInitialPopulation() {
        for (int i = 0; i < size; i++) {
            Chromosome chromosome = new Chromosome(puzzle);
            chromosome.randomize(random);
            this.chromosomes.add(chromosome);
        }
    }

    public String evaluateFitness() {
        String board = "";
        Chromosome bestChromosome = null;
        double highestFitness = Double.NEGATIVE_INFINITY;

        for (Chromosome chromosome : chromosomes) {
            chromosome.evaluateFitness();
            if (chromosome.getFitnessValue() > highestFitness) {
                highestFitness = chromosome.getFitnessValue(); // Update highest fitness value
                bestChromosome = chromosome; // Update best chromosome
            }
        }
        if (bestChromosome != null) {
            board = bestChromosome.itprints();
        }
        if (highestFitness > bestFitness) {
            bestFitness = highestFitness;
        }
        return board;
    }

    public void rankSelection(double elitism) { // rank selection untuk pemilihan parent

        chromosomes.sort((c2, c1) -> Double.compare(c2.getFitnessValue(), c1.getFitnessValue())); // sort chromosome
                                                                                                  // berdasarkan fitness
                                                                                                  // valuenya

        int[] ranks = new int[chromosomes.size()]; // buat ranking untuk population
        for (int i = 0; i < chromosomes.size(); i++) {
            ranks[i] = i + 1;
        }

        int totalRankSum = Arrays.stream(ranks).sum(); // jumlahkan semua untuk rentang probabilitas
        List<Chromosome> parents = new ArrayList<>(); // init parents yang akan dipilih

        while (parents.size() < size * elitism) { // pilih parents hingga ditemukan sesuai dengan elitism rate (karena
                                                  // semua parent masuk ke gen berikutnya)
            int randomValue = random.nextInt(totalRankSum) + 1; // value di antara jumlah rank untuk pemilihan winner
            int sum = 0;// total penambahan rank
            for (int i = 0; i < chromosomes.size(); i++) { // iterasi dan cek setiap chromosome di population
                sum += ranks[i]; // tambahkan ranking ke sum
                if (sum >= randomValue) { // jika sum lebih besar dari randomValue maka chromosome ke i tersebut
                                          // mennjadi parent (chromosome rank lebih tinggi lebih mungkin untuk dipillih,
                                          // karena semakin mungkin tercakup random value)
                    if (!parents.contains(chromosomes.get(i))) { // skip masukan parent ke parents jika sudah ada
                        parents.add(chromosomes.get(i));
                        break;
                    }
                }
            }
        }
        chromosomes = parents;
    }

    public void versusSelection(double elitism) { // tourney selection, pilih 2 chromosome random kemudian bandingkan,
                                                  // pilih yang lebih baik jadi parent
        List<Chromosome> parents = new ArrayList<>();
        while (parents.size() < size * elitism) { // pilih parents hingga ditemukan sesuai dengan elitism rate (karena
                                                  // semua parent masuk ke gen berikutnya)
            Chromosome parent1 = chromosomes.get(random.nextInt(chromosomes.size())); // pilih 2 chromosome random
            Chromosome parent2 = chromosomes.get(random.nextInt(chromosomes.size()));
            while (parent1 == parent2) {// jika kedua parent sama, loop hingga didapatkan yang berbeda
                parent2 = chromosomes.get(random.nextInt(chromosomes.size()));
            }

            Chromosome winner = (parent1.getFitnessValue() > parent2.getFitnessValue()) ? parent1 : parent2; // pilih
                                                                                                             // parent
                                                                                                             // dengan
                                                                                                             // fitness
                                                                                                             // value
                                                                                                             // lebih
                                                                                                             // besar
            if (!parents.contains(winner)) { // skip masukan parent ke parents jika sudah ada
                parents.add(winner);
            }
        }
        chromosomes = parents; // populasi sekarang menjadi parents
    }

    public void simpleTournamentSelection(double elitism) {
        int tournamentSize = 5; // banyak crhomosome per bracket tourney
        List<Chromosome> parents = new ArrayList<>();

        while (parents.size() < size * elitism) {// size / 2 karena 50% jadi parent
            List<Chromosome> contestants = new ArrayList<>();// chromosome per bracket
            while (contestants.size() < tournamentSize) { // tambahkan crhomosome sbyk tourney size ke contestants
                Chromosome contestant = chromosomes.get(random.nextInt(chromosomes.size())); // pilih random
                if (!contestants.contains(contestant)) {// skip jika sebelumnnya sudah dimasukkan
                    contestants.add(contestant);
                }
            }
            Chromosome winner = contestants.stream().max(Comparator.comparingDouble(Chromosome::getFitnessValue))
                    .orElseThrow(); // pilih pemenang dari bracket tourney
            if (!parents.contains(winner)) { // skip masukan parent ke parents jika sudah ada
                parents.add(winner);
            }
        }
        chromosomes = parents;// populasi sekarang menjadi parents
    }

    public void singlePointCrossover(double crossoverRate) { // single point crossover
        while (chromosomes.size() < size) { // tambahkan hasil crossover ke population hingga penuh
            Chromosome parent1 = chromosomes.get(random.nextInt(chromosomes.size())); // pilih 2 parent random (dari
                                                                                      // hasil selection sebelumnya)
            Chromosome parent2 = chromosomes.get(random.nextInt(chromosomes.size()));

            while (parent1 == parent2) { // jika kedua parent sama, loop hingga didapatkan yang berbeda
                parent2 = chromosomes.get(random.nextInt(chromosomes.size()));
            }

            int crossoverPoint = random.nextInt(parent1.genes.length); // randomize crossover point

            char[] offspring1Genes = new char[parent1.genes.length]; // init offspring
            char[] offspring2Genes = new char[parent2.genes.length];

            for (int i = 0; i < parent1.genes.length; i++) {// masukan alele parent ke gene offpsring
                if (i < crossoverPoint) { // crossover point sebagai penanda untuk tukar parent
                    offspring1Genes[i] = parent1.genes[i];
                    offspring2Genes[i] = parent2.genes[i];
                } else {
                    offspring1Genes[i] = parent2.genes[i];
                    offspring2Genes[i] = parent1.genes[i];
                }
            }

            mutateGenes(offspring1Genes); // mutasi gene
            mutateGenes(offspring2Genes);

            if (crossoverRate >= this.random.nextDouble()) {
                chromosomes.add(new Chromosome(puzzle, offspring1Genes));// tambahkan offspring ke population
                chromosomes.add(new Chromosome(puzzle, offspring2Genes));
            } else {
                chromosomes.add(parent1);
                chromosomes.add(parent2);

            }
        }

        while (chromosomes.size() > size) { // hapus offspring berlebih (karena hasil / 2 dari metode selection parent
                                            // bisa ganjil dan metode crossover selalu menambahkan 2 offspring)
            chromosomes.remove(chromosomes.size() - 1);
        }
    }

    public void twoPointCrossover(double crossoverRate) {
        while (chromosomes.size() < size) {// tambahkan hasil crossover ke population hingga penuh
            Chromosome parent1 = chromosomes.get(random.nextInt(chromosomes.size()));// pilih 2 parent random (dari
                                                                                     // hasil selection sebelumnya)
            Chromosome parent2 = chromosomes.get(random.nextInt(chromosomes.size()));

            while (parent1 == parent2) {// parent tidak boleh sama
                parent2 = chromosomes.get(random.nextInt(chromosomes.size()));
            }

            int point1 = random.nextInt(parent1.genes.length);// ambil 2 point ramdom utk crossover
            int point2 = random.nextInt(parent1.genes.length);

            while (point1 == point2) {// point tidak boleh sama
                point2 = random.nextInt(parent1.genes.length);
            }

            if (point1 > point2) {// tukar point 1 dan 2 jika p1 lebih besar (point 2 selalu lebih besar)
                int temp = point1;
                point1 = point2;
                point2 = temp;
            }

            char[] offspring1Genes = parent1.genes.clone();// clone gene parent ke offspring untuk mengcopy agar tidak
                                                           // ikut terubah saat dimodifikasi
            char[] offspring2Genes = parent2.genes.clone();

            for (int i = point1; i < point2; i++) {
                offspring1Genes[i] = parent2.genes[i];// ganti alele antara crossover point 1 dan 2 menjadi milik parent
                                                      // yang lain
                offspring2Genes[i] = parent1.genes[i];
            }

            mutateGenes(offspring1Genes);// mutasi
            mutateGenes(offspring2Genes);
            if (crossoverRate >= this.random.nextDouble()) {
                chromosomes.add(new Chromosome(puzzle, offspring1Genes));// tambahkan offspring ke population
                chromosomes.add(new Chromosome(puzzle, offspring2Genes));
            } else {
                chromosomes.add(parent1);
                chromosomes.add(parent2);

            }

        }

        while (chromosomes.size() > size) { // hapus offspring berlebih (karena hasil / 2 dari metode selection parent
                                            // bisa ganjil dan metode crossover selalu menambahkan 2 offspring)
            chromosomes.remove(chromosomes.size() - 1);
        }
    }

    public void uniformCrossover(double crossoverRate) {
        while (chromosomes.size() < size) {// tambahkan hasil crossover ke population hingga penuh
            Chromosome parent1 = chromosomes.get(random.nextInt(chromosomes.size()));// pilih 2 parent random (dari
                                                                                     // hasil selection sebelumnya)
            Chromosome parent2 = chromosomes.get(random.nextInt(chromosomes.size()));

            while (parent1 == parent2) {// parent tidak boleh sama
                parent2 = chromosomes.get(random.nextInt(chromosomes.size()));
            }

            char[] offspring1Genes = new char[parent1.genes.length];// init offspring
            char[] offspring2Genes = new char[parent2.genes.length];

            for (int i = 0; i < parent1.genes.length; i++) {// masukan alele parent ke gene offpsring
                if (random.nextBoolean()) {// masukkan alele dari parents, 50% kemungkinan alele itu berasal dari salah
                                           // satu parent
                    offspring1Genes[i] = parent1.genes[i];
                    offspring2Genes[i] = parent2.genes[i];
                } else {
                    offspring1Genes[i] = parent2.genes[i];
                    offspring2Genes[i] = parent1.genes[i];
                }
            }

            mutateGenes(offspring1Genes);// mutasi gene
            mutateGenes(offspring2Genes);
            if (crossoverRate >= this.random.nextDouble()) {
                chromosomes.add(new Chromosome(puzzle, offspring1Genes));// tambahkan offspring ke population
                chromosomes.add(new Chromosome(puzzle, offspring2Genes));
            } else {
                chromosomes.add(parent1);
                chromosomes.add(parent2);

            }

        }

        while (chromosomes.size() > size) { // hapus offspring berlebih (karena hasil / 2 dari metode selection parent
                                            // bisa ganjil dan metode crossover selalu menambahkan 2 offspring)
            chromosomes.remove(chromosomes.size() - 1);
        }
    }

    public void mutateGenes(char[] genes) { // mutasi gene
        for (int i = 0; i < genes.length; i++) { // iterasi setiap gene
            if (!puzzle.isLockedPosition(i)) {// jika bukan posisi awal puzzle dan dapat diubah
                if (random.nextDouble() < 0.01) { // mutasi dengan probabilitas 1%
                    genes[i] = (genes[i] == 'W') ? 'B' : 'W'; // ubah menjadi sebaliknya
                }
            }
        }
    }

    public boolean hasSolution() {// check apakah ditemukan solusi di population
        return bestFitness == 1; // true jika bestfitness = 0
    }

    // Kelas yang merepresentasikan sebuah Chromosome dalam algoritma genetika
    public class Chromosome {
        private char[] genes; // Array gen yang merepresentasikan solusi puzzle
        private Fitness fitness; // Objek Fitness untuk menilai kualitas solusi (chromosome)

        // Konstruktor untuk membuat Chromosome berdasarkan puzzle YinYang
        public Chromosome(YinYangPuzzle puzzle) {
            this.genes = new char[puzzle.getBoardSize()]; // Inisialisasi array gen dengan ukuran papan
            this.genes = Arrays.copyOf(puzzle.getBoard(), puzzle.getBoard().length); // Salin data papan dari puzzle
            this.fitness = new Fitness(this); // Hitung fitness untuk chromosome ini
        }

        // Konstruktor untuk membuat Chromosome berdasarkan puzzle dan gen yang
        // diberikan
        public Chromosome(YinYangPuzzle puzzle, char[] genes) {
            this.genes = genes; // Gunakan array gen yang diberikan
            this.fitness = new Fitness(this); // Hitung fitness untuk chromosome ini
        }

        public char[] getGenes() {
            return genes;
        }

        // Metode untuk mencetak representasi papan puzzle
        public String itprints() {
            String board = ""; // String untuk menyimpan representasi papan
            double len = Math.sqrt(genes.length); // Panjang sisi papan (diasumsikan berbentuk persegi)
            for (int i = 0; i < len; i++) {
                for (int j = 0; j < len; j++) {
                    if (genes[i * (int) len + j] == 'W') {
                        System.out.print("O "); // Cetak "O" untuk warna putih
                    } else {
                        System.out.print(". "); // Cetak "." untuk warna hitam
                    }
                    board += genes[i * (int) len + j] + " "; // Tambahkan gen ke string board
                }
                board += "\n"; // Tambahkan baris baru di akhir setiap baris
                System.out.println(); // Cetak baris baru untuk output konsol
            }
            return board; // Kembalikan representasi papan sebagai string
        }

        // Metode untuk mengacak gen di posisi yang tidak terkunci
        public void randomize(Random random) {
            for (int i = 0; i < this.genes.length; i++) {
                if (!puzzle.isLockedPosition(i)) { // Periksa apakah posisi tidak terkunci
                    int randomChoice = random.nextInt(2) + 1; // Pilih angka acak antara 1 dan 2
                    if (randomChoice == 1) {
                        this.genes[i] = 'W'; // Tetapkan warna putih jika randomChoice == 1
                    } else {
                        this.genes[i] = 'B'; // Tetapkan warna hitam jika randomChoice != 1
                    }
                }
            }
        }

        // Metode untuk mengevaluasi ulang fitness dari chromosome ini
        public void evaluateFitness() {
            this.fitness.calculate();
        }

        // Getter untuk mengembalikan nilai fitness dari chromosome
        public double getFitnessValue() {
            return fitness.getValue();
        }
    }

    // kelas yang merepresentasikan nilai Fitness dari chromosome tertentu
    class Fitness {
        private double value;
        private Chromosome chromosome;

        public Fitness(Chromosome chromosome) {
            this.chromosome = chromosome;
            this.value = calculate(); // menghitung fitness langsung setelah insialisasi
        }

        public double calculate() {
            char[] board = chromosome.genes; // gen chromosome yang merepresentasikan papan
            int size = (int) Math.sqrt(board.length); // ukuran sisi papan (asumsi papan berbentuk persegi)
            int checkerboardPenalty = 0; // penalti untuk pola checkerboard
            int violationPenalty = 0; // penalti untuk blok 2x2 dengan warna yang sama
            int islandPenalty = 0; // penalti untuk jumlah pulau yang berlebihan

            double bobotCP = 0.1; // bobot penalti untuk pola checkerboard
            double bobotVP = 0.2; // bobot penalti untuk blok 2x2
            double bobotIP = 0.3; // bobot penalti untuk jumlah pulau

            // loop untuk memeriksa blok 2x2 dan pola checkerboard
            for (int row = 0; row < size - 1; row++) {
                for (int col = 0; col < size - 1; col++) {
                    char current = board[row * size + col];
                    char right = board[row * size + col + 1];
                    char below = board[(row + 1) * size + col];
                    char diagonal = board[(row + 1) * size + col + 1];

                    // Tambahkan penalti jika ditemukan blok 2x2 dengan warna yang sama (notes: pola
                    // checkerboard melanggar aturan permainan)
                    if (current == right && current == below && current == diagonal) {
                        violationPenalty++;
                    }

                    // Tambahkan penalti untuk pola checkerboard tertentu (notes: pada permainan
                    // apabila ditemukan pola checkerboard dalam puzzle, puzzle tersebut mustahil
                    // untuk ditemukan solusinya)
                    if ((current == 'W' && right == 'B' && below == 'B' && diagonal == 'W') ||
                            (current == 'B' && right == 'W' && below == 'W' && diagonal == 'B')) {
                        checkerboardPenalty++;
                    }
                }
            }

            // Gunakan Union-Find untuk menghitung jumlah pulau
            UnionFind uf = new UnionFind(board.length);
            for (int i = 0; i < board.length; i++) {
                if (i % size != size - 1 && board[i] == board[i + 1]) {
                    uf.union(i, i + 1); // hubungkan dengan tetangga kanan
                }
                if (i < board.length - size && board[i] == board[i + size]) {
                    uf.union(i, i + size); // hubungkan dengan tetangga bawah
                }
            }

            // Hitung jumlah pulau untuk setiap warna
            int whiteIslands = 0, blackIslands = 0;
            for (int i = 0; i < board.length; i++) {
                if (board[i] == 'W' && uf.find(i) == i) {
                    whiteIslands++;
                } else if (board[i] == 'B' && uf.find(i) == i) {
                    blackIslands++;
                }
            }

            // Tambahkan penalti untuk jumlah pulau yang melebihi 1 (notes : seharusnya
            // kalau merupakan solusi, hanya ada 1 buah pulai untuk masing-masing warna)
            islandPenalty = (whiteIslands > 1 ? whiteIslands - 1 : 0) +
                    (blackIslands > 1 ? blackIslands - 1 : 0);

            // Hitung total fitness (semakin negatif semakin buruk)
            return 1 / (1 + (bobotCP * checkerboardPenalty) + (bobotVP * violationPenalty) + (bobotIP * islandPenalty));
        }

        public double getValue() {
            return value;
        }

        // Kelas Union-Find untuk menghitung jumlah pulau dengan efisien
        private static class UnionFind {
            private int[] parent; // array untuk merepresentasikan parent dari setiap elemen
            private int[] rank; // array untuk menyimpan ranking (kedalaman pohon)

            public UnionFind(int size) {
                parent = new int[size]; // inisialisasi parent untuk setiap elemen
                rank = new int[size]; // inisialisasi rank ke 0
                for (int i = 0; i < size; i++) {
                    parent[i] = i; // setiap elemen adalah parent dari dirinya sendiri
                    rank[i] = 0; // rank awal adalah nol
                }
            }

            public int find(int p) {
                if (parent[p] != p) {
                    parent[p] = find(parent[p]); // path compression untuk optimisasi
                }
                return parent[p];
            }

            public void union(int p, int q) {
                int rootP = find(p);
                int rootQ = find(q);
                if (rootP != rootQ) {
                    // Gabungkan berdasarkan rank
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
