package com.jfletcher.braingame;

import android.app.AlertDialog;
import android.app.VoiceInteractor;
import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.*;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import java.util.Random;

import static android.R.attr.prompt;

public class MainActivity extends AppCompatActivity {

    RadioButton bE, bM, bH;
    MyDBHandler myDBHandler;
    CountDownTimer timer;
    EditText userEditText;
    String timerText, username, scoreText, viewText, message, mathText,
            correctAnswerText, wrongAnswer1Text, wrongAnswer2Text, wrongAnswer3Text,
            tbl, function;
    TextView field1, field2, field3, field4, mathTextView, scoreTextView, timerTextView,
        level2time, level2points;
    Button b, b2, nb1, nb2, nb3, nb4, nb5, nb6;
    View l1, l2;
    int timerMax = 15;
    int threeInARow = 0;

    int scoreToL2 = 3;//<<Level 1 goal////////////////////////////////

    int problemTotal, rn, cn, answer, wrongAnswer1, wrongAnswer2, wrongAnswer3, correctAnswer, points, level,
        nmax,nmin,lev2correctAnswer, newTime;
    int setChoice = 1;

    //General Methods/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void playGame(View view) {

        level = 1;

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
        setProblem();//add difficult to setProblem
        runTimer(timerMax, timerTextView);
    }

    public void diffSelect (View view){

        RadioButton rb = (RadioButton) findViewById(view.getId());
        tbl = rb.getTag().toString();
        Log.i("tbl chosen", tbl);

        printDatabase();
        b.setEnabled(true);
    }
    public void resetHSTable(View view) {

        SQLiteDatabase db = myDBHandler.getWritableDatabase();
        myDBHandler.resetTable(db, tbl);
        printDatabase();
    }
    public void runTimer(int time, final TextView tv) {

        //this needs work to format for multiple levels

        timer = new CountDownTimer(time * 1000 + 100, 1000) {

            @Override
            public void onTick(long l) {

                if (l < 10000) {
                    timerText = "00:0" + Long.toString(l / 1000);
                } else {
                    timerText = "00:" + Long.toString(l / 1000);
                }
                tv.setText(timerText);
            }

            @Override
            public void onFinish() {

                field1.setEnabled(false);
                field2.setEnabled(false);
                field3.setEnabled(false);
                field4.setEnabled(false);
                userEditText.setEnabled(true);

                tv.setText("00:00");
                //change button to reset
                b = (Button) findViewById(R.id.playButton);
                message = "Play Again";
                b.setText(message);

                addToDB();

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Great Game!!!");
                alertDialog.setMessage("You scored "+ points +" on difficulty level " + tbl + ".\n" +
                        "\n" +
                        "Try again on a different difficulty. Have fun!");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

////
////                Intent i = new Intent(getApplicationContext(), Pop.class);
////                i.putExtra("key", "Value");
////                i.putExtra("points", points);
////                i.putExtra("total", problemTotal);
//
//
//                startActivity(i);
                resetL1Game();
                b.setVisibility(View.VISIBLE);
                //b2.setVisibility(View.VISIBLE);

                resetLayouts();

            }

        };
        timer.start();

    }

    public int randGen(int max, int min) {
        Random r = new Random();
        int nrn = r.nextInt(max - min + 1) + min;
        return nrn;
    }

    public void printDatabase() {
        String c = myDBHandler.databaseToString(tbl);
        String s = myDBHandler.databaseToStringScore(tbl);

        TextView hsListScore = (TextView) findViewById(R.id.hsListScore);
        TextView hsList = (TextView) findViewById(R.id.hsList);
        hsList.setText(c);
        hsListScore.setText(s);

        Log.i("DB Results", c);
        Log.i("DB Results", s);
    }

    //add highscore to database
    public void addToDB() {

        username = userEditText.getText().toString();
        if (username.equals("")){
            username = "Player Name";
        }

        HighScore highscore = new HighScore(username, points);
        myDBHandler.addHighScore(highscore, tbl);
        printDatabase();
    }

    // delete highscore
    public void deleteScore() {
        String inputName = username;
        myDBHandler.deleteHighScore(inputName, tbl);
        printDatabase();
    }


    public void setScore() {

        if (checkAnswer()) {
            //add point and to total
            points++;
            problemTotal++;
            threeInARow++;
            //add +1 animation/////////////////////////////

            TextView p1 = (TextView) findViewById(R.id.plusOneScore);
            p1.setAlpha(1);
            p1.animate().alpha(0.0f).setDuration(1000);
            Log.i("Answer", "True");
        } else {
            //add to total
            points--;
            problemTotal++;
            threeInARow = 0;
            //add -1 animation/////////////////////////////
            TextView m1 = (TextView) findViewById(R.id.minusOneScore);
            m1.setAlpha(1);
            m1.animate().alpha(0.0f).setDuration(1000);
            Log.i("Answer", "True");
            Log.i("Answer", "False");
        }
        scoreText = Integer.toString(points) + "/" + Integer.toString(problemTotal);
        scoreTextView.setText(scoreText);
    }

    //Level 1 Specific Methods/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void waitForLevel2(){
        new CountDownTimer(3000, 3000){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Level 2 Rules...");
                alertDialog.setMessage(getResources().getString(R.string.l2alert_text));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                runTimer(newTime, level2time);
                                level2SetProblem();
                                translateLevel2Blocks();
                                Log.i("Wait", "over");
                            }
                        });

                alertDialog.show();


            }
        }.start();
        Log.i("Wait", "over again");

    }
    public void resetL1Game() {

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

    public void setProblem() {

        // Setting Random numbers and getting answer
        int randNum1 = randGen(6, 1);
        //second number set
        int randNum2 = randGen(6, 1);

        if (tbl.equals("Easy")){
            correctAnswer = randNum1 + randNum2;
            function = "+";
        } else if(tbl.equals("Medium")){
            if (randGen(2, 1)==1) {
                function = "-";
                correctAnswer = randNum1 - randNum2;
            } else {
                function = "+";
                correctAnswer = randNum1 + randNum2;
            }
        } else {
            int i = randGen(3, 1);
            if (i==1) {
                function = "*";
                correctAnswer = randNum1 * randNum2;
            } else if (i==2) {
                function = "+";
                correctAnswer = randNum1 + randNum2;
            } else {
                function = "-";
                correctAnswer = randNum1 - randNum2;
            }
        }

        correctAnswerText = Integer.toString(correctAnswer);

        //set problem text to equal answer
        mathText = Integer.toString(randNum1) + " " + function  +" " + Integer.toString(randNum2);
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
        if (tbl.equals("Easy")){
            nmax = 12;
            nmin = 2;
        } else if (tbl.equals("Medium")){
            nmax = 12;
            nmin = -5;
        } else if (tbl.equals("Hard")){
            nmax = 36;
            nmin = -5;
        }
        wrongAnswer1 = randGen(nmax, nmin);
        wrongAnswer2 = randGen(nmax, nmin);
        wrongAnswer3 = randGen(nmax, nmin);

        while (wrongAnswer1 == correctAnswer) {
            wrongAnswer1 = randGen(12, 2);
        }
        while (wrongAnswer2 == correctAnswer) {
            wrongAnswer2 = randGen(12, 2);
        }
        while (wrongAnswer3 == correctAnswer) {
            wrongAnswer3 = randGen(12, 2);
        }

        wrongAnswer1Text = Integer.toString(wrongAnswer1);
        wrongAnswer2Text = Integer.toString(wrongAnswer2);
        wrongAnswer3Text = Integer.toString(wrongAnswer3);


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
    public boolean checkAnswer() {

        return answer == correctAnswer;
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
        if (threeInARow == 3) {
            timer.cancel();
            threeInARow = 0;
            String timeString = timerTextView.getText().toString();
            String[] separated = timeString.split(":");
            int newTime = Integer.parseInt(separated[1]);
            Log.i("new time", Integer.toString(newTime));
            runTimer(newTime + 3, timerTextView);

            //add plus 3 animation
            TextView p3 = (TextView) findViewById(R.id.plusThreeTime);
            p3.setAlpha(1);
            p3.animate().alpha(0.0f).setDuration(1000);
        }

        //if score equals L1 goal then move to level 2, if not set problem and check for 3 in a row
        if (points >= scoreToL2) {

            //translate L1 off screen
            l1.animate().translationX(1800f).setDuration(3000);

            //stop timer
            timer.cancel();
            Log.i("level 1", "complete");

            //animate L2 on to screen
            level2time = (TextView) findViewById(R.id.level2Time);
            level2time.setText(Integer.toString(newTime));
            level2points = (TextView) findViewById(R.id.level2points);
            level2points.setText(Integer.toString(points));
            l2.animate().translationX(0).setDuration(3000);
            Log.i("level 2", "start");

            //reset three in a row variable and grab time remaining
            threeInARow = 0;
            String timeString = timerTextView.getText().toString();
            String[] separated = timeString.split(":");
            newTime = Integer.parseInt(separated[1]);
            Log.i("new time", Integer.toString(newTime));

            //wait for L2 translate before starting game
            waitForLevel2();
            Log.i("After wait code", "yes");

        } else {

            setProblem();

            //if players gets 3 in a row, stop timer and start a new one with old time +3
//            if (threeInARow == 3) {
//                timer.cancel();
//                threeInARow = 0;
//                String timeString = timerTextView.getText().toString();
//                String[] separated = timeString.split(":");
//                int newTime = Integer.parseInt(separated[1]);
//                Log.i("new time", Integer.toString(newTime));
//                runTimer(newTime + 3, timerTextView);
//
//                //add plus 3 animation
//                TextView p3 = (TextView) findViewById(R.id.plusThreeTime);
//                p3.setAlpha(1);
//                p3.animate().alpha(0.0f).setDuration(1000);
//            }
        }
    }

    //Level 2 Specific Methods/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void resetL2Blocks (){

        //reset blocks to starting position
        nb1.animate().translationY(0).setDuration(1000);
        nb2.animate().translationY(0).setDuration(1000);
        nb3.animate().translationY(0).setDuration(1000);
        nb4.animate().translationY(0).setDuration(1000);
        nb5.animate().translationY(0).setDuration(1000);
        nb6.animate().translationY(0).setDuration(1000);

        new CountDownTimer(1000, 100) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                translateLevel2Blocks();
            }
        }.start();

    }

    public void translateLevel2Blocks(){

        int tmin = 7000; int tmax = 8000;
        nb1.animate().translationY(2000).setDuration(randGen(tmax, tmin));
        nb2.animate().translationY(2000).setDuration(randGen(tmax, tmin));
        nb3.animate().translationY(2000).setDuration(randGen(tmax, tmin));
        nb4.animate().translationY(2000).setDuration(randGen(tmax, tmin));
        nb5.animate().translationY(2000).setDuration(randGen(tmax, tmin));
        nb6.animate().translationY(2000).setDuration(randGen(tmax, tmin));
    }

    public void level2CheckAnswer (){

        ///Check answer on block 2 selection
        TextView cb1 = (TextView) findViewById(R.id.levelTwoChoice1);TextView cb2 = (TextView) findViewById(R.id.levelTwoChoice2);
        int playerGuess = Integer.parseInt(cb1.getText().toString())+Integer.parseInt(cb2.getText().toString());
        if (lev2correctAnswer==playerGuess){
            resetL2Blocks();
            level2SetProblem();
            points++;
            problemTotal++;
            threeInARow++;
        } else {
            points--;
            problemTotal++;
            threeInARow=0;
        }

        level2points = (TextView) findViewById(R.id.level2points);
        level2points.setText(Integer.toString(points));

    }

    public void level2SetProblem(){

        TextView l2AnswerTextView = (TextView) findViewById(R.id.levelTwoAnswer1);
        lev2correctAnswer = randGen(11, 3);
        l2AnswerTextView.setText(Integer.toString(lev2correctAnswer));

    }

    public void level2setChoice(int n){

        String nSet = Integer.toString(n);

        if (setChoice == 1){
            //set choice 1 to n
            TextView cb1 = (TextView) findViewById(R.id.levelTwoChoice1);
            cb1.setText(nSet);
            cb1.setScaleX(3);cb1.setScaleY(3);
            cb1.animate().scaleX(1).scaleY(1).setDuration(1000);
            setChoice = 2;
        } else {
            //set choice 2 to n
            TextView cb1 = (TextView) findViewById(R.id.levelTwoChoice2);
            cb1.setText(nSet);
            cb1.setScaleX(3);cb1.setScaleY(3);
            cb1.animate().scaleX(1).scaleY(1).setDuration(1000);
            setChoice = 1;
            level2CheckAnswer();
        }

    }

    public void level2Translate(final View vReset, long l, final long r){

        new CountDownTimer(l, 100){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                vReset.animate().translationY(2000).setDuration(r);
            }
        }.start();
    }

    public void level2Click(View view){

        Button vb = (Button) findViewById(view.getId());
        String selectedButtonText = vb.getText().toString();
        int selectedNumber = Integer.parseInt(selectedButtonText);

        view.animate().cancel();
        Log.i("L2 Button Clicked", view.getTag().toString());
        float translation = view.getTranslationY();
        Log.i("Translation", Float.toString(translation));
        long randReset = randGen(8000, 6000);

        level2setChoice(selectedNumber);

        //if players gets 3 in a row, stop timer and start a new one with old time +3
        if (threeInARow == 3) {
            timer.cancel();
            threeInARow = 0;
            String timeString = level2time.getText().toString();
            String[] separated = timeString.split(":");
            int newTime = Integer.parseInt(separated[1]);
            Log.i("new time", Integer.toString(newTime));
            runTimer(newTime + 3, level2time);

            //add plus 3 animation
            TextView p3 = (TextView) findViewById(R.id.l2plusThreeTime);
            p3.setAlpha(1);
            p3.animate().alpha(0.0f).setDuration(1000);
        }

//        view.animate().translationY(0).setDuration(2000);
//        level2Translate(view, 2000, randReset);

    }

    public void resetl2game(){

//        resetL2Blocks();

    }

    //Level 3 Specific Methods/////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    public void resetLayouts(){

