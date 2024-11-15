import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        init(new BufferedReader(new InputStreamReader(System.in)), new Scanner(System.in));
    }

    private static void init(BufferedReader bReader, Scanner scanner) {
        System.out.println("How many rows and columns on the board? (Enter a number between 6 - 24):");

        int dimension = scanner.nextInt();
        char[][] board = new char[dimension][dimension];

        try {
            System.out.println("Input the board:");
            for (int rows = 0; rows < board.length; rows++) {
                String input = bReader.readLine();
                for (int cols = 0; cols < board.length; cols++) {
                    board[rows][cols] = input.charAt(cols);
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        YinYangPuzzle yyp = new YinYangPuzzle(board);
        GeneticSolution solution = new GeneticSolution(yyp);
        solution.solve(); // Solve the puzzle using the genetic algorithm
    }
}
