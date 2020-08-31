package com.example.smartoffice.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartoffice.R;
import com.example.smartoffice.activity.InformationDetailMeeting;
import com.example.smartoffice.common.Common;
import com.example.smartoffice.model.MeetingDetail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterHomeBooking extends RecyclerView.Adapter<AdapterHomeBooking.ViewHolder> {
    private Context context;
    private int layout;
    private List<MeetingDetail> meetingDetailList;
    public  boolean isAllShow;

    public AdapterHomeBooking(Context context, int layout, List<MeetingDetail> meetingDetailList) {
        this.context = context;
        this.layout = layout;
        this.meetingDetailList = meetingDetailList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
         holder.txt_address_home.setText(Common.cutStringIfSoLong(25, meetingDetailList.get(position).getRoombooking().getAddress()));

         // get date
        final String dateString = meetingDetailList.get(position).getDate();
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE");
        String dayOfTheWeek = "";
        try {
            Date date = Common.formatDate.parse(dateString);
            dayOfTheWeek = dayOfWeekFormat.format(date);
            holder.txt_day_of_week.setText(dayOfTheWeek);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.txt_date_of_year.setText(dateString);

        holder.txt_title_meeting.setText(Common.cutStringIfSoLong(100,  meetingDetailList.get(position).getTitle()));
        holder.txt_time_home.setText("Từ "+meetingDetailList.get(position).getStarttime()+" đến "+meetingDetailList.get(position).getEndtime());
        holder.txt_count_member_home.setText(Common.cutStringIfSoLong(3, meetingDetailList.get(position).getMemberjoin().size()+""));

        final String finalDayOfTheWeek = dayOfTheWeek;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.dateSelected = finalDayOfTheWeek +" ngày "+dateString;
                Intent intent = new Intent(context, InformationDetailMeeting.class);
                intent.putExtra(Common.KEY_MEETING_ALL_ACTIVITY, (Parcelable) meetingDetailList.get(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (isAllShow)
        {
            return meetingDetailList.size();
        }
        else {
            if (meetingDetailList.size() >= 2)
            {
                return 2;
            }
            else
            {
                return meetingDetailList.size();
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_count_member_home, txt_address_home, txt_time_home,
                txt_title_meeting, txt_day_of_week, txt_date_of_year;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_address_home = itemView.findViewById(R.id.txt_address_home);
            txt_count_member_home = itemView.findViewById(R.id.txt_count_member_home);
            txt_time_home = itemView.findViewById(R.id.txt_time_home);
            txt_title_meeting = itemView.findViewById(R.id.txt_title_meeting);
            txt_day_of_week = itemView.findViewById(R.id.txt_dayOfWeek);
            txt_date_of_year = itemView.findViewById(R.id.txt_date_of_year);
        }
    }
}