//        resetL1Game();
//        resetl2game();
        l1.setAlpha(1);
        l2.setAlpha(1);
        l1.animate().translationX(0).setDuration(0);
        l2.animate().translationX(-8000).setDuration(0);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        l1 = (View) findViewById(R.id.levelOneLayout);
        l2 = (View) findViewById(R.id.levelTwoLayout);

        resetLayouts();

        nb1 = (Button) findViewById(R.id.numBlock1);
        nb2 = (Button) findViewById(R.id.numBlock2);
        nb3 = (Button) findViewById(R.id.numBlock3);
        nb4 = (Button) findViewById(R.id.numBlock4);
        nb5 = (Button) findViewById(R.id.numBlock5);
        nb6 = (Button) findViewById(R.id.numBlock6);

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Rules of this game...");
        alertDialog.setMessage(getResources().getString(R.string.alert_text));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();

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

        bE = (RadioButton) findViewById(R.id.eButton);
        bM = (RadioButton) findViewById(R.id.mButton);
        bH = (RadioButton) findViewById(R.id.hButton);

        bE.setChecked(false);
        bM.setChecked(false);
        bH.setChecked(false);

        b = (Button) findViewById(R.id.playButton);
        b.setEnabled(false);

        mathTextView = (TextView) findViewById(R.id.mathProblem);

        //print initial db results
        myDBHandler = new MyDBHandler(this, null, null, 1);
//        printDatabase();
    }

}
