package com.example.smartoffice.customview;

import java.util.Date;

public class CourseEvent {
    private String nameEvent;

    private Date startDatetime;

    private Date endDatetime;

    private String address;

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    private  Date currentDate;

    public CourseEvent(String nameEvent, Date startDatetime, Date endDatetime,  String address, Date currentDate) {
        this.nameEvent = nameEvent;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.address = address;
        this.currentDate = currentDate;
    }

    public String getNameEvent() {
        return nameEvent;
    }

    public void setNameEvent(String nameEvent) {
        this.nameEvent = nameEvent;
    }

    public Date getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(Date startDatetime) {
        this.startDatetime = startDatetime;
    }

    public Date getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(Date endDatetime) {
        this.endDatetime = endDatetime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
