package com.bphc.oops_project.models;

import com.google.gson.annotations.SerializedName;

public class ToDo {

    @SerializedName("title")
    public String title;

    @SerializedName("tasktime")
    public String tasktime;

    @SerializedName("description")
    public String description;

    @SerializedName("taskId")
    public int taskId;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTasktime() {
        return tasktime;
    }

    public void setTasktime(String tasktime) {
        this.tasktime = tasktime;
    }

    public ToDo(String title, String tasktime, String description, int taskId) {
        this.title = title;
        this.tasktime = tasktime;
        this.description = description;
        this.taskId = taskId;
    }
}
