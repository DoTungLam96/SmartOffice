package com.example.smartoffice.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.smartoffice.R;
import com.example.smartoffice.adapter.AdapterHomeBooking;
import com.example.smartoffice.common.Common;
import com.example.smartoffice.model.AttachmentFiles;
import com.example.smartoffice.model.DetailRoomBooking;
import com.example.smartoffice.model.MeetingDetail;
import com.example.smartoffice.model.NewDataUser;
import com.example.smartoffice.model.Room;
import com.example.smartoffice.model.TimeDetail;
import com.example.smartoffice.model.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbarMain;
    private ImageView img_avatar, img_checkin, im_logout;
    private MeetingDetail meetingDetailCurrent;
    TextView txt_account, txt_role, txt_count_meeting, txt_view_all;
    private DatabaseReference mDatabaseUser, mDatabaseBookingRoom, mAllUser, mAllRoom, mAllAttachments, mMeetingDetail;
    SharedPreferences sharedPreferences;
    ImageView img_no_meeting;
    private boolean isFirstRun;
    private BottomSheetDialog bottomSheetDialog;
    private  boolean isChooseStartTime, isChooseDate;
    private LinearLayout btn_dat_lich_hop, btn_kiem_tra_phong, btn_checkin, btn_checkout;
    List<MeetingDetail> listMeeting;
    private  boolean isUpdateSuccess;
    private boolean isFirstCorrect = false;
    private int day = 0, monthNumber = 0, year = 0;
    GridLayout gridLayout;
    private BroadcastReceiver br_listen_time_change;
    AdapterHomeBooking adapterHomeBooking;
    RecyclerView recycler_room_detail_home;
    LinearLayout linearLayout;
    private  Room room;
    private User m_user;
    String m_starttime, m_dateSelected, m_endtime, m_currentDate, m_title;
    private AlertDialog alertDialog;
    List<DetailRoomBooking> detailRoomBookings ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        getInFormationUserLogin();
        doSaveUser();
        getAllUserFromServer();
        getAllRoomFromServer();
        getAlAttachmentFromServer();
        getAllListMeetingDetail();
        listenTimeChange();
    }

    //region register broadcast receiver
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver();
    }
    private void registerReceiver() {
        IntentFilter intent = new IntentFilter();
        intent.addAction(Intent.ACTION_TIME_TICK);
        intent.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intent.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(br_listen_time_change, intent);
    }
    //endregion

    //region notify to all user
    private void pushAllNotify(List<MeetingDetail> userList) {
        if (userList.size() > 0)
        {
                    String start_time, end_time, date, title, address, content;
                    Date currentDate = new Date();
                    final long currentDateMilis = currentDate.getTime();
                    for (MeetingDetail meetingDetail : userList)
                    {
                        try {
                            long getDateMilis = Common.formatDate.parse(meetingDetail.getDate()).getTime();
                            if (currentDateMilis < getDateMilis)
                            {
                                start_time = meetingDetail.getStarttime();
                                end_time = meetingDetail.getEndtime();
                                date = meetingDetail.getDate();
                                title = meetingDetail.getTitle();
                                content = meetingDetail.getContent();
                                address = meetingDetail.getRoombooking().getAddress();
                                Common.addToDeviceCalendar(MainActivity.this, date+" "+start_time,
                                        date+" "+end_time, title, content, address);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

        }
    }
    //endregion

    //region getAllListMeetingDetail
    private void getAllListMeetingDetail() {
        mMeetingDetail = FirebaseDatabase.getInstance().getReference().child(Common.MEETINGDETAIL);
        Common.listAllMeetingDetail = new ArrayList<>();
        Common.hashMapCommon = new HashMap<>();
        mMeetingDetail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    MeetingDetail meetingDetail = dataSnapshot.getValue(MeetingDetail.class);

                    DetailRoomBooking detailRoomBooking =
                            new DetailRoomBooking(meetingDetail.getDate(),meetingDetail.getStarttime(),meetingDetail.getEndtime());

                    if (Common.hashMapCommon.containsKey(meetingDetail.getRoombooking().getAddress()))
                    {
                        detailRoomBookings = Common.hashMapCommon.get(meetingDetail.getRoombooking().getAddress());
                        detailRoomBookings.add(detailRoomBooking);
                    }
                    else
                    {
                        detailRoomBookings = new ArrayList<>();
                        Common.hashMapCommon.put(meetingDetail.getRoombooking().getAddress(),detailRoomBookings);
                    }

                    Common.listAllMeetingDetail.add(meetingDetail);
                }
                if (alertDialog.isShowing()) alertDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Common.ShowMessageError(MainActivity.this , "MainActivity : "+error.getMessage(), Toasty.LENGTH_LONG);
                if (alertDialog.isShowing()) alertDialog.dismiss();
            }
        });
    }

    //endregion

    //region get all information user, room, attachment files
    private void getAllUserFromServer() {
        mAllUser = FirebaseDatabase.getInstance().getReference().child(Common.USER);
        Common.listAllUsers = new ArrayList<>();
        mAllUser.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
             for (DataSnapshot dataSnapshot : snapshot.getChildren())
             {
                 User user = dataSnapshot.getValue(User.class);
                 Common.listAllUsers.add(user);
             }
               if (alertDialog.isShowing()) alertDialog.dismiss();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {
              Common.ShowMessageError(MainActivity.this , "MainActivity : "+error.getMessage(), Toasty.LENGTH_LONG);
               if (alertDialog.isShowing()) alertDialog.dismiss();
           }
       });
    }

    private void getAllRoomFromServer() {
        mAllRoom = FirebaseDatabase.getInstance().getReference().child(Common.ROOM);
        Common.listAllRooms = new ArrayList<>();
        mAllRoom.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Room room = dataSnapshot.getValue(Room.class);
                    Common.listAllRooms.add(room);
                }
                if (alertDialog.isShowing()) alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Common.ShowMessageError(MainActivity.this , "MainActivity : "+error.getMessage(), Toasty.LENGTH_LONG);
                if (alertDialog.isShowing()) alertDialog.dismiss();
            }
        });
    }

    private void getAlAttachmentFromServer() {
        mAllAttachments = FirebaseDatabase.getInstance().getReference().child(Common.Attachment);
        Common.listAllAtachments = new ArrayList<>();
        mAllAttachments.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    AttachmentFiles value = dataSnapshot.getValue(AttachmentFiles.class);
                    Common.listAllAtachments.add(value);
                }
                if (alertDialog.isShowing()) alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Common.ShowMessageError(MainActivity.this , "MainActivity : "+error.getMessage(), Toasty.LENGTH_LONG);
                if (alertDialog.isShowing()) alertDialog.dismiss();
            }
        });
    }
    //endregion

    //region get information room, user has booking room
    private void getInFormationRoomBooking() {
        mDatabaseBookingRoom = FirebaseDatabase.getInstance().getReference().child(Common.MEETINGDETAIL);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listMeeting = new ArrayList<>();
                for (DataSnapshot dataMeetingDetail : snapshot.getChildren()){
                    MeetingDetail meetingDetail = dataMeetingDetail.getValue(MeetingDetail.class);
                    for (User user : meetingDetail.getMemberjoin())
                    {
                        if (user.getId().toLowerCase().trim().equals(Common.currentUser))
                        {
                           meetingDetail.setKey(dataMeetingDetail.getKey());
                            m_user = user;
                            listMeeting.add(meetingDetail);
                        }
                    }
                }
                Collections.sort(listMeeting, new Comparator<MeetingDetail>() {
                    Date date1 = null;
                    Date date2 = null;
                    @Override
                    public int compare(MeetingDetail m1, MeetingDetail m2) {
                        try {
                            date1 = Common.formatDate.parse(m1.getDate());
                            date2 = Common.formatDate.parse(m2.getDate());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return date1.compareTo(date2);
                    }
                });

                pushAllNotify(listMeeting);

                txt_count_meeting.setText(Html.fromHtml(getString(R.string.danhsachcuochop," <b><u>"+listMeeting.size()+"</u></b>")));
                adapterHomeBooking = new AdapterHomeBooking(MainActivity.this, R.layout.custom_recycler_layout_detail_room, listMeeting);
                recycler_room_detail_home.setAdapter(adapterHomeBooking);
                if (listMeeting.size() > 2)
                {
                    txt_view_all.setVisibility(View.VISIBLE);
                }
                else if (listMeeting.size() == 0)
                {
                    img_no_meeting.setVisibility(View.VISIBLE);
                }
                adapterHomeBooking.notifyDataSetChanged();
                if (alertDialog.isShowing()) alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (alertDialog.isShowing()) alertDialog.dismiss();
            }
        };
        mDatabaseBookingRoom.addValueEventListener(listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getInFormationRoomBooking();
     //   Common.DeleteAllNotify(MainActivity.this);
        if (adapterHomeBooking != null)
        adapterHomeBooking.notifyDataSetChanged();
    }

    private void listenTimeChange() {
        br_listen_time_change = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if (action.equals(Intent.ACTION_TIME_CHANGED) || action.equals(Intent.ACTION_TIME_TICK)) {
                   Calendar now = Calendar.getInstance();
                   long dateNow = 0;
                    int hour = now.get(Calendar.HOUR_OF_DAY);
                    int minute = now.get(Calendar.MINUTE);
                    try {
                        dateNow = Common.formatterTimeHour.parse(hour+":"+minute).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                   String currentDate = Common.formatDate.format(now.getTime());
                   for (MeetingDetail meetingDetail : listMeeting)
                   {
                       if (meetingDetail.getDate().equals(currentDate))
                       {
                           try {
                               long startTime = Common.formatterTimeHour.parse(meetingDetail.getStarttime()).getTime();
                               long endTime = Common.formatterTimeHour.parse(meetingDetail.getEndtime()).getTime();
                               if (dateNow >= startTime && dateNow <= endTime)
                               {
                                   btn_checkin.setEnabled(true);
                                   btn_checkin.setBackground(getResources().getDrawable(R.drawable.custom_bg_item_checkin_enable));
                                   img_checkin.setImageResource(R.drawable.tick);
                                   meetingDetailCurrent = meetingDetail;
                                   return;
                               }
                               else
                               {
                                   btn_checkin.setEnabled(false);
                                   btn_checkin.setBackground(getResources().getDrawable(R.drawable.custom_bg_item_checkin));
                                   img_checkin.setImageResource(R.drawable.enter_100);

                                   btn_checkout.setBackground(getResources().getDrawable(R.drawable.custom_bg_item_checkin));
                                   img_checkin.setImageResource(R.drawable.logout_100);
                                   btn_checkout.setEnabled(false);
                               }
                           } catch (ParseException e) {
                               e.printStackTrace();
                           }
                       }
                   }
                }
            }
        };
    }
    //endregion

    //region init view
    private void initView() {
        toolbarMain = findViewById(R.id.toolBarMain);
        setSupportActionBar(toolbarMain);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        img_avatar = findViewById(R.id.img_avatar);
        txt_account = findViewById(R.id.txt_account);
        gridLayout = findViewById(R.id.grid_layout);
        txt_role = findViewById(R.id.txt_role);
        txt_view_all = findViewById(R.id.txt_view_all);
        img_no_meeting = findViewById(R.id.img_no_meeting);
        txt_count_meeting = findViewById(R.id.txt_count_meeting);
        linearLayout = findViewById(R.id.linearLayout);
        btn_dat_lich_hop = findViewById(R.id.btn_dat_lich_hop);
        btn_kiem_tra_phong = findViewById(R.id.btn_kiem_tra_lich_hop);
        btn_checkin = findViewById(R.id.btn_checkin_home);
        btn_checkout = findViewById(R.id.btn_checkout_home);
        img_checkin = findViewById(R.id.img_checkin);
        im_logout = findViewById(R.id.img_logout);

        btn_checkin.setEnabled(false);
        btn_checkout.setEnabled(false);
        btn_checkin.setBackground(getResources().getDrawable(R.drawable.custom_bg_item_checkin));
        btn_checkout.setBackground(getResources().getDrawable(R.drawable.custom_bg_item_checkin));
        img_checkin.setImageResource(R.drawable.enter_100);
        im_logout.setImageResource(R.drawable.logout_100);

        txt_view_all.setVisibility(View.GONE);
        img_no_meeting.setVisibility(View.GONE);

        // declare event click
        txt_view_all.setOnClickListener(this);
        btn_dat_lich_hop.setOnClickListener(this);
        btn_kiem_tra_phong.setOnClickListener(this);
        btn_checkin.setOnClickListener(this);
        btn_checkout.setOnClickListener(this);

        listMeeting = new ArrayList<>();
        recycler_room_detail_home = findViewById(R.id.recycler_view_home_booking);
        m_currentDate = Common.formatDate.format( new Date());

        alertDialog = new SpotsDialog.Builder().setContext(MainActivity.this).setCancelable(false).build();
        alertDialog.show();

        recycler_room_detail_home.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false));
    }
    //endregion

    //region get information login to update to toolbar
    private void getInFormationUserLogin() {
        Common.currentUser = SplashScreen.firebaseAuth.getCurrentUser().getEmail().substring(0,SplashScreen.firebaseAuth.getCurrentUser().getEmail().length()-15).toLowerCase().trim();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child(Common.USER);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataUser : snapshot.getChildren()){
                    User user = dataUser.getValue(User.class);
                   if (user.getId().toLowerCase().trim().equals(Common.currentUser))
                   {
                       txt_account.setText(user.getFullname());
                       Picasso.get().load(user.getAvatar()).into(img_avatar);
                       txt_role.setText(user.getRole());
                   }
                   if (alertDialog.isShowing()) alertDialog.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
               Common.ShowMessageError(MainActivity.this,"MainActivity "+"error: "+error.getMessage(), Toasty.LENGTH_LONG);
                if (alertDialog.isShowing()) alertDialog.dismiss();
            }
        };
        mDatabaseUser.addValueEventListener(listener);

    }
    //endregion

    //region create and declare action menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br_listen_time_change);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
            {
                SplashScreen.firebaseAuth.signOut();
                //remove value in share references
                sharedPreferences = getSharedPreferences(Common.KEY_CURRENT_USER, Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();

                Intent iLogin = new Intent(MainActivity.this, SplashScreen.class);
                iLogin.putExtra(Common.ACTION_SEND_DATA_LOGIN, Common.KEY_LOGIN);
                startActivity(iLogin);
                finish();
            }
            break;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    //endregion

    //region handle logic when click btn_dat_lich_hop
    private void showDialogBookingRoom() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_booking_room);
        dialog.setCancelable(false);
        final EditText edt_dialog_date = dialog.findViewById(R.id.dialog_edt_date_detail);
        final TextView txt_start_time = dialog.findViewById(R.id.dialog_time_detail_start);
        final TextView txt_end_time = dialog.findViewById(R.id.dialog_time_detail_end);
        EditText dialog_edt_title_detail = dialog.findViewById(R.id.dialog_edt_title_detail);
        Spinner spinner_address = dialog.findViewById(R.id.dialog_spinner_address_detail);
        TextView txt_capacity = dialog.findViewById(R.id.dialog_toi_da_nguoi);

        LinearLayout btn_huy_dang_ky = dialog.findViewById(R.id.dialog_btn_huy_dang_ky_lich);
        LinearLayout btn_xac_nhan = dialog.findViewById(R.id.dialog_btn_xac_nhan_lich);

        SetUpDefault(edt_dialog_date,txt_start_time,txt_end_time,dialog_edt_title_detail,txt_capacity,spinner_address);

        //region show datetime picker dialog
        edt_dialog_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpDate(edt_dialog_date);
            }
        });
        //endregion

        //region show time
        txt_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpTimeStart( txt_start_time);
            }
        });

        txt_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpTimeStop(txt_end_time, txt_start_time);
            }
        });
        //endregion

        //region handle event click btn_dat_lich
        btn_xac_nhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        UpdateMeetingDetailUser(room.getAddress(), dialog);
                    }
                },1200);

            }
        });
        //endregion

        //region handle event btn_huy
        btn_huy_dang_ky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        //endregion
        dialog.show();
    }

    //region handle logic when click btn_dat_phong_hop
    private void UpdateMeetingDetailUser(String addressRoom, Dialog dialog) {
        try {
            if (alertDialog.isShowing()) alertDialog.dismiss();

            if (TextUtils.isEmpty(m_starttime) || TextUtils.isEmpty(m_endtime))
            {
                Toasty.warning(this,getString(R.string.chonthoigianbatdau),Toasty.LENGTH_SHORT).show();
                return;
            }
            if (Common.hashMapCommon.size() > 0)
            {
                boolean isCheck2nd = false;
                List<TimeDetail> listTimeDetail = new ArrayList<>();
                List<String> dateInRoomExist = new ArrayList<>();
                long startHourSelected  = new Time(Common.formatterTimeHour.parse(m_starttime).getTime()).getTime();

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
                if (dateInRoomExist.contains(m_dateSelected))
                {
                    for (int i = 0 ; i<  bookingList.size(); i++)
                    {
                        if (bookingList.get(i).getDateDetail().equals(m_dateSelected))
                        {
                            long lastTime = new Time(Common.formatterTimeHour.parse(bookingList.get(i).getEndTime()).getTime()).getTime();
                            if (startHourSelected > lastTime && !isCheck2nd )
                            {
                                isFirstCorrect = true;
                                CheckOvertimeBooking(new ArrayList<TimeDetail>(), dialog);
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
                    CheckOvertimeBooking(listTimeDetail, dialog);
                }
                else {
                    isFirstCorrect = true;
                    CheckOvertimeBooking(new ArrayList<TimeDetail>(), dialog);
                }
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(this, ex.getMessage()+"", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("RestrictedApi")
    private void CheckOvertimeBooking(List<TimeDetail> listTimeDetail, Dialog dialog ) {
        String uri;
        //Check null here
        if ( TextUtils.isEmpty(m_starttime) || TextUtils.isEmpty(m_title)
                 || TextUtils.isEmpty(m_dateSelected) || TextUtils.isEmpty(m_endtime) )
        {
            Common.ShowMessageError(this,"Không nên để trống các giá trị",Toasty.LENGTH_SHORT);
            return;
        }
        List<User> users = new ArrayList<>();
        users.add(m_user);
        List<AttachmentFiles> attachmentFiles = new ArrayList<>();

        if (isFirstCorrect)
        {
           uri =  Common.addToDeviceCalendar(MainActivity.this,m_dateSelected+" "+m_starttime, m_dateSelected+" "+m_endtime, m_title,
                   "họp đúng giờ",
                    new StringBuilder("Địa điểm họp:")
                            .append(room.getAddress()).toString()
            ).toString();
            NewDataUser newDataUser = new NewDataUser("", m_starttime, m_endtime, m_dateSelected,m_title,
                    m_user.getId(),"", users, room, attachmentFiles, uri);
            isFirstCorrect = false;
            isUpdateSuccess = true;
            mMeetingDetail.push().setValue(newDataUser);
            Common.ShowMessageSuccess(MainActivity.this,"Thêm mới thành công.",Toasty.LENGTH_SHORT);
            getInFormationRoomBooking();
            if (adapterHomeBooking != null)
                adapterHomeBooking.notifyDataSetChanged();
            dialog.dismiss();
            return;
        }
        if (listTimeDetail.size() > 0 )
        {
            try {
                if (isUpdateSuccess) return;
                TimeDetail firstItem = listTimeDetail.get(listTimeDetail.size() - 1 );
                long lastTimeBooked = new Time(Common.formatterTimeHour.parse(firstItem.getStartTimeDetail()).getTime()).getTime();
                long endHourSelected  = new Time(Common.formatterTimeHour.parse(m_endtime).getTime()).getTime();

                if ( endHourSelected > lastTimeBooked )
                {
                    Toasty.warning(this,getString(R.string.khunggiochuachinhxac),Toasty.LENGTH_LONG).show();
                    dialog.dismiss();
                }
                else
                {

                  uri =  Common.addToDeviceCalendar(MainActivity.this,m_dateSelected+" "+m_starttime, m_dateSelected+" "+m_endtime, m_title,
                            "họp đúng giờ",
                            new StringBuilder("Địa điểm họp:")
                                    .append(room.getAddress()).toString()
                    ).toString();
                    NewDataUser newDataUser = new NewDataUser("", m_starttime, m_endtime, m_dateSelected,m_title,
                            m_user.getId(),"", users, room, attachmentFiles, uri);
                    mMeetingDetail.push().setValue(newDataUser);
                    getInFormationRoomBooking();
                    if (adapterHomeBooking != null)
                        adapterHomeBooking.notifyDataSetChanged();
                    if (alertDialog.isShowing()) alertDialog.dismiss();
                    dialog.dismiss();
                }
                if (alertDialog.isShowing()) alertDialog.dismiss();

            }catch (Exception ex)
            {
                if (alertDialog.isShowing()) alertDialog.dismiss();
                Toast.makeText(this, ex.getMessage()+"", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }
        else {
            if (alertDialog.isShowing()) alertDialog.dismiss();
            if (isUpdateSuccess) return;
            Toasty.warning(this,getString(R.string.khunggiochuachinhxac),Toasty.LENGTH_LONG).show();
        }
    }
    //endregion

    private void SetUpDefault(EditText edt_dialog_date, TextView txt_start, TextView txt_end,EditText edt_title, final TextView txt_capacity, Spinner spinnerAddress) {
        edt_dialog_date.setText(m_currentDate);
        m_dateSelected = m_currentDate;
        m_title = edt_title.getText().toString();
        txt_start.setText("Giờ bắt đầu");
        txt_end.setText("giờ kết thúc");

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
        spinnerAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                room = Common.listAllRooms.get(pos);
                txt_capacity.setText("Chứa tối đa "+room.getCapacity()+" người");
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    //region get and set time
    private String setUpTimeStart(final TextView txt_start) {
        Calendar calendar = Calendar.getInstance();
        String[] startTimeHour = Common.formatterTimeHour.format(calendar.getTime()).split(":");
        String minStart = startTimeHour[1].trim();
        String hourStart = startTimeHour[0].trim();
        isChooseStartTime = false;
        TimePickerDialog timePickerDialog= new TimePickerDialog(this, R.style.DialogTheme_Time, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                if (i < 9 || i >18)
                {
                    Toasty.warning(MainActivity.this,getString(R.string.datlichgiohanhchinh),Toasty.LENGTH_SHORT).show();
                    m_starttime = null;
                    return ;
                }
                String  newHourStart= i+"";
                String newMinStart = i1+"";
                if (i1<10)
                {
                    newMinStart = "0"+newMinStart;
                }

                txt_start.setText(Html.fromHtml(" <b><u>"+newHourStart+":"+newMinStart+"</u></b>"));
                m_starttime = newHourStart+":"+newMinStart;
                isChooseStartTime = true;
            }
        },Integer.valueOf(hourStart),Integer.valueOf(minStart), true);

        timePickerDialog.setCanceledOnTouchOutside(false);
        timePickerDialog.show();
        return  txt_start.getText().toString().trim();
    }

    private String setUpTimeStop(final TextView txt_end_time, final  TextView txt_time_start) {
        if (!isChooseStartTime)
        {
            Toasty.warning(MainActivity.this,getString(R.string.chonthoigianbatdau),Toasty.LENGTH_SHORT).show();
            m_starttime = null;
            return txt_end_time.getText().toString();
        }
        Calendar calendar = Calendar.getInstance();
        String[] endTimeHour = Common.formatterTimeHour.format(calendar.getTime()).split(":");
        String minEnd = endTimeHour[1].trim();
        String hourEnd = endTimeHour[0].trim();
        TimePickerDialog timePickerDialog= new TimePickerDialog(this, R.style.DialogTheme_Time, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                try {
                    if (i < 9 || i >18)
                    {
                        Toasty.warning(MainActivity.this,R.string.giohanhchinh,Toasty.LENGTH_SHORT).show();
                        return;
                    }
                    String  newHour= i+"";
                    String newMin = i1+"";
                    long startTime = new Time(Common.formatterTimeHour.parse(txt_time_start.getText().toString()).getTime()).getTime();
                    long endTime = new Time(Common.formatterTimeHour.parse(i+":"+i1).getTime()).getTime();
                    if (startTime > endTime)
                    {
                        Toasty.warning(MainActivity.this,R.string.thoigianketthucnhohon,Toasty.LENGTH_SHORT).show();
                        m_endtime = null;
                    }
                    if (i1<10)
                    {
                        newMin = "0"+newMin;
                    }

                    txt_end_time.setText(Html.fromHtml(" <b><u>"+newHour+":"+newMin+"</u></b>"));
                    m_endtime = newHour+":"+newMin;
                    isChooseStartTime = true;
                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, e.getMessage()+"", Toast.LENGTH_SHORT).show();
                }

            }
        },Integer.valueOf(hourEnd),Integer.valueOf(minEnd), true);
        timePickerDialog.setCanceledOnTouchOutside(false);
        timePickerDialog.show();
        return txt_end_time.getText().toString().trim();
    }
    //endregion

    //region get and set date
    private void setUpDate(final EditText edt_date_detail) {
        // get date
        final Calendar calendar = Calendar.getInstance();
        String dateString = Common.formatDate.format(new Date());
        Date currentDate = new Date();
        final long currentDateMilis = currentDate.getTime();
        final String[] dayOfWeek = new String[1];
        try {
            if (!isChooseDate) {
                Date date = Common.formatDate.parse(dateString);
                day = Common.convertDateToString("dd", date);
                monthNumber = Common.convertDateToString("MM", date);
                year = Common.convertDateToString("yyyy", date);
            }
            new DatePickerDialog(this, R.style.my_dialog_theme, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int years, int monthOfYear, int dayOfMonth) {
                    calendar.set(years, monthOfYear, dayOfMonth);
                    Long dateSelectedMilis = calendar.getTimeInMillis();
                    if (currentDateMilis > dateSelectedMilis) {
                        Toasty.warning(MainActivity.this, R.string.ngaydatlichkhongdung, Toasty.LENGTH_SHORT).show();
                        m_dateSelected = "";
                        return;
                    }
                    m_dateSelected = Common.formatDate.format(calendar.getTime());

                    day = dayOfMonth;
                    monthNumber = monthOfYear + 1;
                    year = years;
                    try {
                        dayOfWeek[0] = Common.dayOfWeekFormat.format(Common.formatDate.parse(m_dateSelected));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    edt_date_detail.setText(dayOfWeek[0] + " " + m_dateSelected);

                    isChooseDate = true;
                }
            }, year, monthNumber, day).show();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //endregion

    //endregion

    //region save user when login success
    public void doSaveUser()  {
        sharedPreferences= this.getSharedPreferences(Common.KEY_CURRENT_USER, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(Common.Email, SplashScreen.firebaseAuth.getCurrentUser().getEmail());

        // Save.
        editor.commit();
    }

    //endregion

    //region Show dialog bottom to update information user
    private void ShowDialogBottomSearch() {
        final Room[] room = {new Room()};
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setTitle("Chọn phòng cần kiểm tra");
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        View sheetView = getLayoutInflater().inflate(R.layout.dialog_seach_room, null);
        LinearLayout btn_search = sheetView.findViewById(R.id.btn_search_room);
        Spinner spinner_floor = sheetView.findViewById(R.id.spinner_floor_bottom);
        final TextView txt_detail_count = sheetView.findViewById(R.id.txt_toi_da_nguoi_bottom);

        btn_search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ViewInformationRoom.class);
                intent.putExtra( Common.KEY_ROOM, room[0]);
                startActivity(intent);
                finish();
            }
        });

        List<String> stringList = new ArrayList<>();

        Collections.sort(Common.listAllRooms, new Comparator<Room>() {
            @Override
            public int compare(Room room, Room t1) {
                return room.getName().compareTo(t1.getName());
            }
        });

        for (Room r : Common.listAllRooms)
        {
            stringList.add(r.getAddress());
        }

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter adapterSpinner = new ArrayAdapter(this,android.R.layout.simple_spinner_item, stringList);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner_floor.setAdapter(adapterSpinner);
        spinner_floor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                room[0] = Common.listAllRooms.get(pos);
                txt_detail_count.setText("Chứa tối đa "+ room[0].getCapacity()+" người");
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }
    //endregion

    //region handle logic click view
    @Override
    public void onClick(View view) {
      switch (view.getId()){
          case R.id.txt_view_all :
          {
              if (!adapterHomeBooking.isAllShow)
              {
                  ((LinearLayout.LayoutParams) linearLayout.getLayoutParams()).weight = 2f;;
                  gridLayout.setVisibility(View.GONE);
                  txt_view_all.setText(Html.fromHtml("<b><u><i>"+getString(R.string.rutgon)+"</i></u></b>"));
                  adapterHomeBooking.isAllShow = true;
                  adapterHomeBooking.notifyDataSetChanged();
              }
              else
              {
                  ((LinearLayout.LayoutParams) linearLayout.getLayoutParams()).weight = 1f;;
                  gridLayout.setVisibility(View.VISIBLE);
                  txt_view_all.setText(Html.fromHtml("<b><u><i>"+getString(R.string.xemtatca)+"</i></u></b>"));
                  adapterHomeBooking.isAllShow = false;
                  adapterHomeBooking.notifyDataSetChanged();
              }
          }
          break;

          case R.id.btn_dat_lich_hop :
          {
              showDialogBookingRoom();
          }
          break;
          case R.id.btn_kiem_tra_lich_hop :
          {
              ShowDialogBottomSearch();
          }
          break;
          case R.id.btn_checkin_home:
          {
              String nameRoom = "";
              if (meetingDetailCurrent != null) {
                  nameRoom = meetingDetailCurrent.getRoombooking().getAddress();
              }
              Common.ShowMessageSuccess(this,"Bạn đã vào phòng "+'"'+nameRoom+'"',Toasty.LENGTH_LONG);
              btn_checkout.setEnabled(true);
              btn_checkout.setBackground(getResources().getDrawable(R.drawable.custom_bg_checkout_enable));
              im_logout.setImageResource(R.drawable.logout_100);

          }
          break;
          case R.id.btn_checkout_home:
          {
              androidx.appcompat.app.AlertDialog.Builder confirmDialog = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogCustom)
                      .setCancelable(false)
                      .setMessage(getString(R.string.banmuonroikhoiphong))
                      .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int i) {
                              dialogInterface.dismiss();
                          }
                      }).setPositiveButton("Tôi muốn.", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int i) {
                              alertDialog.show();
                              new Handler().postDelayed(new Runnable() {
                                  @Override
                                  public void run() {
                                      if (alertDialog.isShowing()) alertDialog.dismiss();

                                      btn_checkout.setBackground(getResources().getDrawable(R.drawable.custom_bg_item_checkin));
                                      img_checkin.setImageResource(R.drawable.logout_100);
                                      mMeetingDetail.child(meetingDetailCurrent.getKey()).removeValue();
                                      btn_checkout.setEnabled(false);

                                      btn_checkin.setBackground(getResources().getDrawable(R.drawable.custom_bg_item_checkin));
                                      img_checkin.setImageResource(R.drawable.enter_100);
                                      btn_checkin.setEnabled(false);
                                  }
                              },1000);

                          }
                      });
              confirmDialog.show();
          }
          break;

          default: break;
      }
    }
    //endregion
}