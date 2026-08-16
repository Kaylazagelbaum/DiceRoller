package com.example.termproject.activities.models;

import com.example.termproject.R;

import nl.dionsegijn.konfetti.xml.KonfettiView;

import java.io.Serializable;

public class TermProject implements Serializable {
    public static final int ROWS = 6;
    public static final int COLS = 7;

    private int[][] board;
    private int currentPlayer; // 1 or 2
    private boolean gameOver;

    public TermProject() {
        reset();
    }

    public void reset() {
        board = new int[ROWS][COLS];
        currentPlayer = 1;
        gameOver = false;
    }

    public int placeToken(int col) {
        if (gameOver || col < 0 || col >= COLS) {
            return -1;
        }

        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][col] == 0) {
                board[row][col] = currentPlayer;
                int placedRow = row;
                
                if (checkWin(placedRow, col)) {
                    gameOver = true;
                } else {
                    switchPlayer();
                }
                
                return placedRow;
            }
        }
        return -1; // Column full
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getCell(int row, int col) {
        return board[row][col];
    }

    private boolean checkWin(int row, int col) {
        int player = board[row][col];
        return checkHorizontal(row, player) ||
                checkVertical(col, player) ||
                checkDiagonal1(row, col, player) ||
                checkDiagonal2(row, col, player);
    }

    private boolean checkHorizontal(int row, int player) {
        int count = 0;
        for (int col = 0; col < COLS; col++) {
            if (board[row][col] == player) {
                count++;
                if (count == 4) return true;
            } else {
                count = 0;
            }
        }
        return false;
    }

    private boolean checkVertical(int col, int player) {
        int count = 0;
        for (int row = 0; row < ROWS; row++) {
            if (board[row][col] == player) {
                count++;
                if (count == 4) return true;
            } else {
                count = 0;
            }
        }
        return false;
    }

    private boolean checkDiagonal1(int row, int col, int player) {
        int count = 0;
        int r = row;
        int c = col;

        while (r > 0 && c > 0) {
            r--;
            c--;
        }
        while (r < ROWS && c < COLS) {
            if (board[r][c] == player) {
                count++;
                if (count == 4) return true;
            } else {
                count = 0;
            }
            r++;
            c++;
        }
        return false;
    }

    private boolean checkDiagonal2(int row, int col, int player) {
        int count = 0;
        int r = row;
        int c = col;

        while (r < ROWS - 1 && c > 0) {
            r++;
            c--;
        }
        while (r >= 0 && c < COLS) {
            if (board[r][c] == player) {
                count++;
                if (count == 4) return true;
            } else {
                count = 0;
            }
            r--;
            c++;
        }
        return false;
    }
}
