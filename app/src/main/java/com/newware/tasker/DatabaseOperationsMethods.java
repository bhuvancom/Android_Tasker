package com.newware.tasker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by Bhuvaneshvar Nath Srivastava on 10-08-2018 at 01:15 AM.
 * Copyright (c) 2018
 **/
public class DatabaseOperationsMethods {
    private static final String DATABASE_NAME = "tasker.db";
    protected static final String TABLE_NAME = "tasks";
    protected static final String TABLE_REMINDER = "reminders";
    private static final int DB_VERSIOn = 1;
    private final String qry = "CREATE_TIME =?" ;

    public SQLiteDatabase db;

    private Context mcontext;
    private DatabaseOpenHelper mDatabaseOpenHelper;


    // this will insure to create db for 1st time
    public DatabaseOperationsMethods(Context mcontext) {
        this.mcontext = mcontext;
        mDatabaseOpenHelper = new DatabaseOpenHelper(mcontext, DATABASE_NAME, null, DB_VERSIOn);
    }

    //db opener
    public DatabaseOperationsMethods open() throws SQLException {
        db = mDatabaseOpenHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public String insertTask(TaskDetails taskDetails)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("CREATE_TIME", String.valueOf(taskDetails.getCurrentTime()));
        contentValues.put("INFO", taskDetails.getTaskInfo());
        contentValues.put("IS_DONE", taskDetails.getIsTaskDone());
        contentValues.put("DATE_OF_CREATION", taskDetails.getDateOfCreation());
        contentValues.put("LAST_UPDATE",taskDetails.getLastEdited());

        long a = db.insert(TABLE_NAME, null, contentValues);
        if (a > 0)
        {
            return "Added";
        }
        else
            return "Something Went Wrong";
    }

    public int updateTask(ContentValues values, String timeToselectTask)
    {
        return db.update(TABLE_NAME, values, qry, new String[]{timeToselectTask});
    }

    public int deleteTask(String primary_key)
    {
        Cursor cur = db.query(TABLE_REMINDER,null,qry, new String[]{primary_key},null,null,null);
        if (cur.getCount() > 0)
        {
            db.delete(TABLE_REMINDER,qry, new String[]{primary_key});
        }
        cur.close();
        return db.delete(TABLE_NAME,qry, new String[]{primary_key});
    }

    public Cursor fetchTask()
    {
        return db.query(TABLE_NAME, null, null, null, null, null, "ID DESC");
    }

    public Cursor fetchByTime(String primary_key)
    {
       return db.query(TABLE_NAME,
               new String[]{"INFO","IS_DONE","DATE_OF_CREATION","LAST_UPDATE"},
               qry,
               new String[]{primary_key},null,null,null);
    }

    public int setTaskDone(ContentValues contentValues,String primary_key)
    {
        Cursor cur = db.query(TABLE_REMINDER,null,qry, new String[]{primary_key},null,null,null);
        if (cur.getCount() > 0)
        {
            db.delete(TABLE_REMINDER,qry, new String[]{primary_key});
        }
        cur.close();
        return db.update(TABLE_NAME,contentValues,qry, new String[]{primary_key});
    }

    public Cursor findReminder(String primary_key)
    {
        return db.query(TABLE_REMINDER,null,qry, new String[]{primary_key},null,null,null);
    }

    public String setReminder(String primary,String remindTime)
    {
        Cursor cur = db.query(TABLE_REMINDER,null,qry, new String[]{primary},null,null,null);
        if (cur.getCount() > 0)
        {
            ContentValues values = new ContentValues();
            values.put("TIME_TO_REMIND", remindTime);
            db.update(TABLE_REMINDER,values,qry, new String[]{primary});
            return "Updated Reminder";
        }
        else
            {
            ContentValues values = new ContentValues();
            values.put("CREATE_TIME", primary);
            values.put("TIME_TO_REMIND", remindTime);
            long a = db.insert(TABLE_REMINDER, null, values);
            if (a > 0)
                return "Reminder Added";

            else
                return "something went wrong";
        }
    }

}
