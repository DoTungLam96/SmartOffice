package com.example.smartoffice.model;

public class TimeDetail {
    private String startTimeDetail, endTimeDetail;

    public TimeDetail(String startTimeDetail, String endTimeDetail) {
        this.startTimeDetail = startTimeDetail;
        this.endTimeDetail = endTimeDetail;
    }
    public TimeDetail(){

    }

    public String getStartTimeDetail() {
        return startTimeDetail;
    }

    public void setStartTimeDetail(String startTimeDetail) {
        this.startTimeDetail = startTimeDetail;
    }

    public String getEndTimeDetail() {
        return endTimeDetail;
    }

    public void setEndTimeDetail(String endTimeDetail) {
        this.endTimeDetail = endTimeDetail;
    }
}
