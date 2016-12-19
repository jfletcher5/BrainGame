package com.jfletcher.braingame;

/**
 * Created by jondf on 12/15/2016.
 */

public class HighScore {

    private int _id;
    private String _username;
    private int _score;

    public HighScore(){

    }

    public HighScore(String user, int highscore){
        this._score = highscore;
        this._username = user;
    }

    public int get_id() {
        return _id;
    }
    public void set_id(int _id) {
        this._id = _id;
    }

    public int get_score() {
        return _score;
    }
    public void set_score(int _score) {
        this._score = _score;
    }

    public String get_username() {
        return _username;
    }
    public void set_username(String _username) {
        this._username = _username;
    }



}
