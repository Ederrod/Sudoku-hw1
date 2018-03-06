package edu.utep.cs.cs4330.sudoku.model;

/**
 * Created by ederr on 3/4/2018.
 */

public class Square {
    private final int X;
    private final int Y;

    private int value;

    private boolean isSet;

    public Square(int X, int Y) {
        this.X = X;
        this.Y = Y;
        this.value = -1;
        this.isSet = false;
    }

    public void setValue(int value) { this.value = value; }
    public void setIsSet(boolean isSet) { this.isSet = isSet; }

    public int getX() { return this.X; }
    public int getY() { return this.Y; }
    public int getValue() { return this.value; }
    public boolean getIsSet() { return this.isSet; }
}
