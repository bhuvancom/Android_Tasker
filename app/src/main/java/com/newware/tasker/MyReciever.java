package com.newware.tasker;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Bhuvaneshvar on 8/13/18 at 2:42 AM
 **/
public class MyReciever extends BroadcastReceiver
{
    String time,task;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        time = intent.getStringExtra("time");
        task = intent.getStringExtra("task");

        Log.i("alarm","alarm is called");

        Toast.makeText(context, "Task is waiting", Toast.LENGTH_SHORT).show();

        Intent alrm = new Intent("android.intent.action.Alert");
        alrm.putExtra("primary_key",time);
        alrm.putExtra("task",task);
        alrm.setClass(context, AlertDialogClass.class);
        alrm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alrm.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        context.startActivity(alrm);
    }
}
