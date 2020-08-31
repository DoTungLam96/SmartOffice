package com.example.smartoffice.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.smartoffice.R;
import com.example.smartoffice.adapter.AdapterEditAttach;
import com.example.smartoffice.adapter.AdapterEditMemberJoin;
import com.example.smartoffice.adapter.AutoCompleteAdapter;
import com.example.smartoffice.common.Common;
import com.example.smartoffice.interfaces.IRemoveItem;
import com.example.smartoffice.model.AttachmentFiles;
import com.example.smartoffice.model.DetailRoomBooking;
import com.example.smartoffice.model.MeetingDetail;
import com.example.smartoffice.model.NewDataUser;
import com.example.smartoffice.model.Room;
import com.example.smartoffice.model.TimeDetail;
import com.example.smartoffice.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class EditDetailMeetingActivity extends AppCompatActivity implements View.OnClickListener, IRemoveItem {

    private Toolbar toolbar;
    public MeetingDetail meetingDetail;
    private EditText edt_date_detail, edt_title_detail,
            edt_content_detail;
    private Button btn_update, btn_delete;
    private  boolean isChooseDate, isUpdateSuccess;
    private boolean isFirstCorrect = false;
    private boolean isChooseStartTime;
    private TextView txt_upload_file, txt_title_toolbar, txt_time_detail_start, txt_time_detail_end;
    private  IRemoveItem iRemoveItem;
    private  int  widthRecycler,widthTxt;
    private  long startCountTime, endCountTime;
    private AutoCompleteTextView txt_search_user;
    private int day = 0, monthNumber = 0, year = 0;
    private DatabaseReference firebaseDatabase;
    private Spinner spinnerAddress;
    private  static final int FILE_SELECT_CODE = 10;
    private AdapterEditAttach adapterEditAttachment;
    private AdapterEditMemberJoin  adapterEditMemberJoin;
    private String startHour, endHour;
    private AlertDialog alertDialog;
    private RecyclerView recycler_edit_member, recycler_attachment_edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_detail_meeting);
        init();
        getDataFromInformationAcitvity();
        suggestFindUser();
    }

    //region init view and property
    private void init() {
        spinnerAddress = findViewById(R.id.spinner_address_detail);
        edt_date_detail = findViewById(R.id.edt_date_detail);
        txt_time_detail_start = findViewById(R.id.txt_time_detail_start);
        txt_time_detail_end = findViewById(R.id.txt_time_detail_end);
        edt_content_detail = findViewById(R.id.edt_content_detail);
        edt_title_detail = findViewById(R.id.edt_title_detail);
        txt_upload_file = findViewById(R.id.txt_tai_file);
        txt_title_toolbar = findViewById(R.id.txt_title_toolbar);
        btn_delete = findViewById(R.id.btn_detele_user);
        btn_update = findViewById(R.id.btn_update);
        txt_search_user = findViewById(R.id.txt_search_member);

        //init firebase database
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        recycler_attachment_edit = findViewById(R.id.recycler_attachment);
        recycler_edit_member = findViewById(R.id.recycler_edit_member_join);

        widthRecycler = recycler_attachment_edit.getWidth();
        widthTxt = txt_upload_file.getWidth();

        alertDialog = new SpotsDialog.Builder().setContext(EditDetailMeetingActivity.this).setCancelable(false).build();

        //on click listener
        iRemoveItem = this;
        btn_update.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        txt_upload_file.setOnClickListener(this);
        edt_date_detail.setOnClickListener(this);
        txt_time_detail_start.setOnClickListener(this);
        txt_time_detail_end.setOnClickListener(this);

        //setup toolbar
        toolbar = findViewById(R.id.tool_bar);
        txt_title_toolbar.setText(getString(R.string.suacuochop));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    //endregion

    //region search or add user to list user member
    private void suggestFindUser() {
        if (Common.listAllUsers.isEmpty()) return;

        AutoCompleteAdapter adapterString = new AutoCompleteAdapter(EditDetailMeetingActivity.this, Common.listAllUsers);
        txt_search_user.setAdapter(adapterString);
        //event click Autocomplete
        txt_search_user.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Clear text
                txt_search_user.setText("");
                User user = Common.listAllUsers.get(i);
                for (User user1 : meetingDetail.getMemberjoin()){
                    if (user.getId().equals(user1.getId()))
                    {
                        Toasty.warning(EditDetailMeetingActivity.this, getString(R.string.thanhviendathamgia), Toast.LENGTH_SHORT).show();
                       // Toast.makeText(;
                        return;
                    }
                }
                meetingDetail.getMemberjoin().add(user);
            }
        });
    }
    //endregion

    //region get data from InformationDetail activity
    private void getDataFromInformationAcitvity() {
        Intent intent = getIntent();
        meetingDetail = intent.getParcelableExtra(Common.KEY_MEETING_ALL_ACTIVITY);
        if (meetingDetail != null)
        {
           edt_title_detail.setText( meetingDetail.getTitle());
            edt_content_detail.setText( meetingDetail.getContent());

            //setup spinner address
            final String address = meetingDetail.getRoombooking().getAddress();
           List<String> stringList = new ArrayList<>();
           for (Room r : Common.listAllRooms)
           {
               stringList.add(r.getAddress());
           }
            //Creating the ArrayAdapter instance having the country list
            ArrayAdapter adapterSpinner = new ArrayAdapter(this,android.R.layout.simple_spinner_item, stringList);
             adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            spinnerAddress.setAdapter(adapterSpinner);
            int spinnerPosition = adapterSpinner.getPosition(address);
            spinnerAddress.setSelection(spinnerPosition);
            spinnerAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    meetingDetail.setRoombooking(Common.listAllRooms.get(pos));
                }
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            startHour = meetingDetail.getStarttime();
            endHour = meetingDetail.getEndtime();

            txt_time_detail_start.setText(Html.fromHtml(getString(R.string.thoigianhop," <b><u>"+startHour+"</u></b>")));
            txt_time_detail_end.setText(Html.fromHtml(getString(R.string.thoigianhop," <b><u>"+endHour+"</u></b>")));
            edt_date_detail.setText(Common.dateSelected);
            setUpRecyclerMemberJoin(meetingDetail.getMemberjoin());
            setUpRecyclerAttachFiles(meetingDetail.getAttachmentfile());
        }
    }

    private void setUpRecyclerMemberJoin(List<User> memberjoin) {
        if (memberjoin == null) return;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recycler_edit_member.setLayoutManager(gridLayoutManager);
        adapterEditMemberJoin = new AdapterEditMemberJoin(memberjoin, R.layout.custom_layout_edit_member_join, this, iRemoveItem);
        recycler_edit_member.setAdapter(adapterEditMemberJoin);
    }

    private void setUpRecyclerAttachFiles(List<AttachmentFiles> filesList) {
        int widthRecycler = recycler_attachment_edit.getWidth();
        int widthTxt = txt_upload_file.getWidth();
        if (filesList == null || filesList.size() == 0 )
        {
            recycler_attachment_edit.setVisibility(View.GONE);
            txt_upload_file.setWidth(widthRecycler+widthTxt - 100);
        }
        else {
            recycler_attachment_edit.setVisibility(View.VISIBLE);
        }
        LinearLayoutManager linear = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        recycler_attachment_edit.setLayoutManager(linear);
        adapterEditAttachment = new AdapterEditAttach(filesList, R.layout.custom_layout_edit_member_join, this, iRemoveItem);
        recycler_attachment_edit.setAdapter(adapterEditAttachment);
    }

    //endregion

    //region handle event click on view
    @Override
    public void onClick(View view) {
       switch (view.getId())
       {
           case R.id.btn_detele_user :
           {
               DeleteBookingRoom();
           }
           break;
           case R.id.btn_update :
           {
               alertDialog.show();
               new Handler().postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       UpdateMeetingDetailUser(meetingDetail.getRoombooking().getAddress(), meetingDetail.getDate());
                   }
               },1200);

           }
           break;
           case R.id.txt_tai_file :
           {
              showFileChooser();
           }
           break;
           case R.id.edt_date_detail :
           {
              setUpDate();
           }
           break;
           case R.id.txt_time_detail_start :
           {
               setUpTimeStart();
           }
           break;
           case R.id.txt_time_detail_end :
           {
               setUpTimeStop();
           }
           break;
           default: break;
       }
    }

    //region Handler login when click btn delete
    private void DeleteBookingRoom() {
        final String key = meetingDetail.getKey();
        androidx.appcompat.app.AlertDialog.Builder confirmDialog = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogCustom)
                .setCancelable(false)
                .setMessage(getString(R.string.banmuonxoalichhopkhong))
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        firebaseDatabase.child(Common.MEETINGDETAIL).child(key).removeValue();
                        Common.ShowMessageSuccess(EditDetailMeetingActivity.this,getString(R.string.xoalichhop),Toasty.LENGTH_SHORT);
                        Common.DeleteNotify(EditDetailMeetingActivity.this, meetingDetail.getDate()+" "+meetingDetail.getStarttime());
                        startActivity(new Intent(EditDetailMeetingActivity.this, MainActivity.class));
                        finish();
                    }
                });
        confirmDialog.show();
    }
    //endregion

    //region handle logic when click btnUpdate
    private void UpdateMeetingDetailUser(String addressRoom, String currentDate) {
        try {
            if (alertDialog.isShowing()) alertDialog.dismiss();
            if (Common.hashMapCommon.size() > 0)
            {
                boolean isCheck2nd = false;
                List<TimeDetail> listTimeDetail = new ArrayList<>();
                List<String> dateInRoomExist = new ArrayList<>();
                long startHourSelected  = new Time(Common.formatterTimeHour.parse(txt_time_detail_start.getText().toString()).getTime()).getTime();

                 List<DetailRoomBooking> bookingList = Common.hashMapCommon.get(addressRoom);

                 for (DetailRoomBooking detailRoomBooking : bookingList)
                 {
                     dateInRoomExist.add(detailRoomBooking.getDateDetail());
                 }
                Collections.sort(bookingList, new Comparator<DetailRoomBooking>() {
                    @Override
                    public int compare(DetailRoomBooking detailRoomBooking, DetailRoomBooking t1) {
                        int compare = 0;
                        try {
                            compare = new Time(Common.formatterTimeHour.parse(t1.getEndTime()).getTime()).compareTo(
                                    new Time(Common.formatterTimeHour.parse(detailRoomBooking.getEndTime()).getTime())
                            );
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return  compare;

                    }
                });
                 if (dateInRoomExist.contains(meetingDetail.getDate()))
                 {
                         for (int i = 0 ; i<  bookingList.size(); i++)
                         {
                             if (bookingList.get(i).getDateDetail().equals(currentDate))
                             {
                                 long lastTime = new Time(Common.formatterTimeHour.parse(bookingList.get(i).getEndTime()).getTime()).getTime();
                                 if (startHourSelected > lastTime && !isCheck2nd )
                                 {
                                     isFirstCorrect = true;
                                     CheckOvertimeBooking(new ArrayList<TimeDetail>());
                                     break;
                                 }
                                 if (startHourSelected > lastTime)
                                 {
                                     isCheck2nd = true;
                                     continue;
                                 }
                                 listTimeDetail.add(new TimeDetail(bookingList.get(i).getStartTime(),bookingList.get(i).getEndTime()));
                                 isCheck2nd = true;
                             }
                         }
                     CheckOvertimeBooking(listTimeDetail);
                 }
                 else {
                     isFirstCorrect = true;
                     CheckOvertimeBooking(new ArrayList<TimeDetail>());
                 }
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(this, ex.getMessage()+"", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("RestrictedApi")
    private void CheckOvertimeBooking(List<TimeDetail> listTimeDetail ) {
        String content = edt_content_detail.getText().toString();
        String startTime = txt_time_detail_start.getText().toString().trim();
        String endTime = txt_time_detail_end.getText().toString().trim();
        String date = meetingDetail.getDate();
        String title = edt_title_detail.getText().toString();
        String userBooking = meetingDetail.getUsernamebooking();
        List<User> memberJoin = new ArrayList<>(meetingDetail.getMemberjoin());
        Room room = meetingDetail.getRoombooking();
        String id = meetingDetail.getId();
        List<AttachmentFiles> attachmentFiles = meetingDetail.getAttachmentfile();
        String key = meetingDetail.getKey();
        String uri = meetingDetail.getUri();
        if (startCountTime > endCountTime) return;

         //Check null here
        if ( TextUtils.isEmpty(title) || TextUtils.isEmpty(date)
                || TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime) )
        {
            Common.ShowMessageError(this,getString(R.string.khongdetronggiatri),Toasty.LENGTH_SHORT);
            return;
        }

        NewDataUser newDataUser = new NewDataUser(content, startTime, endTime, date, title, userBooking, id,memberJoin,room,attachmentFiles,uri);
        if (isFirstCorrect)
        {
            firebaseDatabase.child(Common.MEETINGDETAIL).child(key).setValue(newDataUser);
            isFirstCorrect = false;
            isUpdateSuccess = true;
            Common.ShowMessageSuccess(EditDetailMeetingActivity.this,getString( R.string.capnhatdatlich),Toasty.LENGTH_SHORT);
            startActivity(new Intent(EditDetailMeetingActivity.this, MainActivity.class));
            finish();
            return;
        }
        if (listTimeDetail.size() > 0 )
        {
            try {
                if (isUpdateSuccess) return;
                TimeDetail lastItem = listTimeDetail.get(listTimeDetail.size() - 1 );
                long lastTimeBooked = new Time(Common.formatterTimeHour.parse(lastItem.getStartTimeDetail()).getTime()).getTime();
                long endHourSelected  = new Time(Common.formatterTimeHour.parse(txt_time_detail_end.getText().toString()).getTime()).getTime();

                    if ( endHourSelected > lastTimeBooked )
                    {
                        Toasty.warning(this,getString(R.string.khunggiochuachinhxac),Toasty.LENGTH_LONG).show();
                    }
                    else
                    {
                        firebaseDatabase.child(Common.MEETINGDETAIL).child(key).setValue(newDataUser);

                        Common.ShowMessageSuccess(EditDetailMeetingActivity.this,getString( R.string.capnhatdatlich),Toasty.LENGTH_SHORT);
                        startActivity(new Intent(EditDetailMeetingActivity.this, MainActivity.class));
                        finish();
                    }
            }catch (Exception ex)
            {
                Toast.makeText(this, ex.getMessage()+"", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if (isUpdateSuccess) return;
            Toasty.warning(this,getString(R.string.khunggiochuachinhxac),Toasty.LENGTH_LONG).show();
        }
    }
    //endregion

    //endregion

    //region get and set time
    private void setUpTimeStart() {
        String[] startTimeHour = startHour.split(":");
        String minStart = startTimeHour[1].trim();
        String hourStart = startTimeHour[0].trim();
        isChooseStartTime = false;
        TimePickerDialog timePickerDialog= new TimePickerDialog(this, R.style.DialogTheme_Time, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                if (i < 9 || i >18)
                {
                    Toasty.warning(EditDetailMeetingActivity.this,"Bạn vui lòng đặt lịch họp trong giờ hành chính",Toasty.LENGTH_SHORT).show();
                    return;
                }
                  String  newHourStart= i+"";
                  String newMinStart = i1+"";
                if (i1<10)
                {
                    newMinStart = "0"+newMinStart;
                }

                txt_time_detail_start.setText(Html.fromHtml(" <b><u>"+newHourStart+":"+newMinStart+"</u></b>"));
                  isChooseStartTime = true;
            }
        },Integer.valueOf(hourStart),Integer.valueOf(minStart), true);

        timePickerDialog.setCanceledOnTouchOutside(false);
        timePickerDialog.show();
    }

    private void setUpTimeStop() {
        if (!isChooseStartTime)
        {
            Toasty.warning(EditDetailMeetingActivity.this,"Vui lòng chọn thời gian bắt đầu trước.",Toasty.LENGTH_SHORT).show();
            return;
        }
        final String[] endTimeHour = endHour.split(":");
        String minEnd = endTimeHour[1].trim();
        String hourEnd = endTimeHour[0].trim();
        TimePickerDialog timePickerDialog= new TimePickerDialog(this, R.style.DialogTheme_Time, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                try {
                    if (i < 9 || i >18)
                    {
                        Toasty.warning(EditDetailMeetingActivity.this,R.string.giohanhchinh,Toasty.LENGTH_SHORT).show();
                        return;
                    }
                    String  newHour= i+"";
                    String newMin = i1+"";
                    startCountTime = new Time(Common.formatterTimeHour.parse(txt_time_detail_start.getText().toString()).getTime()).getTime();
                    endCountTime  = new Time(Common.formatterTimeHour.parse(i+":"+i1).getTime()).getTime();
                    if (startCountTime > endCountTime)
                    {
                        Toasty.warning(EditDetailMeetingActivity.this,R.string.thoigianketthucnhohon,Toasty.LENGTH_SHORT).show();
                        return;
                    }
                    if (i1<10)
                    {
                        newMin = "0"+newMin;
                    }

                    txt_time_detail_end.setText(Html.fromHtml(" <b><u>"+newHour+":"+newMin+"</u></b>"));
                    isChooseStartTime = true;
                }
                catch (Exception e)
                {
                    Toast.makeText(EditDetailMeetingActivity.this, e.getMessage()+"", Toast.LENGTH_SHORT).show();
                }

            }
        },Integer.valueOf(hourEnd),Integer.valueOf(minEnd), true);
        timePickerDialog.setCanceledOnTouchOutside(false);
        timePickerDialog.show();
    }
    //endregion

    //region get and set date
    private void setUpDate() {
        // get date
        final Calendar calendar = Calendar.getInstance();
        String dateString = meetingDetail.getDate();
        Date currentDate = new Date();
        final long currentDateMilis = currentDate.getTime();
        final String[] dayOfWeek = new String[1];
        try {
            if (!isChooseDate)
            {
                Date date = Common.formatDate.parse(dateString);
                day     = Common.convertDateToString("dd",   date);
                monthNumber  = Common.convertDateToString("MM",   date);
                year         =  Common.convertDateToString("yyyy",   date);
            }
            new DatePickerDialog(this, R.style.my_dialog_theme, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int years, int monthOfYear, int dayOfMonth) {
                    calendar.set(years, monthOfYear, dayOfMonth);
                    Long dateSelectedMilis = calendar.getTimeInMillis();
                    if (currentDateMilis > dateSelectedMilis)
                    {
                        Toasty.warning(EditDetailMeetingActivity.this,R.string.ngaydatlichkhongdung,Toasty.LENGTH_SHORT).show();
                        return;
                    }
                    String dateSelected = Common.formatDate.format(calendar.getTime());
                    meetingDetail.setDate(dateSelected);
                    day = dayOfMonth;
                    monthNumber = monthOfYear + 1;
                    year = years;
                    try {
                        dayOfWeek[0] = Common.dayOfWeekFormat.format(Common.formatDate.parse(dateSelected));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    edt_date_detail.setText(dayOfWeek[0]+" "+dateSelected);

                    isChooseDate = true;
                }
            }, year, monthNumber - 1, day).show();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region get and show files from SDcard
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("*/*");

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri content_describer = data.getData();
                    String nameFile = "";
                    if (content_describer != null)
                    {
                        File file= new File(content_describer.getPath());
                        file.getName();
                        nameFile = file.getAbsolutePath();
                        int cut = nameFile.lastIndexOf('/');
                        if (cut != -1) {
                            nameFile = nameFile.substring(cut + 1);
                        }
                        if (meetingDetail.getAttachmentfile() == null)
                        meetingDetail.setAttachmentfile(new ArrayList<AttachmentFiles>());

                        if (meetingDetail.getAttachmentfile().size() >= 2)
                        {
                            Toasty.warning(EditDetailMeetingActivity.this,"Bạn chỉ được upload tối đa 2 files",Toasty.LENGTH_SHORT).show();
                             return;
                        }
                        AttachmentFiles attachmentFiles = new AttachmentFiles(nameFile,"pdf");

                        try {
                            recycler_attachment_edit.setVisibility(View.VISIBLE);
                            recycler_attachment_edit.getLayoutParams().width = widthRecycler;
                            txt_upload_file.setWidth(widthTxt);

                            meetingDetail.getAttachmentfile().add(meetingDetail.getAttachmentfile().size(),attachmentFiles);
                            adapterEditAttachment.notifyItemInserted(meetingDetail.getAttachmentfile().size());
                            adapterEditAttachment.notifyDataSetChanged();
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(this, e.getMessage()+"", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    //endregion

    //region update user or delete user
    @Override
    public void RemoveItemInListUser(int position) {
        meetingDetail.getMemberjoin().remove(position);
        recycler_edit_member.removeViewAt(position);
        adapterEditMemberJoin.notifyItemRemoved(position);
        adapterEditMemberJoin.notifyItemRangeChanged(position, meetingDetail.getMemberjoin().size());
    }

    @Override
    public void RemoveItemInListAttachment(int position) {
       // recycler_attachment_edit.setVisibility(View.GONE);
        meetingDetail.getAttachmentfile().remove(position);
        recycler_attachment_edit.removeViewAt(position);
        adapterEditAttachment.notifyItemRemoved(position);
        adapterEditAttachment.notifyItemRangeChanged(position, meetingDetail.getAttachmentfile().size());

        if (meetingDetail.getAttachmentfile().size() == 0)
        {
            recycler_attachment_edit.setVisibility(View.GONE);
            txt_upload_file.setWidth(widthRecycler+widthTxt - 100);
        }

    }
    //endregion
}