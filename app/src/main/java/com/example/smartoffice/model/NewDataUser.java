package com.example.smartoffice.model;

import java.io.Serializable;
import java.util.List;

public class NewDataUser implements Serializable {
    public String content, starttime,endtime,date, title, usernamebooking, id, uri;

    public List<User> memberjoin;
    public Room roombooking;
    public  List<AttachmentFiles> attachmentfile;

    public NewDataUser(String content, String starttime, String endtime, String date, String title, String usernamebooking, String id, List<User> memberjoin, Room roombooking, List<AttachmentFiles> attachmentfile, String uri) {
        this(content, starttime, endtime, date, title, usernamebooking, id, memberjoin, roombooking, attachmentfile);
        this.uri = uri;
    }

    public NewDataUser(String content, String starttime, String endtime, String date, String title, String usernamebooking, String id, List<User> memberjoin, Room roombooking, List<AttachmentFiles> attachmentfile) {
        this.content = content;
        this.starttime = starttime;
        this.endtime = endtime;
        this.date = date;
        this.title = title;
        this.usernamebooking = usernamebooking;
        this.id = id;
        this.memberjoin = memberjoin;
        this.roombooking = roombooking;
        this.attachmentfile = attachmentfile;

    }
}
