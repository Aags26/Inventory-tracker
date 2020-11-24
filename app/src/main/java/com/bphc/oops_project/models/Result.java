package com.bphc.oops_project.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Result {

    @SerializedName("userId")
    public String userId;

    @SerializedName("name")
    public String name;

    @SerializedName("username")
    public String username;

    @SerializedName("email")
    public String email;

    @SerializedName("phoneVerified")
    public boolean phoneVerified;

    @SerializedName("professionGiven")
    public boolean professionGiven;

    @SerializedName("phone")
    public String phone;

    @SerializedName("profession")
    public String profession;

    @SerializedName("authToken")
    public String authToken;

    @SerializedName("categories")
    public ArrayList<ItemGroup> categories;

    @SerializedName("runningOut")
    public ArrayList<Item> runningOutItems;

    @SerializedName("todo")
    public ArrayList<ToDo> toDos;


    public Result(String userId, String name, String username, String email, boolean phoneVerified,
                  boolean professionGiven, String phone, String profession, String authToken, ArrayList<ItemGroup> categories,
                  ArrayList<ToDo> toDos, ArrayList<Item> runningOutItems) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phoneVerified = phoneVerified;
        this.professionGiven = professionGiven;
        this.phone = phone;
        this.profession = profession;
        this.authToken = authToken;
        this.categories = categories;
        this.toDos = toDos;
        this.runningOutItems = runningOutItems;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public boolean isProfessionGiven() {
        return professionGiven;
    }

    public void setProfessionGiven(boolean professionGiven) {
        this.professionGiven = professionGiven;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public ArrayList<ItemGroup> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<ItemGroup> categories) {
        this.categories = categories;
    }

    public ArrayList<ToDo> getToDos() {
        return toDos;
    }

    public void setToDos(ArrayList<ToDo> toDos) {
        this.toDos = toDos;
    }

    public ArrayList<Item> getRunningOutItems() {
        return runningOutItems;
    }

    public void setRunningOutItems(ArrayList<Item> runningOutItems) {
        this.runningOutItems = runningOutItems;
    }
}
