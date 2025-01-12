package code;

import java.util.ArrayList;
import java.util.List;

public class YinYangPuzzle {
    private char[] board;
    private List<Integer> locked;

    public YinYangPuzzle(char[][] input) {
        this.locked = new ArrayList<>();
        this.board = new char[input.length * input.length];

        int boardItr = 0;
        for (int rows = 0; rows < input.length; rows++) {
            for (int cols = 0; cols < input[0].length; cols++) {
                board[boardItr] = input[rows][cols];
                if (input[rows][cols] != '.') {
                    this.locked.add(boardItr);
                }
                boardItr++;
            }
        }
    }

    public boolean isLockedPosition(int index) {
        return locked.contains(index);
    }

    public char[] getBoard() {
        return board;
    }

    public int getBoardSize() {
        return board.length;
    }
}
