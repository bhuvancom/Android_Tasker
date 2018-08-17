package com.newware.tasker;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout relativeLayoutMain;
    private RecyclerView recyclerViewMain;
    private List<TaskDetails> dataItems;
    private AdapterOur adapterOur;
    Cursor cursor;
    TextView tvNoTask;
    FloatingActionButton fab;

    private DatabaseOperationsMethods db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        android.support.v7.app.ActionBar actionBar = getSupportActionBar(); // use v7 and getSupport method to work fine

        actionBar.setLogo(R.drawable.app_logo);//logo file

        actionBar.setDisplayUseLogoEnabled(true);//to display logo

        actionBar.setDisplayShowHomeEnabled(true);//to add logo as home button haha noo bro

        relativeLayoutMain = findViewById(R.id.relative_main);

        fab = findViewById(R.id.fab);


        recyclerViewMain = findViewById(R.id.recyclerview_list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewMain.setLayoutManager(layoutManager);
        recyclerViewMain.setItemAnimator(new DefaultItemAnimator());


        db = new DatabaseOperationsMethods(MainActivity.this);


        dataItems = new ArrayList<>();

        getAllTasks();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

                final View viewForAddTask = getLayoutInflater().inflate(R.layout.add_task_custum, null);

                Button btn = viewForAddTask.findViewById(R.id.btn_task_save);

                alertDialog.setTitle("Add Task").setCancelable(true);
                alertDialog.setView(viewForAddTask).create();


                alertDialog.setCancelable(false)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.cancel();
                            }
                        });



                final AlertDialog dialog = alertDialog.create();

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        EditText etTaskInfo = viewForAddTask.findViewById(R.id.et_add_task);
                        String taskinfo = etTaskInfo.getText().toString();
                        if (taskinfo.isEmpty())
                        {

                            etTaskInfo.setError("Fill it !");
                            etTaskInfo.requestFocus();

                            return;
                        }
                        else if (taskinfo.length() < 3)
                        {
                            etTaskInfo.setError("Too short");
                            etTaskInfo.requestFocus();
                            return;
                        }
                        else
                        {
                            String info = etTaskInfo.getText().toString().trim() + "\t";
                            int isDone = 0;
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                            String dateToday = sdf.format(new Date());
                            long currentTime = System.currentTimeMillis() / 1000;
                            TaskDetails dataToUploadTodb =
                                    new TaskDetails(info,
                                            currentTime, isDone, dateToday,dateToday);

                            db.open();
                            String returnedString = db.insertTask(dataToUploadTodb);
                            dialog.cancel();
                            if (returnedString != null)
                            {
                                showSnackbar(returnedString);
                                tvNoTask.setVisibility(View.GONE);
                                db.close();
                                dataItems.add(0,dataToUploadTodb);
                                adapterOur.notifyDataSetChanged();
                            }
                        }
                    }
                });

                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });
//
//        MyAsync myAsync = new MyAsync();
//        myAsync.execute();
    }

    private void getAllTasks() {
        db.open();
        cursor = db.fetchTask();
        int count = cursor.getCount();
        Log.i("db", "got row " + count);
        dataItems.clear();
        adapterOur = new AdapterOur(dataItems, MainActivity.this);
        recyclerViewMain.setAdapter(adapterOur);

        while (cursor.moveToNext())
        {
            int current = cursor.getPosition();
            Log.i("db", "iteration " + current);
            long currentTime = Long.parseLong(cursor.getString(1));
            String taskInfo = cursor.getString(2);
            int isTaskDone = Integer.parseInt(cursor.getString(3));
            String dateOfCreation = cursor.getString(4);

            dataItems.add(new TaskDetails(taskInfo, currentTime, isTaskDone, dateOfCreation,"no"));
            //adapterOur.notifyDataSetChanged();
        }
        adapterOur.notifyDataSetChanged();
        tvNoTask = findViewById(R.id.tv_noTask);
        if (cursor.getCount() > 0)
            tvNoTask.setVisibility(View.GONE);

        else
            tvNoTask.setVisibility(View.VISIBLE);



        db.close();
    }


    private void showSnackbar(String msgToShow) {
        Snackbar.make(relativeLayoutMain, "" + msgToShow, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getAllTasks();
    }
}
