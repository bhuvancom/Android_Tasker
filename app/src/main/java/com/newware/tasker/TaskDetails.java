package com.newware.tasker;

/**
 * Created by Bhuvaneshvar Nath Srivastava on 10-08-2018 at 12:45 AM.
 * Copyright (c) 2018
 **/
public class TaskDetails
{
    String taskInfo;
    long currentTime;
    int isTaskDone;
    String dateOfCreation;
    String lastEdited;

    public TaskDetails(String taskInfo, long currentTime, int isTaskDone, String dateOfCreation, String lastEdited) {
        this.taskInfo = taskInfo;
        this.currentTime = currentTime;
        this.isTaskDone = isTaskDone;
        this.dateOfCreation = dateOfCreation;
        this.lastEdited = lastEdited;
    }


    public String getTaskInfo() {
        return taskInfo;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public int getIsTaskDone() {
        return isTaskDone;
    }

    public String getDateOfCreation() {
        return dateOfCreation;
    }

    public String getLastEdited() {
        return lastEdited;
    }
}
