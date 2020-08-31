package com.example.smartoffice.customview;

import android.content.Context;
import android.util.TypedValue;

import java.util.Calendar;

public class Helper {

    public static int shifMonth() {
        Calendar firstDayOfMonth = Calendar.getInstance();
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        int dayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek - 1; // SUNDAY = 1..SATURDAY = 7
    }

    public static int numberOfWeeksWithShift() {
        Calendar calendar = Calendar.getInstance();
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return (int) Math.ceil((double) (Helper.shifMonth() + daysInMonth) / 7);
    }

    public static int dipValue(Context context, int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }
}
