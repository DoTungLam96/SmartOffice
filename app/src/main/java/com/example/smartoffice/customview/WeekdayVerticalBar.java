package com.example.smartoffice.customview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.smartoffice.R;
import com.example.smartoffice.common.Common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class WeekdayVerticalBar extends RelativeLayout implements View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {

    private int HOUR_HEIGHT;
    private int HOUR_HEIGHT_LINE;

    private Date mDate, dateNow;
    private List<CourseEvent> mItems;

    WeekPageAdapter.onClickView onClickView;

    private ScrollView scrollView;

    private Activity activity;
    private SimpleDateFormat sdf;
    ScaleGestureDetector mScaleDetector = new ScaleGestureDetector(getContext(), this);

    public WeekdayVerticalBar(Context context, Date date, List<CourseEvent> items, WeekPageAdapter.onClickView onClickView, ScrollView scrollView, Activity activity) {
        super(context);
        mDate = date;
        this.onClickView = onClickView;
        this.scrollView = scrollView;
        this.activity = activity;
        this.mItems = items;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createDayLayout();
            }
        });

    }

    private void createDayLayout() {
        HOUR_HEIGHT = Helper.dipValue(getContext(), Constants.HOUR_HEIGHT);
        HOUR_HEIGHT_LINE = Helper.dipValue(getContext(), Constants.HOUR_HEIGHT_LINE);
        createHoursCell();
        createItems();
        moveScrollViewToCurrentTime();

    }

    public void moveScrollViewToCurrentTime() {
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, Math.round(calculateHeightCurrentTime()));
            }
        }, 200);
    }

    private void createHoursCell() {

        LinearLayout verticalHourCell = new LinearLayout(getContext());
        verticalHourCell.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams bg1Params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, HOUR_HEIGHT_LINE / 2 - 1);

        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);

        LinearLayout.LayoutParams bg1LayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams lineLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams hourHeightLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, HOUR_HEIGHT);
        for (int i = Constants.INITIAL_HOUR; i <= Constants.END_HOUR; i++) {

            LinearLayout bg1Layout = new LinearLayout(getContext());
            bg1Layout.setPadding(5, 0, 5, 0);
            View bg1 = new View(getContext());
            bg1Layout.addView(bg1, bg1Params);

            LinearLayout hourHeightLayout = new LinearLayout(getContext());
            hourHeightLayout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout lineLayout = new LinearLayout(getContext());
            View lineView = new View(getContext());
            lineView.setBackgroundColor(getResources().getColor(R.color.colorDividerCustomView));
            lineParams.setMargins(50, 0, 0, 0);
            lineLayout.addView(lineView, lineParams);

            hourHeightLayout.addView(bg1Layout, bg1LayoutParams);
            hourHeightLayout.addView(lineLayout, lineLayoutParams);
            verticalHourCell.addView(hourHeightLayout, hourHeightLayoutParams);

        }

        View viewOne = LayoutInflater.from(getContext()).inflate(R.layout.current_time_layout, this, false);
        LayoutParams layoutParamsOne = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParamsOne.setMargins(0, Math.round(calculateHeightCurrentTime()), 0, 0);
        addView(viewOne, layoutParamsOne);

        addView(verticalHourCell, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    private void createItems() {
        final SimpleDateFormat output = new SimpleDateFormat("HH'h'mm");
        for (final CourseEvent item : mItems) {
            View view = null;
            String dateItem = Common.formatDate.format(item.getCurrentDate());
            String currentDate = Common.formatDate.format(Calendar.getInstance().getTime());

            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);
            try {
                dateNow = Common.formatterTimeHour.parse(hour+":"+minute);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (item.getEndDatetime().before(dateNow) && dateItem.equals(currentDate)) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_current_time, this, false);
            } else {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_courses_envent_4, this, false);
            }

            RelativeLayout relativeLayout = view.findViewById(R.id.ln);

            TextView tv_name_courses = view.findViewById(R.id.tv_name_courses);
            TextView tv_time_courses = view.findViewById(R.id.tv_time_courses);
            TextView tv_class_name_courses = view.findViewById(R.id.tv_class_name_courses);

            tv_name_courses.setText(item.getNameEvent());
            tv_time_courses.setText("Từ "+output.format(item.getStartDatetime()) + " đến " + output.format(item.getEndDatetime()));
            tv_class_name_courses.setText("Địa điểm: "+item.getAddress());

            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, Math.round(calculateHeight(item)));
            relativeLayout.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
            layoutParams.setMargins(150, calculateRow(item), 40, 10);


            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView text = v.findViewById(R.id.tv_time_courses);

                    String s = output.format(item.getStartDatetime()) + "-" + output.format(item.getEndDatetime());

                    if (s.equals(text.getText())) {
                        CourseEvent itemcureent = item;
                        int poss = mItems.indexOf(itemcureent);

                        onClickView.clickView(poss);

                    }
                }
            });
            addView(view, layoutParams);
        }
    }

    private float calculateHeight(CourseEvent item) {

        float sizeMinutes = (float) HOUR_HEIGHT / 60;
        int minutes = 0;
        minutes = (int) (((item.getEndDatetime()).getTime() / 60000) - ((item.getStartDatetime()).getTime() / 60000));
        return minutes * sizeMinutes;
    }

    private float calculateHeightCurrentTime() {
        float sizeMinutes = (float) HOUR_HEIGHT / 60;

        Calendar startDateTime = Calendar.getInstance();

        startDateTime.set(Calendar.HOUR_OF_DAY, Constants.INITIAL_HOUR);
        startDateTime.set(Calendar.MINUTE, 0);
        Date currentTime = Calendar.getInstance().getTime();

        if (currentTime.getHours() < 8 || currentTime.getHours() > 18)
        {
            return 5;
        }
        int minutes = (int) ((currentTime.getTime() / 60000) - (startDateTime.getTime().getTime() / 60000));

        return minutes * sizeMinutes;
    }

    private int calculateRow(CourseEvent item) {
        Calendar calendar = GregorianCalendar.getInstance();

        calendar.setTime(item.getStartDatetime());

        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        int minutesOfHour = calendar.get(Calendar.MINUTE);

        Log.d("TAG", "calculateRow:=== " + ((hourOfDay - Constants.INITIAL_HOUR) * HOUR_HEIGHT) + (minutesOfHour * (HOUR_HEIGHT / 60) + HOUR_HEIGHT_LINE / 2));

        return ((hourOfDay - Constants.INITIAL_HOUR) * HOUR_HEIGHT) + (minutesOfHour * (HOUR_HEIGHT / 60) + HOUR_HEIGHT_LINE / 2);
    }
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mScaleDetector.onTouchEvent(event))
            return true;
        return super.onTouchEvent(event);

    }
}