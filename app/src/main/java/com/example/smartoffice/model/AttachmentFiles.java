package com.example.smartoffice.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AttachmentFiles implements Parcelable {
    private String name, type;

    public AttachmentFiles(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public AttachmentFiles() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    protected AttachmentFiles(Parcel in) {
        name = in.readString();
        type = in.readString();
    }

    public static final Creator<AttachmentFiles> CREATOR = new Creator<AttachmentFiles>() {
        @Override
        public AttachmentFiles createFromParcel(Parcel in) {
            return new AttachmentFiles(in);
        }

        @Override
        public AttachmentFiles[] newArray(int size) {
            return new AttachmentFiles[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(type);
    }
}
