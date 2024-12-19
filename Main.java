import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        //ganti pake path sendiri
        String filePath = "C:\\Users\\...\\Documents\\...\\Proyek_AI_Genetika\\board1.txt";
        init(filePath);
    }

    private static void init(String filePath) {
        // System.out.println("How many rows and columns on the board? (Enter a number between 6 - 24):");

        char[][] board;

        try (BufferedReader bReader = new BufferedReader(new FileReader(filePath))) {
            System.out.println("Reading the board from file: " + filePath);

            String firstLine = bReader.readLine();
            if (firstLine == null || firstLine.isEmpty()) {
                throw new IllegalArgumentException("The board file is empty or invalid.");
            }

            String[] values = firstLine.split(" ");
            int size = values.length; // Assume square board, size is the number of columns in the first row
            board = new char[size][size];


            for (int i = 0; i < size; i++) {
                if (i > 0) { // Read subsequent lines
                    firstLine = bReader.readLine();
                    if (firstLine == null) {
                        throw new IllegalArgumentException("The board file has incomplete rows.");
                    }
                    values = firstLine.split(" ");
                }
                if (values.length != size) {
                    throw new IllegalArgumentException("The board file is not a valid square board.");
                }
                for (int j = 0; j < size; j++) {
                    board[i][j] = values[j].charAt(0);
                }
            }
             
            System.out.println("Board size: " + size + "x" + size);

            System.out.println("Board read from file:");
            for (char[] row : board) {
                for (char value : row) {
                    System.out.print(value + " ");
                }
                System.out.println();
            }

            // YinYangPuzzle yyp = new YinYangPuzzle(board);
            // GeneticSolution solution = new GeneticSolution(yyp);
            // solution.solve(); // Solve the puzzle using the genetic algorithm

        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.toString());
        }

    }
}
