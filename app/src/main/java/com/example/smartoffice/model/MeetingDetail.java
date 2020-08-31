package com.example.smartoffice.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class MeetingDetail extends ArrayList<Parcelable>  implements Parcelable {
    private String content, starttime,endtime,date, title, usernamebooking, id, uri;
    private  String key;
    private List<User> memberjoin;
    private Room roombooking;
    private  List<AttachmentFiles> attachmentfile;

    public MeetingDetail(String content, String starttime, String endtime, String date, String title, String usernamebooking, String id, String uri, String key, List<User> memberjoin, Room roombooking, List<AttachmentFiles> attachmentfile) {
        this.content = content;
        this.starttime = starttime;
        this.endtime = endtime;
        this.date = date;
        this.title = title;
        this.usernamebooking = usernamebooking;
        this.id = id;
        this.uri = uri;
        this.key = key;
        this.memberjoin = memberjoin;
        this.roombooking = roombooking;
        this.attachmentfile = attachmentfile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsernamebooking() {
        return usernamebooking;
    }

    public void setUsernamebooking(String usernamebooking) {
        this.usernamebooking = usernamebooking;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<User> getMemberjoin() {
        return memberjoin;
    }

    public void setMemberjoin(List<User> memberjoin) {
        this.memberjoin = memberjoin;
    }

    public Room getRoombooking() {
        return roombooking;
    }

    public void setRoombooking(Room roombooking) {
        this.roombooking = roombooking;
    }

    public List<AttachmentFiles> getAttachmentfile() {
        return attachmentfile;
    }

    public void setAttachmentfile(List<AttachmentFiles> attachmentfile) {
        this.attachmentfile = attachmentfile;
    }

    public MeetingDetail() {
    }

    protected MeetingDetail(Parcel in) {
        content = in.readString();
        starttime = in.readString();
        endtime = in.readString();
        date = in.readString();
        title = in.readString();
        usernamebooking = in.readString();
        id = in.readString();
        uri = in.readString();
        key = in.readString();
        memberjoin = in.createTypedArrayList(User.CREATOR);
        roombooking = in.readParcelable(Room.class.getClassLoader());
        attachmentfile = in.createTypedArrayList(AttachmentFiles.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeString(starttime);
        dest.writeString(endtime);
        dest.writeString(date);
        dest.writeString(title);
        dest.writeString(usernamebooking);
        dest.writeString(id);
        dest.writeString(uri);
        dest.writeString(key);
        dest.writeTypedList(memberjoin);
        dest.writeParcelable(roombooking, flags);
        dest.writeTypedList(attachmentfile);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MeetingDetail> CREATOR = new Creator<MeetingDetail>() {
        @Override
        public MeetingDetail createFromParcel(Parcel in) {
            return new MeetingDetail(in);
        }

        @Override
        public MeetingDetail[] newArray(int size) {
            return new MeetingDetail[size];
        }
    };
}
