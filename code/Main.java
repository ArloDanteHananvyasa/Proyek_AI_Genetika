package code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // ganti path sendiri
        String filePath = "/C:/Users/seba/Documents/hw/ai2/Proyek_AI_Genetika/board7.txt";
        int seed = 1234; //seed
        int populationSize = 5000;
        int generationSize = 200;
        init(filePath, seed, populationSize, generationSize);
    }

    private static void init(String filePath, int seed, int populationSize, int generationSize) {

        char[][] board;
        Random random = new Random(seed);

        try (BufferedReader bReader = new BufferedReader(new FileReader(filePath))) {//baca input dari file
            System.out.println("reading file: " + filePath);//print untuk cek

            // baca line pertama untuk cek apakah isi file kosong
            String line = bReader.readLine();
            if (line == null || line.isEmpty()) {
                throw new IllegalArgumentException("empty board");
            }

            // split line menjadi alele nya saja
            String[] values = line.split(" "); //split per spasi
            int size = values.length; //dapatkan size board dari banyaknya character yang di dapat
            board = new char[size][size];//init board kotak dengan size tersebut

            for (int i = 0; i < size; i++) {//cek tiap line
                if (i > 0) { //baca dari line kedua karena sebelumnya sudah didapatkan line pertama
                    line = bReader.readLine();
                    values = line.split(" ");
                }
                if (values.length != size) {//jika byk char yang diapat != size
                    throw new IllegalArgumentException("board isnt square");
                }
                for (int j = 0; j < size; j++) {
                    board[i][j] = values[j].charAt(0); //masukkan valuenya ke board
                }
            }

            System.out.println("board size: " + size);

            // print board untuk cek
            System.out.println("board:");
            for (char[] row : board) {
                for (char value : row) {
                    System.out.print(value + " ");
                }
                System.out.println();
            }
            System.out.println();

            YinYangPuzzle yyp = new YinYangPuzzle(board); //init puzzle
            GeneticSolution solution = new GeneticSolution(yyp, random, populationSize, generationSize); //init genetic solutionnya dengan random yang sudah memiliki seed
            solution.solve();

        } catch (IOException e) { //output error baca file
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }
}
