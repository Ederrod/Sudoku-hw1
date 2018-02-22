package edu.utep.cs.cs4330.sudoku.model;

import android.util.Log;

/** An abstraction of Sudoku puzzle. */
public class Board {

    /** Size of this board (number of columns/rows). */
    public final int size;

    public int[][] grid;


    /** Create a new board of the given size. */
    public Board(int size) {
        this.size = size;

        // WRITE YOUR CODE HERE ...
        grid = new int[size][size];
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                grid[i][j] = -1;
            }
        }
    }

    /** Return the size of this board. */
    public int size() {
    	return size;
    }

    // WRITE YOUR CODE HERE ..

    public void setSquareValue(int x, int y, int n) {
        if(n == 0) n = -1;
        grid[y][x] = n;
    }

    public boolean validateRow(int y, int n) {
        for(int i = 0; i < size; i++) {
            if(grid[y][i] == n) return false;
        }
        return true;
    }

    public boolean validateColumn(int x, int n) {
        for(int i = 0; i < size; i++) {
            if(grid[i][x] == n) return false;
        }
        return true;
    }

    public boolean validateSubGrid(int x, int y, int n) {
        int startRow = (y%3 == 0)? y : y - (y%3);
        int startColumn = (x%3 == 0)? x : x - (x%3);
        int endRow = startRow + 3;
        int endColumn = startColumn + 3;

        for(int i = startRow; i < endRow; i++) {
            for(int j = startColumn; j < endColumn; j++) {
                if(grid[i][j] == n) return false;
            }
        }
        return true;
    }

    public boolean gameInProgress() {
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(grid[i][j] > 0) return true;
            }
        }
        return false;
    }

    public boolean gameSolved() {
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(grid[i][j] == -1) return false;
            }
        }
        return true;
    }
}
