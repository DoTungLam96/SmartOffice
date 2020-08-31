package com.example.smartoffice.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Room implements Parcelable {
    private String address, name, idroom, capacity;

    public Room() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdroom() {
        return idroom;
    }

    public void setIdroom(String idroom) {
        this.idroom = idroom;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public Room(String address, String name, String idroom, String capacity) {
        this.address = address;
        this.name = name;
        this.idroom = idroom;
        this.capacity = capacity;
    }

    protected Room(Parcel in) {
        address = in.readString();
        name = in.readString();
        idroom = in.readString();
        capacity = in.readString();
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(address);
        parcel.writeString(name);
        parcel.writeString(idroom);
        parcel.writeString(capacity);
    }
}
