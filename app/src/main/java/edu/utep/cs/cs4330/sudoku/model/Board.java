package edu.utep.cs.cs4330.sudoku.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** An abstraction of Sudoku puzzle. */
public class Board {

    /** Size of this board (number of columns/rows). */
    public final int size;

    private List<SubGrid> subGrids;

    private List<Square> squares;

    /** Create a new board of the given size. */
    public Board(int size) {
        this.size = size;

        subGrids = new ArrayList<>();
        initSubGrids();

        squares = new ArrayList<>();
        initSquares();
    }

    private void initSubGrids() {
        for(int i = 0; i < size; i++) {
            subGrids.add(new SubGrid(i));
        }
    }

    private void initSquares() {
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                squares.add(new Square(i,j));
            }
        }
    }
    /** @return The size of this board. */
    public int size() {
    	return size;
    }

    /** @return All squares cells contained in the board. */
    public Collection<Square> getSquares() { return this.squares; }

    /**
     *
     * @param x 0-based x-coordinate of the square.
     * @param y 0-based y-coordinate of the square.
     * @param n value to be set in square.
     */
    public void setSquareValue(int x, int y, int n) {
        if(n == 0) n = -1;
        for(Square square : squares) {
            if((square.getX() == x) && (square.getY() == y)) {
                square.setValue(n);
                addSquareToSubGrid(square);
            }
        }
    }

    /**
     *
     * @param square square to be added in to a sub-grid.
     */
    private void addSquareToSubGrid(Square square) {
        // TODO: Get location of the sub-grid where square should go.
        // TODO: Add square to the corresponding square.
    }

    /**
     *
     * @param y 0-based y-coordinate of the grid row.
     * @param n value to be checked.
     * @return true if value isn't contained in row y.
     */
    public boolean validateRow(int y, int n) {
        for(Square square : squares) {
            if(square.getY() == y) {
                if(square.getValue() == n) return false;
            }
        }
        return true;
    }

    public boolean validateColumn(int x, int n) {
        for(Square square : squares) {
            if (square.getX() == x) {
                if (square.getValue() == n) return false;
            }
        }
        return true;
    }

    public boolean validateSubGrid(int x, int y, int n) {
        int startRow = (y%3 == 0)? y : y - (y%3);
        int startColumn = (x%3 == 0)? x : x - (x%3);
        int endRow = startRow + 3;
        int endColumn = startColumn + 3;


        return true;
    }

    public boolean gameInProgress() {
        for(Square square : squares) {
            if(square.getValue() > 0) return true;
        }
        return false;
    }

    public boolean gameSolved() {

        return true;
    }
}