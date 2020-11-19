package com.bphc.oops_project.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    public String email;
    public String name;
    public String username;
    public String profession;
    public String phone;

    public User(String email, String name, String username, String profession, String phone) {
        this.email = email;
        this.name = name;
        this.username = username;
        this.profession = profession;
        this.phone = phone;
    }

    protected User(Parcel in) {
        email = in.readString();
        name = in.readString();
        username = in.readString();
        profession = in.readString();
        phone = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(profession);
        dest.writeString(phone);
    }
}
