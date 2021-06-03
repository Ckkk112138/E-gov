package com.example.e_gov;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "E_gov.db";
    public static final String TABLE1_NAME = "FQA_Answer_table";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE1_NAME + "(ID integer primary key autoincrement,ANSWER text )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists " + TABLE1_NAME);
        onCreate(db);
    }

    public void setFQAAnswer(String answer)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ANSWER",answer);
        db.insert(TABLE1_NAME,null,contentValues);


    }

    public String getAnswer(String question)
    {
        String ans = "I don't understand your question.";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select ANSWER from " + TABLE1_NAME +" where ID = ?",new String[]{question} );
        while (res.moveToNext())
        {
             ans = res.getString(0);

        }

        return ans;
    }

    public void cleanFQAAnswer()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("drop table if exists " + TABLE1_NAME);
        db.execSQL("create table " + TABLE1_NAME + "(ID integer primary key autoincrement,ANSWER text )");



    }
}
