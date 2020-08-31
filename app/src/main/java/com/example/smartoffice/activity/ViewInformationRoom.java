package com.example.smartoffice.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.smartoffice.R;
import com.example.smartoffice.common.Common;
import com.example.smartoffice.customview.CourseEvent;
import com.example.smartoffice.customview.CustomViewPager;
import com.example.smartoffice.customview.Helper;
import com.example.smartoffice.customview.WeekPageAdapter;
import com.example.smartoffice.model.MeetingDetail;
import com.example.smartoffice.model.Room;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class ViewInformationRoom extends AppCompatActivity implements WeekPageAdapter.onClickView {

    private CustomViewPager viewPager;
    private List<CourseEvent>courseEventList;
    private HorizontalCalendarView calendarView;
    private Room room;
    private ImageView imgBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_information_room);

        init();
        setValueForChart(Calendar.getInstance());
    }

    private void setValueForChart(Calendar currentTime) {
        String startDate = "";
        String endDate = "";
        String curentDateString = Common.formatDate.format(currentTime.getTime());
        for (MeetingDetail meetingDetail : Common.listAllMeetingDetail)
        {
            if (meetingDetail.getDate().equals(curentDateString)
                    && meetingDetail.getRoombooking().getAddress().equals(room.getAddress()))
            {
                try
                {
                    startDate = meetingDetail.getStarttime();
                    endDate = meetingDetail.getEndtime();
                    courseEventList.add(new CourseEvent(Common.cutStringIfSoLong(40,meetingDetail.getTitle()),
                            Common.formatterTimeHour.parse(startDate),
                            Common.formatterTimeHour.parse(endDate),
                            meetingDetail.getRoombooking().getAddress(),
                            currentTime.getTime()
                    ));
                }
                catch (Exception e)
                {
                    Toast.makeText(this, "error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        initAdapter(courseEventList);
        moveToThisWeek();
    }

    private void init() {
        viewPager = findViewById(R.id.view_pager_time);
        calendarView = findViewById(R.id.calenderView);
        imgBack = findViewById(R.id.img_back);
        viewPager.setPagingEnabled(false);

        Intent intent = getIntent();
        room = intent.getParcelableExtra(Common.KEY_ROOM);
        initCalendarView();
        courseEventList = new ArrayList<>();
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewInformationRoom.this, MainActivity.class));
                finish();
            }
        });

    }

    private void initCalendarView() {
        //Calendar
        Calendar startCalendar = Calendar.getInstance();

        startCalendar.add(Calendar.DATE , 0);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.DATE , 4);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calenderView)
                .range(startCalendar, endCalendar)
                .datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS)
                .defaultSelectedDate(startCalendar)
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
              courseEventList.clear();
              setValueForChart(date);
            }
        });
    }

    private void moveToThisWeek() {
        Calendar calendar = Calendar.getInstance();
        int thisDay = calendar.get(Calendar.DAY_OF_MONTH);
        int position = (int) (Helper.shifMonth() + thisDay) / 7;
        viewPager.setCurrentItem(position);
    }
    private void initAdapter(List<CourseEvent> list) {
        WeekPageAdapter adapter = new WeekPageAdapter(getSupportFragmentManager(), list, this, ViewInformationRoom.this);
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void clickView(int poss) {

    }
}
