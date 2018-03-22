package edu.utep.cs.cs4330.sudoku;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {


    private int boardLevel;
    private int boardSize;

    private RadioButton sizeFour;
    private RadioButton sizeNine;

    private RadioButton levelEasy;
    private RadioButton levelMedium;
    private RadioButton levelHard;

    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        boardLevel = -1;
        boardSize = -1;

        sizeFour = findViewById(R.id.buttonSize4);
        sizeFour.setOnClickListener(e -> sizeFourClicked());
        sizeNine = findViewById(R.id.buttonSize9);
        sizeNine.setOnClickListener(e -> sizeNineClicked());

        levelEasy = findViewById(R.id.buttonEasy);
        levelEasy.setOnClickListener(e -> easyClicked());
        levelMedium = findViewById(R.id.buttonMedium);
        levelMedium.setOnClickListener(e -> mediumClicked());
        levelHard = findViewById(R.id.buttonHard);
        levelHard.setOnClickListener(e -> hardClicked());

        startButton = findViewById(R.id.buttonStart);
        startButton.setOnClickListener(e -> startClicked());
    }

    public void sizeFourClicked() {
        if(sizeNine.isChecked()) { sizeNine.setChecked(false); }
        boardSize = 4;
    }
    public void sizeNineClicked() {
        if(sizeFour.isChecked()) { sizeFour.setChecked(false); }
        boardSize = 9;
    }

    public void easyClicked() {
        if(levelMedium.isChecked()) { levelMedium.setChecked(false); }
        if(levelHard.isChecked()) { levelHard.setChecked(false); }
        boardLevel = 1;
    }
    public void mediumClicked() {
        if(levelEasy.isChecked()) { levelEasy.setChecked(false); }
        if(levelHard.isChecked()) { levelHard.setChecked(false); }
        boardLevel = 2;
    }
    public void hardClicked() {
        if(levelMedium.isChecked()) { levelMedium.setChecked(false); }
        if(levelEasy.isChecked()) { levelEasy.setChecked(false); }
        boardLevel = 3;
    }

    public void startClicked() {
        if(boardSize == -1 || boardLevel == -1) {
            toast("Can't start game with current board configuration!");
            return;
        }
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("board_size", String.format("%d",boardSize));
        edit.putString("board_level", String.format("%d",boardLevel));

        edit.commit();
        Intent intent = new Intent(this,SudokuBoardActivity.class);
        intent.putExtra("level", boardLevel+"");
        intent.putExtra("size",boardSize+"");
        startActivity(intent);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}