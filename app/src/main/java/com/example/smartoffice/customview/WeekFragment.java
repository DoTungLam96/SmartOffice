package com.example.smartoffice.customview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartoffice.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@SuppressLint("ValidFragment")
public class WeekFragment extends Fragment {

    private Date mStartDate;
    private List<CourseEvent> mItems;

    private int widthCell;
    private int hourWidth;
    private int hourHeight;
    private int hourHeightLine;

    private WeekPageAdapter.onClickView onClickView;
    private ScrollView scrollView;
    private LinearLayout weekDayHeader;
    private Activity activity;


    public WeekFragment() {
    }

    public WeekFragment(Date startDate, List<CourseEvent> items, WeekPageAdapter.onClickView onClickView, Activity activity) {
        mStartDate = startDate;
        this.onClickView = onClickView;
        this.activity = activity;

//        if (items == null)
//            mItems = new ArrayList<ListCourseEvent>();
//        else
//            mItems = items;
        this.mItems=items;


    }
    private static final float MIN_ZOOM = 1.0f;
    private static final float MAX_ZOOM = 4.0f;

    private float scale = 1.0f;
    private float lastScaleFactor = 0f;

    // Where the finger first  touches the screen
    private float startX = 0f;
    private float startY = 0f;

    // How much to translate the canvas
    private float dx = 0f;
    private float dy = 0f;
    private float prevDx = 0f;
    private float prevDy = 0f;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        widthCell = Helper.dipValue(getActivity().getBaseContext(), 48);
        hourWidth = Helper.dipValue(container.getContext(), Constants.HOUR_WIDTH);
        hourHeight = Helper.dipValue(container.getContext(), Constants.HOUR_HEIGHT);
        hourHeightLine = Helper.dipValue(container.getContext(), Constants.HOUR_HEIGHT_LINE);

        View weekdayView = inflater.inflate(R.layout.weekday, null);

        final LinearLayout weekdayHorizontalBar = (LinearLayout) weekdayView.findViewById(R.id.weekdayHorizontalBar);

        final ZoomableRelativeLayout zome = (ZoomableRelativeLayout) weekdayView.findViewById(R.id.zoom);
        zome.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                zome.init(getContext());
                return false;
            }
        });

        scrollView = weekdayView.findViewById(R.id.scroll_view);
        weekDayHeader = weekdayView.findViewById(R.id.weekdayHeader);

        LinearLayout linearRed = weekDayHeader.findViewById(R.id.linearRed);
        LinearLayout linearBlue = weekDayHeader.findViewById(R.id.linearBlue);

        View view = new View(linearRed.getContext());
        LinearLayout.LayoutParams layoutParamsView = new LinearLayout.LayoutParams(100,30);
        layoutParamsView.gravity = Gravity.CENTER;
        view.setLayoutParams(layoutParamsView);
        view.setBackgroundColor(getResources().getColor(R.color.colorPDF));
        linearRed.addView(view);

        TextView textView = new TextView(linearRed.getContext());
        textView.setText("Cuộc họp kết thúc");
        textView.setTextSize(10);
        LinearLayout.LayoutParams layoutParamTxt = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamTxt.setMargins(10,0,0,0);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setLayoutParams(layoutParamTxt);
        linearRed.addView(textView);

        View viewBlue = new View(linearBlue.getContext());
        LinearLayout.LayoutParams layoutParamsViewBlue = new LinearLayout.LayoutParams(100,30);
        layoutParamsViewBlue.gravity = Gravity.CENTER;
        viewBlue.setLayoutParams(layoutParamsViewBlue);
        viewBlue.setBackgroundColor(getResources().getColor(R.color.colorDocx));
        linearBlue.addView(viewBlue);

        TextView textViewBlue = new TextView(linearRed.getContext());
        textViewBlue.setText("Cuộc họp sắp tới");
        textViewBlue.setTextSize(10);
        LinearLayout.LayoutParams layoutParamTxtBlue = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamTxtBlue.setMargins(10,0,0,0);
        textViewBlue.setLayoutParams(layoutParamTxtBlue);
        textViewBlue.setTypeface(textView.getTypeface(), Typeface.BOLD);
        linearBlue.addView(textViewBlue);


        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(mStartDate);


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createHoursRuler(weekdayHorizontalBar);
            }
        });

        createWeekdayBar(container.getContext(), calendar.getTime(), weekdayHorizontalBar);

        return weekdayView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void createHoursRuler(LinearLayout weekdayHorizontalBar) {
        LinearLayout verticalHourCell = new LinearLayout(weekdayHorizontalBar.getContext());
        verticalHourCell.setBackgroundColor(Color.WHITE);
        verticalHourCell.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, hourHeightLine);
        LinearLayout.LayoutParams hourHeightLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, hourHeight);

        for (int i = Constants.INITIAL_HOUR; i <= Constants.END_HOUR; i++) {
            LinearLayout hourHeightLayout = new LinearLayout(weekdayHorizontalBar.getContext());
            hourHeightLayout.setBackgroundColor(getResources().getColor(R.color.bg_custom_view_color));
            TextView textView = new TextView(weekdayHorizontalBar.getContext());
            textView.setText(String.format("%02d:00", i));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            textView.setTextColor(getResources().getColor(R.color.colorDocx));
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
            hourHeightLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            hourHeightLayout.addView(textView, textViewParams);

            verticalHourCell.addView(hourHeightLayout, hourHeightLayoutParams);
        }
        weekdayHorizontalBar.addView(verticalHourCell, 0, new RelativeLayout.LayoutParams(hourWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void createWeekdayBar(Context context, Date date, LinearLayout weekdayHorizontalBar) {
        WeekdayVerticalBar verticalBar = new WeekdayVerticalBar(context, date, mItems, onClickView, scrollView, activity);
        verticalBar.setBackgroundColor(getResources().getColor(R.color.bg_custom_view_color));;
        weekdayHorizontalBar.addView(verticalBar, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private static boolean IsWeekend(Calendar calDate) {
        if (calDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
            return true;
        if (calDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            return true;
        return false;
    }

    private static boolean IsDifferentMonth(Calendar calDate) {
        Calendar today = Calendar.getInstance();
        if (calDate.get(Calendar.MONTH) != today.get(Calendar.MONTH))
            return true;
        return false;
    }

}
