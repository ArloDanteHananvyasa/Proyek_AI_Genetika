# Proyek_AI_Genetika
//this documentation was generated with GPT

Proyek ini adalah sebuah aplikasi Java untuk memecahkan puzzle YinYang menggunakan Algoritma Genetika. Aplikasi ini menggunakan struktur data dan operasi genetik untuk mencari solusi terbaik bagi puzzle yang diberikan oleh pengguna.
Struktur Proyek

    Main Class: Menginisialisasi aplikasi dan menerima input puzzle dari pengguna.
    YinYangPuzzle Class: Merepresentasikan puzzle dan mengatur logika untuk membaca dan menyimpan papan permainan.
    GeneticSolution Class: Mengimplementasikan algoritma genetika untuk mencari solusi terbaik dari puzzle.
    Population Class: Mengelola populasi kromosom (solusi potensial) yang akan dievaluasi, dipilih, dikawinsilangkan, dan dimutasi dalam setiap generasi.
    Chromosome Class (dalam Population): Merepresentasikan solusi potensial individual.
    Fitness Class (dalam Population): Menghitung nilai fitness dari setiap kromosom.

Penjelasan Kelas dan Fungsinya
Main

Kelas utama yang bertanggung jawab untuk:

    Membaca dimensi papan dari input pengguna.
    Menginisialisasi kelas YinYangPuzzle dengan konfigurasi papan.
    Memulai proses pencarian solusi menggunakan algoritma genetika.

YinYangPuzzle

Kelas ini merepresentasikan puzzle YinYang dan menyimpan posisi kunci (posisi tetap) dalam papan permainan. Beberapa fungsi utama dalam kelas ini adalah:

    Konversi Papan: Mengonversi papan dari bentuk 2D ke bentuk 1D agar lebih mudah dikelola oleh algoritma.
    isLockedPosition: Mengecek apakah suatu posisi di papan adalah posisi yang dikunci atau posisi tetap yang tidak boleh diubah.
    getBoard dan getBoardSize: Mengembalikan data papan dan ukuran papan untuk diolah oleh algoritma genetika.

GeneticSolution

Kelas ini mengatur proses algoritma genetika, termasuk:

    solve: Metode utama yang menjalankan proses algoritma genetika dalam beberapa generasi untuk mencari solusi terbaik.
    Operasi Genetik: Mengatur operasi seperti inisialisasi populasi, seleksi, kawin silang (crossover), dan mutasi.

Population

Kelas Population bertanggung jawab untuk mengelola sekumpulan Chromosome, yaitu solusi potensial untuk puzzle. Fitur utama dari kelas ini termasuk:

    generateInitialPopulation: Membuat populasi awal dari kromosom secara acak.
    evaluateFitness: Mengevaluasi nilai fitness dari setiap kromosom dalam populasi.
    selection, crossover, mutation: Melakukan seleksi, kawin silang, dan mutasi untuk menciptakan generasi baru dari solusi potensial.
    hasSolution: Mengecek apakah sudah ditemukan solusi optimal.

Chromosome (di dalam Population)

Kelas Chromosome merepresentasikan satu solusi potensial dalam populasi. Fitur utama dari Chromosome adalah:

    randomize: Mengacak gen (posisi pada papan) untuk membuat solusi awal.
    evaluateFitness: Memanggil Fitness untuk menghitung nilai fitness dari kromosom berdasarkan aturan puzzle.

Fitness (di dalam Population)

Kelas Fitness bertanggung jawab untuk menghitung dan menyimpan nilai fitness dari setiap Chromosome, yang digunakan untuk mengevaluasi seberapa baik solusi tersebut dalam menyelesaikan puzzle. Fitness yang lebih tinggi menunjukkan solusi yang lebih mendekati hasil optimal.
Cara Kerja Algoritma Genetika

    Inisialisasi Populasi: Populasi awal kromosom dihasilkan secara acak.
    Evaluasi Fitness: Setiap kromosom dievaluasi menggunakan fungsi fitness untuk menentukan seberapa baik kromosom tersebut menyelesaikan puzzle.
    Seleksi: Kromosom dengan nilai fitness lebih tinggi dipilih untuk bereproduksi.
    Kawin Silang (Crossover): Kromosom terpilih dikombinasikan untuk menghasilkan kromosom baru yang memiliki sifat campuran dari kedua orang tua.
    Mutasi: Mutasi dilakukan pada beberapa kromosom untuk memperkenalkan variasi acak.
    Generasi Baru: Proses berulang hingga ditemukan solusi optimal atau batas generasi tercapai.
