package com.newware.tasker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Bhuvaneshvar Nath Srivastava on 10-08-2018 at 12:08 AM.
 * Copyright (c) 2018
 **/
public class DatabaseOpenHelper extends SQLiteOpenHelper {
    public DatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tasks (ID integer primary key autoincrement ,CREATE_TIME  text unique , INFO text , IS_DONE integer, DATE_OF_CREATION text , LAST_UPDATE text );");
        db.execSQL("CREATE TABLE reminders (CREATE_TIME text , TIME_TO_REMIND text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
    {
        db.execSQL("DROP TABLE IF EXISTS tasks");
        onCreate(db);
    }
}
