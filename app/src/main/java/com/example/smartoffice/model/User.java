package com.example.smartoffice.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private  String avatar, fullname, role, id, sdt;

    protected User(Parcel in) {
        avatar = in.readString();
        fullname = in.readString();
        role = in.readString();
        id = in.readString();
        sdt = in.readString();
    }

    public User() {
    }

    public User(String avatar, String fullname, String role, String id, String sdt) {
        this.avatar = avatar;
        this.fullname = fullname;
        this.role = role;
        this.id = id;
        this.sdt = sdt;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(avatar);
        dest.writeString(fullname);
        dest.writeString(role);
        dest.writeString(id);
        dest.writeString(sdt);
    }

    @Override
    public int describeContents() {
        return 0;
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
}
