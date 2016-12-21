package com.jfletcher.braingame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.*;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    AlertDialog dialog;
    MyDBHandler myDBHandler;
    CountDownTimer timer;
    EditText userEditText;
    String timerText, username, scoreText, viewText, message, mathText,
            correctAnswerText, wrongAnswer1Text, wrongAnswer2Text, wrongAnswer3Text;
    TextView field1, field2, field3, field4, mathTextView, scoreTextView, timerTextView;
    Button b, b2;
    int timerMax = 15;
    int problemTotal, rn, cn, answer, wrongAnswer1, wrongAnswer2, wrongAnswer3, correctAnswer, points;
    int threeInARow = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public void resetGame(View view) {

        SQLiteDatabase db = myDBHandler.getWritableDatabase();
        myDBHandler.resetTable(db);
        printDatabase();
    }

    public int randGen(int max, int min) {
        Random r = new Random();
        int nrn = r.nextInt(max - min + 1) + min;
        return nrn;
    }

    public boolean checkUnique(int wrongA) {

        return wrongA == correctAnswer;
    }

    public void setProblem() {

        // Setting Random numbers and getting answer
        int randNum1 = randGen(6, 1);
        //second number set
        int randNum2 = randGen(6, 1);
        correctAnswer = randNum1 + randNum2;

        if (correctAnswer < 10) {
            correctAnswerText = "0" + Integer.toString(correctAnswer);
        } else {
            correctAnswerText = Integer.toString(correctAnswer);
        }

        //set problem to equal answer
        mathText = Integer.toString(randNum1) + " + " + Integer.toString(randNum2);
        mathTextView.setText(mathText);

        //set one box to correct answer
        cn = randGen(4, 1);
        if (cn == 1) {
            field1.setText(correctAnswerText);
        } else if (cn == 2) {
            field2.setText(correctAnswerText);
        } else if (cn == 3) {
            field3.setText(correctAnswerText);
        } else if (cn == 4) {
            field4.setText(correctAnswerText);
        }


        //set other boxes to random numbers, re-randomize if equal to correctAnswer
        wrongAnswer1 = randGen(12, 2);
        wrongAnswer2 = randGen(12, 2);
        wrongAnswer3 = randGen(12, 2);

        while (wrongAnswer1 == correctAnswer) {
            wrongAnswer1 = randGen(12, 2);
        }
        while (wrongAnswer2 == correctAnswer) {
            wrongAnswer2 = randGen(12, 2);
        }
        while (wrongAnswer3 == correctAnswer) {
            wrongAnswer3 = randGen(12, 2);
        }

        //format single digits
        if (wrongAnswer1 < 10) {
            wrongAnswer1Text = "0" + Integer.toString(wrongAnswer1);
        } else {
            wrongAnswer1Text = Integer.toString(wrongAnswer1);
        }
        if (wrongAnswer2 < 10) {
            wrongAnswer2Text = "0" + Integer.toString(wrongAnswer2);
        } else {
            wrongAnswer2Text = Integer.toString(wrongAnswer2);
        }
        if (wrongAnswer3 < 10) {
            wrongAnswer3Text = "0" + Integer.toString(wrongAnswer3);
        } else {
            wrongAnswer3Text = Integer.toString(wrongAnswer3);
        }

        if (cn == 1) {
            field2.setText(wrongAnswer1Text);
            field3.setText(wrongAnswer2Text);
            field4.setText(wrongAnswer3Text);
        } else if (cn == 2) {
            field1.setText(wrongAnswer1Text);
            field3.setText(wrongAnswer2Text);
            field4.setText(wrongAnswer3Text);
        } else if (cn == 3) {
            field1.setText(wrongAnswer1Text);
            field2.setText(wrongAnswer2Text);
            field4.setText(wrongAnswer3Text);
        } else {
            field1.setText(wrongAnswer1Text);
            field2.setText(wrongAnswer2Text);
            field3.setText(wrongAnswer3Text);
        }

        Log.i("math problem", mathText);
    }

    public void setScore() {

        if (checkAnswer()) {
            //add point and to total
            points++;
            problemTotal++;
            threeInARow++;
            Log.i("Answer", "True");
        } else {
            //add to total
            points--;
            problemTotal++;
            threeInARow = 0;
            Log.i("Answer", "False");
        }
        scoreText = Integer.toString(points) + "/" + Integer.toString(problemTotal);
        scoreTextView.setText(scoreText);
    }

    public boolean checkAnswer() {

        return answer == correctAnswer;
    }

    public void runTimer(int time) {
        timer = new CountDownTimer(time * 1000 + 30, 1000) {

            @Override
            public void onTick(long l) {

                timerTextView = (TextView) findViewById(R.id.timer);
                if (l < 10000) {
                    timerText = "00:0" + Long.toString(l / 1000);
                } else {
                    timerText = "00:" + Long.toString(l / 1000);
                }
                timerTextView.setText(timerText);
            }

            @Override
            public void onFinish() {

                field1.setEnabled(false);
                field2.setEnabled(false);
                field3.setEnabled(false);
                field4.setEnabled(false);
                userEditText.setEnabled(true);

                timerTextView.setText("00:00");
                //change button to reset
                b = (Button) findViewById(R.id.playButton);
                message = "Play Again";
                b.setText(message);

                addToDB();

////
////                Intent i = new Intent(getApplicationContext(), Pop.class);
////                i.putExtra("key", "Value");
////                i.putExtra("points", points);
////                i.putExtra("total", problemTotal);
//
//
//                startActivity(i);
                resetGame();
                b.setVisibility(View.VISIBLE);
                //b2.setVisibility(View.VISIBLE);
            }

        };
        timer.start();

    }

    public void selected(View view) {

        // Finding TextField name and Text of box clicked

        TextView thisView = (TextView) findViewById(view.getId());
        viewText = thisView.getText().toString();
        answer = Integer.parseInt(viewText);

        String boxSelected = view.getResources().getResourceEntryName(view.getId());
        Log.i("Box selected", boxSelected);
        Log.i("Text", viewText);

        setScore();
        setProblem();

        if (threeInARow == 3) {
            timer.cancel();
            threeInARow = 0;
            String timeString = timerTextView.getText().toString();
            String[] separated = timeString.split(":");
            int newTime = Integer.parseInt(separated[1]);
            Log.i("new time", timerTextView.getText().toString());
            Log.i("Seconds", Integer.toString(newTime));
            runTimer(newTime + 3);
        }
    }

    public void playGame(View view) {

        b = (Button) findViewById(R.id.playButton);
        //b2 = (Button) findViewById(R.id.resetScores);
        b.setVisibility(View.INVISIBLE);
        //b2.setVisibility(View.INVISIBLE);

        userEditText.setEnabled(false);

        field1.setEnabled(true);
        field2.setEnabled(true);
        field3.setEnabled(true);
        field4.setEnabled(true);


        Log.i("play", "Yes");
        setProblem();
        runTimer(timerMax);
    }

    //add to database
    public void addToDB() {

        username = userEditText.getText().toString();

        HighScore highscore = new HighScore(username, points);
        myDBHandler.addHighScore(highscore);
        printDatabase();
    }

    // delete item
    public void deleteScore() {
        String inputName = username;
        myDBHandler.deleteHighScore(inputName);
        printDatabase();
    }

    public void printDatabase() {
        String c = myDBHandler.databaseToString();
        String s = myDBHandler.databaseToStringScore();

        TextView hsListScore = (TextView) findViewById(R.id.hsListScore);
        TextView hsList = (TextView) findViewById(R.id.hsList);
        hsList.setText(c);
        hsListScore.setText(s);

        Log.i("DB Results", c);
        Log.i("DB Results", s);
    }

    public void resetGame() {

        field1.setText("00");
        field2.setText("00");
        field3.setText("00");
        field4.setText("00");
        timerTextView.setText("00:" + timerMax);
        mathTextView.setText(" ");
        scoreTextView.setText("0/0");
        points = 0;
        problemTotal = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Rules");
        alertDialog.setMessage("Simple timed math game. Once the game starts, for every correct answer the player will get one point. " +
                "For ever incorrect answer the player loses a point. If the player selects three correct answers in a row, they will get" +
                " three extra seconds added to their time. Good luck!!");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Pos",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Neg",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Neut",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

//        Log.i("Dialog", dText);

        userEditText = (EditText) findViewById(R.id.userName);

        //set timer
        timerTextView = (TextView) findViewById(R.id.timer);
        message = "00:" + Integer.toString(timerMax);
        timerTextView.setText(message);
        //set score
        scoreTextView = (TextView) findViewById(R.id.score);
        points = 0;
        problemTotal = 0;
        scoreText = Integer.toString(points) + "/" + Integer.toString(problemTotal);
        scoreTextView.setText(scoreText);

        field1 = (TextView) findViewById(R.id.field1);
        field2 = (TextView) findViewById(R.id.field2);
        field3 = (TextView) findViewById(R.id.field3);
        field4 = (TextView) findViewById(R.id.field4);

        field1.setEnabled(false);
        field2.setEnabled(false);
        field3.setEnabled(false);
        field4.setEnabled(false);

        b = (Button) findViewById(R.id.playButton);
        b.setEnabled(false);

        mathTextView = (TextView) findViewById(R.id.mathProblem);

        //print initial db results
        myDBHandler = new MyDBHandler(this, null, null, 1);
        printDatabase();
    }

}
