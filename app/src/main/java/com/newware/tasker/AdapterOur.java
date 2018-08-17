package com.newware.tasker;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Bhuvaneshvar Nath Srivastava on 10-08-2018 at 12:44 AM.
 * Copyright (c) 2018
 **/
public class AdapterOur extends RecyclerView.Adapter<AdapterOur.MyviewHolder> {
    private List<TaskDetails> taskData;
    private Context mcontext;
    protected static final String PRIMARY_KEY = "primary_key";

//    interface OnitemCheckedListnerCustom
//    {
//        void onItemcheched(TaskDetails taskDetails);
//
//        void onItemUnchecked(TaskDetails taskDetails);
//    }

    public AdapterOur(List<TaskDetails> taskData, Context mcontext)
    {
        this.taskData = taskData;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.recyclerview_each_item_task, parent, false);
        return new MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyviewHolder holder, int position) {
        final TaskDetails current = taskData.get(position);
        //holder.chk_tasks.setChecked(Boolean.parseBoolean(String.valueOf(current.isTaskDone())));

        holder.tv_date.setText(current.getDateOfCreation());

        holder.tv_taskDetail.setText(current.getTaskInfo());
        if (current.isTaskDone == 1)
        {
            holder.tv_isdon.setText("Task Completed");
        }
        else
        {
            holder.tv_isdon.setVisibility(View.INVISIBLE);
        }



    }

    @Override
    public int getItemCount() {
        return taskData.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        public CheckBox chk_tasks;
        public TextView tv_date,tv_taskDetail,tv_isdon;

        public MyviewHolder(View itemView) {
            super(itemView);
//            chk_tasks = itemView.findViewById(R.id.chkbox_task);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_taskDetail = itemView.findViewById(R.id.tv_textTaskInfo);
            tv_isdon = itemView.findViewById(R.id.tv_isDone);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TaskDetails currentClicked = taskData.get(getAdapterPosition());

                    Intent gotoDetails = new Intent(mcontext,TaskDetailsAndManipulation.class);

                    gotoDetails.putExtra(PRIMARY_KEY,String.valueOf(currentClicked.getCurrentTime()));
                    gotoDetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mcontext.startActivity(gotoDetails);

                }
            });

        }


    }
}
