package com.jfletcher.braingame;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.StringBufferInputStream;

public class MyDBHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 9;
    public static final String DATABASE_NAME = "highScores.db";
    public static final String TABLE_HIGHSCORESE = "Easy";
    public static final String TABLE_HIGHSCORESM = "Medium";
    public static final String TABLE_HIGHSCORESH = "Hard";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "_username";
    public static final String COLUMN_SCORE = "_score";

    //we need to pass database info along to the superclass
    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) { //, String name, SQLiteDatabase.CursorFactory factory, int version
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_HIGHSCORESE + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_SCORE + " INTEGER" +
                ");";
        db.execSQL(query);
        query = "CREATE TABLE " + TABLE_HIGHSCORESM + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_SCORE + " INTEGER" +
                ");";
        db.execSQL(query);
        query = "CREATE TABLE " + TABLE_HIGHSCORESH + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_SCORE + " INTEGER" +
                ");";
        db.execSQL(query);
        Log.e("DB Status", "Tables created/opened...");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIGHSCORESE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIGHSCORESM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIGHSCORESH);
        onCreate(db);
        Log.e("DB Status", "DB Upgraded...");
    }

    public void resetTable (SQLiteDatabase db, String tbl){
        db.execSQL("DROP TABLE IF EXISTS " + tbl);
        Log.e("DB Status", "Table Dropped...");
        onCreate(db);
    }

    //add new row to the DB
    public void addHighScore(HighScore highScore, String tbl){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, highScore.get_username());
        values.put(COLUMN_SCORE, highScore.get_score());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(tbl, null, values);
        db.close();
        Log.e("DB Status", "Row added to "+tbl+" table...");

    }

    //delete from db table
    public void deleteHighScore (String user, String tbl){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + tbl + " WHERE " +
                COLUMN_NAME + " =\"" + user + "\";");
        Log.e("DB Status", "Row deleted from DB...");
    }

    //print db as string
     public String databaseToString (String tbl){

         //get db information
         SQLiteDatabase db = getWritableDatabase();
         String dbString = "";
         String query = "SELECT * FROM " + tbl + " ORDER BY " + COLUMN_SCORE + " DESC";

         //Cursor point to a location in your results
         Cursor c = db.rawQuery(query, null);
         // Move to the first row in result
         c.moveToFirst();

         while(!c.isAfterLast()){
             //null can happen if we use the empty constructor
             if (c.getString(c.getColumnIndex(COLUMN_SCORE))!=null){
                 //dbString += c.getString(c.getColumnIndex(COLUMN_ID));
                 dbString += c.getString(c.getColumnIndex(COLUMN_NAME));
                 //dbString += c.getInt(c.getColumnIndex(COLUMN_SCORE));
                 dbString += "\n";
             }
             c.moveToNext();
         }
         db.close();
         return dbString;

     }

    //print db as string
    public String databaseToStringScore (String tbl){

        //get db information
        SQLiteDatabase db = getWritableDatabase();
        String dbString = "";
        String query = "SELECT * FROM " + tbl + " ORDER BY " + COLUMN_SCORE + " DESC";

        //Cursor point to a location in your results
        Cursor c = db.rawQuery(query, null);
        // Move to the first row in result
        c.moveToFirst();

        while(!c.isAfterLast()){
            //null can happen if we use the empty constructor
            if (c.getString(c.getColumnIndex(COLUMN_SCORE))!=null){
                //dbString += c.getString(c.getColumnIndex(COLUMN_ID));
                //dbString += c.getString(c.getColumnIndex(COLUMN_NAME));
                dbString += c.getInt(c.getColumnIndex(COLUMN_SCORE));
                dbString += "\n";
            }
            c.moveToNext();
        }
        db.close();
        return dbString;

    }



}
