package com.bphc.oops_project.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ToDo implements Parcelable {

    @SerializedName("title")
    public String title;

    @SerializedName("tasktime")
    public String tasktime;

    @SerializedName("description")
    public String description;

    @SerializedName("taskId")
    public int taskId;

    protected ToDo(Parcel in) {
        title = in.readString();
        tasktime = in.readString();
        description = in.readString();
        taskId = in.readInt();
    }

    public static final Creator<ToDo> CREATOR = new Creator<ToDo>() {
        @Override
        public ToDo createFromParcel(Parcel in) {
            return new ToDo(in);
        }

        @Override
        public ToDo[] newArray(int size) {
            return new ToDo[size];
        }
    };

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

    public ToDo(String title, String tasktime, String description) {
        this.title = title;
        this.tasktime = tasktime;
        this.description = description;
    }

    public ToDo(String title, String tasktime, String description, int taskId) {
        this.title = title;
        this.tasktime = tasktime;
        this.description = description;
        this.taskId = taskId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(tasktime);
        dest.writeString(description);
        dest.writeInt(taskId);
    }
}
