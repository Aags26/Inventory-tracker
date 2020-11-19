package com.bphc.oops_project.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.bphc.oops_project.prefs.SharedPrefs;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ItemGroup implements Parcelable {

    @SerializedName("categoryId")
    public int groupId;

    @SerializedName("userId")
    public String userId;

    @SerializedName("category")
    public String group;

    @SerializedName("items")
    public ArrayList<Item> items;

    public ItemGroup(String group, ArrayList<Item> items) {
        this.group = group;
        this.items = items;
    }

    public ItemGroup(int groupId, String userId, String group, ArrayList<Item> items) {
        this.groupId = groupId;
        this.userId = userId;
        this.group = group;
        this.items = items;
    }

    protected ItemGroup(Parcel in) {
        groupId = in.readInt();
        userId = in.readString();
        group = in.readString();
    }

    public static final Creator<ItemGroup> CREATOR = new Creator<ItemGroup>() {
        @Override
        public ItemGroup createFromParcel(Parcel in) {
            return new ItemGroup(in);
        }

        @Override
        public ItemGroup[] newArray(int size) {
            return new ItemGroup[size];
        }
    };

    public int getCategoryId() {
        return groupId;
    }

    public void setCategoryId(int categoryId) {
        this.groupId = categoryId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(groupId);
        dest.writeString(userId);
        dest.writeString(group);
    }
}
