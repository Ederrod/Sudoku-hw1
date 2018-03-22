package edu.utep.cs.cs4330.sudoku;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.utep.cs.cs4330.sudoku.model.Board;
import edu.utep.cs.cs4330.sudoku.model.Square;

public class SudokuBoardActivity extends AppCompatActivity {
    private String FORMAT = "http://www.cs.utep.edu/cheon/ws/sudoku/new/?size=%d&level=%d";

    private int boardSize;
    private int boardLevel;

    private Board board;
    private BoardView boardView;

    private Button buttonSolve;

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
        setContentView(R.layout.activity_sudoku_board);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        Intent intent = getIntent();
        int level = Integer.parseInt(sp.getString("board_level", ""));
        int size = Integer.parseInt(sp.getString("board_size", ""));
        boardLevel = level;
        boardSize = size;

        board = new Board(boardSize);
        boardView = findViewById(R.id.boardView);
        boardView.setBoard(board);
        boardView.addSelectionListener(this::squareSelected);
        buttonSolve = findViewById(R.id.buttonSolve);
        buttonSolve.setOnClickListener(e-> solveClicked());

        numberButtons = new ArrayList<>(numberIds.length);
        for (int i = 0; i < numberIds.length; i++) {
            final int number = i; // 0 for delete button
            View button = findViewById(numberIds[i]);
            if(number > boardSize) {
                button.setEnabled(false);
                continue;
            }
            button.setOnClickListener(e -> numberClicked(number));
            numberButtons.add(button);
            setButtonWidth(button);
        }
        new GetJSONTask().execute(String.format(FORMAT,boardSize,boardLevel));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int size = Integer.parseInt(sp.getString("board_size", ""));
        int level = Integer.parseInt(sp.getString("board_level", ""));

        Log.d("Size: " , String.format("%d",size));
        Log.d("Level: " , String.format("%d",level));

        if(boardSize == size) {
            if(boardLevel != level){
                board = new Board(boardSize);
                boardView.setBoard(board);
                new GetJSONTask().execute(String.format(FORMAT,boardSize,level));
                boardView.invalidate();
            }
        }else {
            board = new Board(size);
            boardView.setBoard(board);
            new GetJSONTask().execute(String.format(FORMAT,size,boardLevel));
            for(View button: numberButtons) {
                int id = getButtonIdNumber(button);
                if(size < boardSize) {
                    if(id > size) {
                        button.setEnabled(false);
                    }
                }else {
                    if(!button.isEnabled()) {
                        button.setEnabled(true);
                    }
                }
            }
            boardView.invalidate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1,1,1,"Settings");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 1) {
            Intent intent = new Intent(this, SudokuSettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }


    private int getButtonIdNumber(View button) {
        switch (button.getId()) {
            case R.id.n0:
                return 0;
            case R.id.n1:
                return 1;
            case R.id.n2:
                return 2;
            case R.id.n3:
                return 3;
            case R.id.n4:
                return 4;
            case R.id.n5:
                return 5;
            case R.id.n6:
                return 6;
            case R.id.n7:
                return 7;
            case R.id.n8:
                return 8;
            case R.id.n9:
                return 9;
            default:
                return -1;
        }
    }
    public void solveClicked() {
        if(board.Solve()){
            boardView.invalidate();
        }
        else{
            toast("Current board isn't solveable.");
        }
    }

    /** Callback to be invoked when the new button is tapped. */
    public void newClicked(View view) {
        if(!board.gameInProgress()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(board.gameSolved()) {
            builder.setTitle("Congratulations!!");
            builder.setMessage("You have solved the puzzle! Click \"New Game\" for a new challenge.");
        }else {
            builder.setTitle("Game in progress!!");
            builder.setMessage("There is still a game in progress! Click \"New Game\" for a different challenge.");
        }
        builder.setCancelable(false);

        builder.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                board = new Board(9);
                boardView.setBoard(board);
                new GetJSONTask().execute(String.format(FORMAT,boardSize,boardLevel));
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

        if(!board.validateRow(selectedSquare,n)) {
            toast(String.format("Row already contains %d!",n));
            return;
        }
        if(!board.validateColumn(selectedSquare,n)){
            toast(String.format("Column already contains %d!",n));
            return;
        }
        if(!board.validateSubGrid(selectedSquare,n)) {
            toast(String.format("Sub grid already contains %d!",n));
            return;
        }
        board.setSquareValue(selectedSquare, n, false);
        for(View button : numberButtons){
            if(!button.isEnabled()) {
                button.setEnabled(true);
            }
        }
        boardView.invalidate();
    }

    /**
     * Callback to be invoked when a square is selected in the board view.
     *
     * @param
     */
    private void squareSelected(Square square) {
        for(View button : numberButtons){
            int number = getButtonIdNumber(button);
            if(!board.validateColumn(square,number)
                    ||!board.validateRow(square,number)
                    || !board.validateSubGrid(square,number)){
                button.setEnabled(false);
            }else {
                button.setEnabled(true);
            }
        }
        boardView.selectedSquare = square;
        boardView.invalidate();
    }

    private void setDefaultSquares(String response) {
        toast(response);
        board.setDefaultSquareValues(response);
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

    private class GetJSONTask extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;

        // onPreExecute called before the doInBackgroud start for display
        // progress dialog.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(SudokuBoardActivity.this, "", "Loading", true,
                    false); // Create and show Progress dialog
        }

        @Override
        protected String doInBackground(String... urls) {

            try {
                return WebClient.sendGet(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the doInBackgroud and also we
        // can hide progress dialog.
        @Override
        protected void onPostExecute(String result) {
            pd.dismiss();
            setDefaultSquares(result);
            boardView.invalidate();
        }
    }
}
