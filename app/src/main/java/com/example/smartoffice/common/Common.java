package com.example.smartoffice.common;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.format.DateFormat;

import com.example.smartoffice.model.AttachmentFiles;
import com.example.smartoffice.model.DetailRoomBooking;
import com.example.smartoffice.model.MeetingDetail;
import com.example.smartoffice.model.Room;
import com.example.smartoffice.model.User;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;

public class Common {

    public static final String KEY_MEETING_ALL_ACTIVITY = "KEY_MEETING_ALL" ;
    public static final String PDF = "pdf";
    public static final String DOCX = "docx";
    public static String KEY_CURRENT_USER = "Current user" ;
    public static String ACTION_SEND_DATA_LOGIN = "Login data";
    public  static  final String KEY_ROOM = "KEY_ROOM";
    public static final Integer KEY_LOGIN = 1;
    public static String currentUser;
    public  static boolean isFirstRun  = true;
    public  static final String USER = "User";
    public  static final String ROOM = "Room";
    public  static final String Attachment = "Attachment";
    public  static final String MEETINGDETAIL = "MeetingDetail";
    public  static SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
    public  static   SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE");
    public  static SimpleDateFormat formatterTimeHour = new SimpleDateFormat("HH:mm", Locale.UK);
    public  static    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    public  static HashMap<String , List<DetailRoomBooking> > hashMapCommon = null;
    public  static HashMap<String , Uri > hashMapUri = new HashMap<>();

    public  static int convertDateToString(String format, Date date){
        return  Integer.parseInt(DateFormat.format(format,   date).toString());
    }

    public static String dateSelected="";
    public static List<User> listAllUsers = null;
    public static List<Room> listAllRooms = null;
    public static List<AttachmentFiles> listAllAtachments = null;
    public static List<MeetingDetail> listAllMeetingDetail = null;
    public static  void ShowMessageError(Context context , String message, int longs)
    {
        Toasty.error(context,message, longs).show();
    }

    public static  void ShowMessageSuccess(Context context , String message, int longs)
    {
        Toasty.success(context,message, longs).show();
    }

    public static final String prefixEmail ="@viettel.com.vn";

    public static String  Email ;

    public static List<Uri> uriList = new ArrayList<>();

    public static String cutStringIfSoLong(int maxLength, String source) {
        return source.length() > maxLength ? new StringBuilder(source.substring(0,maxLength-3)).append("...").toString() : source ;
    }

    public static String getPathFile(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {

            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static Uri addToDeviceCalendar(Context context, String startEventTime, String endEventTime, String title, String description, String location) {

        try {
            Uri uri = null;
            Date startDate = simpleDateFormat.parse(startEventTime);
            Date endDate = simpleDateFormat.parse(endEventTime);
            if (isEventInCalendar(context, title, startDate.getTime(), endDate.getTime()))
            {return uri;}
            ContentResolver cr = context.getContentResolver();
            ContentValues contentValues = new ContentValues();

            //put values
            contentValues.put(CalendarContract.Events.CALENDAR_ID , getCalendar(context));
            contentValues.put(CalendarContract.Events.TITLE, title);
            contentValues.put(CalendarContract.Events.DESCRIPTION, description);
            contentValues.put(CalendarContract.Events.EVENT_LOCATION, location);

            //time
            contentValues.put(CalendarContract.Events.DTSTART, startDate.getTime());
            contentValues.put(CalendarContract.Events.DTEND, endDate.getTime());
            contentValues.put(CalendarContract.Events.ALL_DAY, 0);
            contentValues.put(CalendarContract.Events.HAS_ALARM, 1);

            String timeZone = TimeZone.getDefault().getID();
            contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone);

            uri = cr.insert(CalendarContract.Events.CONTENT_URI, contentValues);
            hashMapUri.put(startEventTime, uri);
            return  uri;
        }
        catch (Exception ex)
        {
            Toasty.error(context, "Error push calendar: "+ex.getMessage(), Toasty.LENGTH_LONG).show();
        }
        return null;
    }
    public static boolean isEventInCalendar(Context context, String titleText, long dtStart, long dtEnd) {
        final String[] projection = new String[]{CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.TITLE};
        Cursor cursor = CalendarContract.Instances.query(context.getContentResolver(), projection, dtStart, dtEnd);
        return cursor != null && cursor.moveToFirst() && cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.TITLE)).equalsIgnoreCase(titleText);
    }

    public static void DeleteAllNotify(Context context, String uri) {
       context.getContentResolver().delete(Uri.parse(uri), null, null);
    }

    public static void DeleteNotify(Context context, String key) {
      if (!hashMapUri.isEmpty())
      {
         Uri uri = hashMapUri.get(key);
          context.getContentResolver().delete(uri, null, null);
      }
    }

    public static String getCalendar(Context activity) {
        //get default calendar ID of calendar of Gmail
        String idMailCalendar = "";
        String projection[] = {"_id","calendar_displayName"};
        Uri uri = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = activity.getContentResolver();
        Cursor cursor = contentResolver.query(uri, projection, null, null, null );
        if (cursor.moveToFirst())
        {
            String callName = "";
            int calName = cursor.getColumnIndex(projection[1]);
            int idCol = cursor.getColumnIndex(projection[0]);

            do {
                callName = cursor.getString(calName);
                if (callName.contains("@gmail.com"))
                {
                    idMailCalendar = cursor.getString(idCol);
                    break;
                }
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return idMailCalendar;
    }
}
