package com.newware.tasker;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.newware.tasker.AdapterOur.PRIMARY_KEY;

public class TaskDetailsAndManipulation extends AppCompatActivity {
    protected static final String REMINDER_TABLE = "reminders";

    private TextView tv_task_info, tv_task_date, tv_task_reminded;
    private CheckBox chbx_isDone;
    private String task_date, task_details, task_isDone, task_last_edited, reminderTime;
    private String task_primary_key;
    private DatabaseOperationsMethods db;
    private TextView tv_updated;
    private RelativeLayout relativeLayout;
    private NestedScrollView nestedScrollView;
    private NestedScrollView scrollView;
    private boolean removeEdit = false;
    private boolean removeDel = false;

    private MenuItem editMenu;
    private String TAG = "getTime";
    private Calendar date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details_and_manipulation);

        Intent getData = getIntent();
        task_primary_key = getData.getStringExtra(PRIMARY_KEY);

        relativeLayout = findViewById(R.id.relative_taskDetails);

        db = new DatabaseOperationsMethods(this);

        getUi();
        getData();

        chbx_isDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (chbx_isDone.isChecked()) {
                    setAsTaskDone();
                }
            }
        });

        nestedScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                findViewById(R.id.scrollView1).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });


        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

    }

    private void getUi()
    {
        tv_task_date = findViewById(R.id.tv_task_details_date);
        tv_task_info = findViewById(R.id.tv_details_textTaskInfo);
        tv_updated = findViewById(R.id.tv_lastModified);
        tv_task_reminded = findViewById(R.id.tv_reminderTime);

        chbx_isDone = findViewById(R.id.chkBox_marks_as_done);
        nestedScrollView = findViewById(R.id.nestedScroll);
        scrollView = findViewById(R.id.scrollView1);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_details, menu);
        editMenu = menu.findItem(R.id.menu_edit_task);
        if (removeEdit)
        {
            editMenu.setVisible(false);
            menu.findItem(R.id.menu_remind_task).setVisible(false);
        }
        if (removeDel)
        {
            menu.findItem(R.id.menu_delete_task).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_task: {
                editTask();
                break;
            }


            case R.id.menu_delete_task:
                deleteTask();
                break;


            case R.id.menu_remind_task:

                getDate();
                break;

            default:
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
    }

    private void getDate()
    {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, final int year, int monthOfYear, int dayOfMonth)
            {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(TaskDetailsAndManipulation.this,
                        new TimePickerDialog.OnTimeSetListener()
                        {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                    {

                        view.setIs24HourView(false);



                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(date.getTimeInMillis());
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

                        String data = simpleDateFormat.format(calendar.getTime());
                        Log.v(TAG, "The choosen one " + data);

                        Intent intent = new Intent(TaskDetailsAndManipulation.this,MyReciever.class);
                        intent.putExtra("time",task_primary_key);

                        intent.putExtra("task",task_details);

                        PendingIntent pendingIntent = PendingIntent
                                .getBroadcast(TaskDetailsAndManipulation.this,0,intent,0);

                        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                        if (alarmManager != null) {
                            alarmManager.set(AlarmManager.RTC,date.getTimeInMillis() - 10000,pendingIntent);
                        }
                        db.open();
                        String saveReminder = db.setReminder(task_primary_key, String.valueOf(date.getTimeInMillis()));
                        db.close();
                        getData();
                        showSnackbar(saveReminder);


                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        },currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }



    private void getData()
    {
        db.open();
        Cursor cur = null;
        cur = db.fetchByTime(task_primary_key);
        System.out.println("data " + cur);
        if (!(cur.getCount() > 0)) {
            Toast.makeText(this, "Error Not found", Toast.LENGTH_SHORT).show();
            task_details = "Not Found";
            task_isDone = "Not Found";
            task_date = "Not Found";
            task_last_edited = "Not Found";
            showSnackbar("Nothing Found");


            tv_task_date.setText("Created on " + task_date);
            tv_task_info.setText(task_details);
            tv_updated.setText("Last edited on " + task_last_edited);
        }
        else
            {

            while (cur.moveToNext())

            {

                System.out.println("data " + cur);
                task_details = cur.getString(0);
                task_isDone = cur.getString(1);
                task_date = cur.getString(2);
                task_last_edited = cur.getString(3);
            }
            tv_task_date.setText("Created on " + task_date);
            tv_task_info.setText(task_details);
            tv_updated.setText("Last edited on " + task_last_edited);

            int taskDone = Integer.parseInt(task_isDone);
            if (taskDone == 1)
            {
                removeEdit = true;
                tv_updated.setText("Task Completed On " + task_last_edited);
                chbx_isDone.setEnabled(false);
                chbx_isDone.setChecked(true);
                chbx_isDone.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                invalidateOptionsMenu();
            }
        }

        Cursor cur2 = db.findReminder(task_primary_key);
        if (!(cur2.getCount() > 0))
        {
            tv_task_reminded.setVisibility(View.INVISIBLE);
        }
        else
            {
        while (cur2.moveToNext())
        {
            reminderTime = cur2.getString(1);
            tv_task_reminded.setVisibility(View.VISIBLE);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            String dateToday = sdf.format(Long.parseLong(reminderTime));
            tv_task_reminded.setText("Reminder set at " + dateToday);
            if (System.currentTimeMillis() > Long.parseLong(reminderTime))
            {
                tv_task_reminded.setVisibility(View.INVISIBLE);
            }

            if (System.currentTimeMillis() < Long.parseLong(reminderTime))
            {
                removeDel = true;
                invalidateOptionsMenu();
            }

        }}

        cur2.close();

        cur.close();
        db.close();

    }


    private void deleteTask()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete this task ? ");
        alert.setMessage("Clicking Delete will delete this task ! Continue ?");
        alert.setCancelable(true);
        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                db.open();
                int j = db.deleteTask(task_primary_key);
               // System.out.println("deleted " + j);
                if ((j > 0)) {
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(TaskDetailsAndManipulation.this, "Task Deleted", Toast.LENGTH_SHORT).show();
                                sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.run();
                    TaskDetailsAndManipulation.super.onBackPressed();
                }
                db.close();
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void editTask()
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(TaskDetailsAndManipulation.this);
        final View view = getLayoutInflater().inflate(R.layout.add_task_custum, null);


        Button btnUpdate = view.findViewById(R.id.btn_task_save);
        btnUpdate.setText("Update");


        alert.setView(view);

        alert.setCancelable(false).setTitle("Update this task")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        final AlertDialog dialog = alert.create();
        final EditText etTaskInfo = view.findViewById(R.id.et_add_task);
        etTaskInfo.requestFocus();
        etTaskInfo.setText(task_details);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String modifiedTask = etTaskInfo.getText().toString().trim();
                if (modifiedTask.isEmpty()) {

                    etTaskInfo.setError("Fill it !");
                    etTaskInfo.requestFocus();

                    return;
                } else if (modifiedTask.length() < 3) {
                    etTaskInfo.setError("Too short");
                    etTaskInfo.requestFocus();
                    return;
                } else
                    {
                    db.open();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                    String dateToday = sdf.format(new Date());
                    ContentValues values = new ContentValues();
                    values.put("INFO", modifiedTask);
                    values.put("LAST_UPDATE", dateToday);

                    int jk = db.updateTask(values, task_primary_key);
                    if (jk > 0)
                    {
                        showSnackbar("Updated");
                        getData();
                        dialog.cancel();
                        db.close();

                    }
                    db.close();

                }
            }
        });
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }


    private void setAsTaskDone() {
        AlertDialog.Builder ab = new AlertDialog.Builder(TaskDetailsAndManipulation.this);
        ab.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        })
                .setTitle("Set As Done ?")
                .setMessage("Clicking Ok will save this task as completed.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.open();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                        String dateToday = sdf.format(new Date());
                        ContentValues val = new ContentValues();
                        val.put("IS_DONE", 1);
                        val.put("LAST_UPDATE", dateToday);
                        int j = db.setTaskDone(val, task_primary_key);
                        db.close();
                        if (j > 0) {
                            showSnackbar("Marked as done");

                            getData();
                        }
                    }
                }).setCancelable(false);

        AlertDialog dialog = ab.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));

    }

    private void showSnackbar(String msgToShow) {
        Snackbar.make(relativeLayout, "" + msgToShow, Snackbar.LENGTH_SHORT).show();
    }
}
