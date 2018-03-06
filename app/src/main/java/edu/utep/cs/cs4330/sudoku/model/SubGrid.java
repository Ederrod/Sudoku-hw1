package edu.utep.cs.cs4330.sudoku.model;

import java.util.Collection;
import java.util.List;

/**
 * Created by ederr on 3/5/2018.
 */

public class SubGrid {
    private List<Square> squares;
    protected int subGridLocation;

    public SubGrid(int subGridLocation) { this.subGridLocation = subGridLocation; }

    protected void addSquare(Square square) { squares.add(square); }

    protected Collection<Square> getSubGridSquares() { return this.squares; }
}
