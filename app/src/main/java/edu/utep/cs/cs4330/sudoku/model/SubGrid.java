package edu.utep.cs.cs4330.sudoku.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */

public class SubGrid {
    private List<Square> squares;
    protected int subGridLocation;

    public SubGrid(int subGridLocation) {
        this.squares = new ArrayList<>();
        this.subGridLocation = subGridLocation;
    }

    public void addSquare(Square square) { squares.add(square); }

    public void setSquareValue(Square square, int value) {
        for(Square s : squares) {
            if(s.getX() == square.getX() && s.getY() == square.getY()) {
                s.setValue(value);
            }
        }
    }

    public boolean containsSquare(Square square) {
        for(Square s : squares) {
            if(s.getX() == square.getX() && s.getY() == square.getY()) {
                return true;
            }
        }
        return false;
    }

    public boolean containsValue(int value) {
        for (Square s: squares) {
            if(s.getValue() == value) return true;
        }
        return false;
    }

    protected Collection<Square> getSubGridSquares() { return this.squares; }
}
