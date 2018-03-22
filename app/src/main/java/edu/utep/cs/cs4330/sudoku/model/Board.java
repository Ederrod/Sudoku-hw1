package edu.utep.cs.cs4330.sudoku.model;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/** An abstraction of Sudoku puzzle. */
public class Board {

    /** Size of this board (number of columns/rows). */
    public final int size;

    /** Total number of subgrids. */
    public final int subGridSize;

    private List<SubGrid> subGrids;

    private List<Square> squares;

    /** Create a new board of the given size. */
    public Board(int size) {
        this.size = size;
        this.subGridSize = (int) Math.sqrt(size);
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
     * @param n value to be set in square.
     */
    public void setSquareValue(Square s, int n, boolean isSet) {
        if(n == 0) n = -1;
        for(Square square : squares) {
            if((square.getX() == s.getX())
                    && (square.getY() == s.getY())
                    && (!square.getIsSet())) {
                square.setValue(n);
                square.setIsSet(isSet);
                addSquareToSubGrid(square);
            }
        }
    }

    public void setDefaultSquareValues(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray squareList = jsonObject.getJSONArray("squares");
            String res = jsonObject.getString("response");
            if(res != "false") {
                for(int i = 0; i < squareList.length(); i++) {
                    JSONObject object = squareList.getJSONObject(i);
                    int x = object.getInt("x");
                    int y = object.getInt("y");
                    int value = object.getInt("value");

                    setSquareValue(new Square(x,y), value, true);
                }
            }else {
                Log.d("response: ", res);
            }
        } catch (JSONException e){
            Log.d("EXCEPTION: ","JSON EXCEPTION");
        }
    }

    /**
     *
     * @param square square to be added in to a sub-grid.
     */
    private void addSquareToSubGrid(Square square) {
        int row = square.getY();
        int col = square.getX();
        int loc = (row/subGridSize) * subGridSize + col/subGridSize;

        for(SubGrid subGrid : subGrids) {
            if(subGrid.subGridLocation == loc) {
                if(subGrid.containsSquare(square)) {
                    subGrid.setSquareValue(square, square.getValue());
                }else {
                    subGrid.addSquare(square);
                }
            }
        }
    }

    /**
     *
     * @param n value to be checked.
     * @return true if value isn't contained in row y.
     */
    public boolean validateRow(Square square, int n) {
        int y = square.getY();
        for(Square s : squares) {
            if(s.getY() == y) {
                if(s.getValue() == n) return false;
            }
        }
        return true;
    }

    public boolean validateColumn(Square square, int n) {
        int x = square.getX();
        for(Square s : squares) {
            if (s.getX() == x) {
                if (s.getValue() == n) return false;
            }
        }
        return true;
    }

    public boolean validateSubGrid(Square square, int n) {
        int row = square.getY();
        int col = square.getX();
        int loc = (row/subGridSize) * subGridSize + col/subGridSize;
        for(SubGrid s : subGrids){
            if(s.subGridLocation == loc) {
                if(s.containsValue(n)) return false;
            }
        }
        return true;
    }

    public boolean gameInProgress() {
        for(Square square : squares) {
            if(square.getValue() > 0) return true;
        }
        return false;
    }

    public boolean gameSolved() {
        for(Square square: squares) {
            if(square.getValue() == -1) {
                return false;
            }
        }
        return true;
    }

    public boolean Solve() {
        // Find empty square
        Square empty = findEmpty();

        // If there is no empty square, board has been solved.
        if(empty == null) {
            return true;
        }

        for(int i = 1; i <= size; i++) {
            if(validateColumn(empty,i)
                    && validateRow(empty,i)
                    && validateSubGrid(empty,i)){
                setSquareValue(empty,i,false);

                if(Solve()){
                    return true;
                }

                setSquareValue(empty,-1,false);
            }
        }

        return false;
    }

    public Square findEmpty() {
        for(Square square: squares) {
            if(square.getValue() == -1)
                return square;
        }
        return null;
    }
}