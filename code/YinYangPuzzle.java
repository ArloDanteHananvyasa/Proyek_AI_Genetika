package code;

import java.util.ArrayList;
import java.util.List;

public class YinYangPuzzle {
    private char[] board; //menyimpan papan ke dalam array 1D
    private List<Integer> locked; //menandai index ke berapa saja yang telah terisi dan tidak bisa diubah

    public YinYangPuzzle(char[][] input) {
        this.locked = new ArrayList<>();
        this.board = new char[input.length * input.length]; //panjang array adalah (panjang papan)^2

        int boardItr = 0;
        for (int rows = 0; rows < input.length; rows++) {
            for (int cols = 0; cols < input[0].length; cols++) {
                board[boardItr] = input[rows][cols];
                if (input[rows][cols] != '.') { //jika kotak bukan merupakan kotak yang bisa diisi, maka tandai indexnya
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
