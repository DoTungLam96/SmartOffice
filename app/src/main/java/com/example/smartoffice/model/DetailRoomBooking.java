package com.example.smartoffice.model;

import java.util.List;

public class DetailRoomBooking {
    private  String dateDetail , startTime, endTime;

    public DetailRoomBooking() {
    }

    public DetailRoomBooking(String dateDetail, String startTime, String endTime) {
        this.dateDetail = dateDetail;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getDateDetail() {
        return dateDetail;
    }

    public void setDateDetail(String dateDetail) {
        this.dateDetail = dateDetail;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
