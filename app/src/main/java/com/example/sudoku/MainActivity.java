package com.example.sudoku;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TableLayout tl;
    private static class Cell {
        int value;
        boolean fixed;
        Button btn;

        public Cell(int initValue, Context context, boolean isFixed) {
            value = initValue;
            fixed = isFixed;
            btn = new Button(context);
            if (fixed) {
                btn.setText(String.valueOf(value));
                btn.setTextColor(Color.BLACK);
                btn.setEnabled(false);
            } else {
                btn.setTextColor(Color.BLUE);
                btn.setOnClickListener(v -> {
                    if (fixed) {
                        return;
                    }
                    value++;
                    if (value > 9) {
                        value = 1;
                    }
                    btn.setText(String.valueOf(value));
                    if (check()) {
                        txtResult.setText("");
                    } else {
                        txtResult.setText("There is a repeated digit!!!");
                        txtResult.setTextColor(Color.BLACK);
                    }
                });}}


    }

    private static Cell[][] table;
    private static TextView txtResult;
    private Button btnReset;

    private String readInputFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append(" ");
        }
        reader.close();
        return stringBuilder.toString();
    }

    private void LoadGame(String filename){
        try {
            InputStream is = getAssets().open(filename);
            String input = readInputFromInputStream(is);
            String[] split = input.split("[ ]+");
            tl.removeAllViews();
            for (int i = 0; i < 9; i++) {
                TableRow tr = new TableRow(this);
                for (int j = 0; j < 9; j++) {
                    String s = split[i * 9 + j];
                    char c = s.charAt(0);
                    table[i][j] = new Cell(c == '-' ? 0 : Character.getNumericValue(c), this, c != '-');
                    tr.addView(table[i][j].btn);
                }
                tl.addView(tr);
            }
        }catch(IOException e){
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tl=findViewById(R.id.tableLayout);

        List<String> inputs = new ArrayList<>();
        try {
            InputStream inputStream1 = getAssets().open("Easy1.txt");
            String input1 = readInputFromInputStream(inputStream1);
            inputs.add(input1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            InputStream inputStream2 = getAssets().open("Medium1.txt");
            String input2 = readInputFromInputStream(inputStream2);
            inputs.add(input2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            InputStream inputStream3 = getAssets().open("Hard1.txt");
            String input3 = readInputFromInputStream(inputStream3);
            inputs.add(input3);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Random random = new Random();
        String input = inputs.get(random.nextInt(inputs.size()));

        String[] split = input.split("[ ]+");
        table = new Cell[9][9];
        TableLayout tl = findViewById(R.id.tableLayout);
        if(tl !=null){
            for (int i = 0; i < 9; i++) {
                TableRow tr = new TableRow(this);
                for (int j = 0; j < 9; j++) {
                    String s = split[i * 9 + j];
                    char c = s.charAt(0);
                    table[i][j] = new Cell(c == '-' ? 0 : Character.getNumericValue(c), this,c!='-');
                    tr.addView(table[i][j].btn);
                }
                tl.addView(tr);
            }
        }

        Button btnCheck = findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(v -> {
            int count = 0;
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (table[i][j].value != 0 && check()) {
                        count++;
                    }
                }
            }
            if (count == 9* 9) {
                txtResult.setText("You Have Won!!!");
                txtResult.setTextColor(Color.GREEN);
            } else {
                txtResult.setText("Unfinished");
                txtResult.setTextColor(Color.RED);
            }
        });
        btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(v -> resetGame());


        txtResult = findViewById(R.id.tvResult);

        Button btnEasy = findViewById(R.id.btnEasy);
        btnEasy.setOnClickListener(v -> {
            int rand=new Random().nextInt(3);
            String filename="Easy"+(rand+1)+".txt";
            LoadGame(filename);
        });

        Button btnMed = findViewById(R.id.btnMed);
        btnMed.setOnClickListener(v -> {
            int rand=new Random().nextInt(3);
            String filename="Medium"+(rand+1)+".txt";
            LoadGame(filename);
        });

        Button btnHard = findViewById(R.id.btnHard);
        btnHard.setOnClickListener(v -> {
            int rand=new Random().nextInt(3);
            String filename="Hard"+(rand+1)+".txt";
            LoadGame(filename);
        });
    }
    private void resetGame() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!table[i][j].fixed) {
                    table[i][j].value = 0;
                    table[i][j].btn.setText("");
                }
            }
        }
        txtResult.setText("");
    }


    private static boolean check(int i1, int j1, int i2, int j2) {
        boolean[] seen = new boolean[9 + 1];
        for (int i = 1; i <= 9; i++) {
            seen[i] = false;
        }
        for (int i = i1; i < i2; i++) {
            for (int j = j1; j < j2; j++) {
                if (i >= 0 && i < 9 && j >= 0 && j < 9) {
                    int value = table[i][j].value;
                    if (value != 0) {
                        if (seen[value]) {
                            return false;
                        }
                        seen[value] = true;
                    }
                }
            }
        }
        return true;
    }

    private static boolean check() {
        for (int i = 0; i < 9; i++) {
            if (!check(i, 0, i + 1, 9)) {
                return false;
            }
        }
        for (int j = 0; j < 9; j++) {
            if (!check(0, j, 9, j + 1)) {
                return false;
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (!check(3 * i, 3 * j, 3 * i + 3, 3 * j + 3)) {
                    return false;
                }
            }
        }
        return true;
    }
}