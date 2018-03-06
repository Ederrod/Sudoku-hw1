package edu.utep.cs.cs4330.sudoku;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.utep.cs.cs4330.sudoku.model.Board;
import edu.utep.cs.cs4330.sudoku.model.Square;

/**
 * HW1 template for developing an app to play simple Sudoku games.
 * You need to write code for three callback methods:
 * newClicked(), numberClicked(int) and squareSelected(int,int).
 * Feel free to improved the given UI or design your own.
 *
 * <p>
 *  This template uses Java 8 notations. Enable Java 8 for your project
 *  by adding the following two lines to build.gradle (Module: app).
 * </p>
 *
 * <pre>
 *  compileOptions {
 *  sourceCompatibility JavaVersion.VERSION_1_8
 *  targetCompatibility JavaVersion.VERSION_1_8
 *  }
 * </pre>
 *
 * @author Yoonsik Cheon
 */
public class MainActivity extends AppCompatActivity {

    private Board board;

    private BoardView boardView;

    private Button progressButton;

    /** All the number buttons. */
    private List<View> numberButtons;
    private static final int[] numberIds = new int[] {
            R.id.n0, R.id.n1, R.id.n2, R.id.n3, R.id.n4,
            R.id.n5, R.id.n6, R.id.n7, R.id.n8, R.id.n9
    };

    /** Width of number buttons automatically calculated from the screen size. */
    private static int buttonWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        board = new Board(9);
        boardView = findViewById(R.id.boardView);
        boardView.setBoard(board);
        boardView.addSelectionListener(this::squareSelected);
        progressButton = findViewById(R.id.progressButton);
        progressButton.setOnClickListener(this::progressClicked);

        numberButtons = new ArrayList<>(numberIds.length);
        for (int i = 0; i < numberIds.length; i++) {
            final int number = i; // 0 for delete button
            View button = findViewById(numberIds[i]);
            button.setOnClickListener(e -> numberClicked(number));
            numberButtons.add(button);
            setButtonWidth(button);
        }
    }

    public void progressClicked(View view) {
        if(board.gameSolved()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Congratulations!");
            builder.setMessage("You have solved the sudoku puzzle. " +
                    "Click the \"New\" button for another challenge.");

            builder.setCancelable(true);
        }else {
            toast("Game hasn't been solved.");
        }
    }

    /** Callback to be invoked when the new button is tapped. */
    public void newClicked(View view) {
        if(!board.gameInProgress()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game in progress!");
        builder.setMessage("There is still a game in porgress. Do you wan't to continue?");
        builder.setCancelable(false);

        builder.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                board = new Board(9);
                boardView.setBoard(board);
                boardView.invalidate();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /** Callback to be invoked when a number button is tapped.
     *
     * @param n Number represented by the tapped button
     *          or 0 for the delete button.
     */
    public void numberClicked(int n) {
        Square selectedSquare = boardView.selectedSquare;
        if(selectedSquare == null) return;

        int x = selectedSquare.getX();
        int y = selectedSquare.getY();

        if(!board.validateRow(y,n)) {
            toast(String.format("Row already contains %d!",n));
            return;
        }
        if(!board.validateColumn(x,n)){
            toast(String.format("Column already contains %d!",n));
            return;
        }
        if(!board.validateSubGrid(x,y,n)) {
            toast(String.format("Sub grid already contains %d!",n));
            return;
        }
        if(x != -1 && y != -1) {
            board.setSquareValue(x, y, n);
        }
        boardView.invalidate();
        //
    }

    /**
     * Callback to be invoked when a square is selected in the board view.
     *
     * @param
     */
    private void squareSelected(Square square) {
        boardView.selectedSquare = square;
        boardView.invalidate();
    }

    /** Show a toast message. */
    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /** Set the width of the given button calculated from the screen size. */
    private void setButtonWidth(View view) {
        if (buttonWidth == 0) {
            final int distance = 2;
            int screen = getResources().getDisplayMetrics().widthPixels;
            buttonWidth = (screen - ((9 + 1) * distance)) / 9; // 9 (1-9)  buttons in a row
        }
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = buttonWidth;
        view.setLayoutParams(params);
    }
}