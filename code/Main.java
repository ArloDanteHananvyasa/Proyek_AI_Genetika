package code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // ganti path sendiri
        String filePath = "/F:/Campus Stuff/Codes/AI/YinYang/YinYang/board2.txt";
        init(filePath);
    }

    private static void init(String filePath) {
        char[][] board;

        try (BufferedReader bReader = new BufferedReader(new FileReader(filePath))) {
            System.out.println("reading file: " + filePath);

            // baca line pertama
            String firstLine = bReader.readLine();
            if (firstLine == null || firstLine.isEmpty()) {
                throw new IllegalArgumentException("empty board");
            }

            // split per char
            String[] values = firstLine.split(" ");
            int size = values.length;
            board = new char[size][size];

            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    firstLine = bReader.readLine();
                    values = firstLine.split(" ");
                }
                if (values.length != size) {
                    throw new IllegalArgumentException("board isnt square");
                }
                for (int j = 0; j < size; j++) {
                    board[i][j] = values[j].charAt(0);
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

            YinYangPuzzle yyp = new YinYangPuzzle(board);
            GeneticSolution solution = new GeneticSolution(yyp);
            solution.solve();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }
}
