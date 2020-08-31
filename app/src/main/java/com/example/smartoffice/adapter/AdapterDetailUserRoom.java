package com.example.smartoffice.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartoffice.R;
import com.example.smartoffice.activity.InformationDetailMeeting;
import com.example.smartoffice.common.Common;
import com.example.smartoffice.model.MeetingDetail;
import com.example.smartoffice.model.User;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class AdapterDetailUserRoom extends RecyclerView.Adapter<AdapterDetailUserRoom.ViewHolder> {
    private Context context;
    private int layout;
    private List<User> userList;

    public AdapterDetailUserRoom(Context context, int layout, List<User> userList) {
        this.context = context;
        this.layout = layout;
        this.userList = userList;
    }

    @NonNull
    @Override
    public AdapterDetailUserRoom.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layout, parent, false);
        AdapterDetailUserRoom.ViewHolder viewHolder = new AdapterDetailUserRoom.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Picasso.get().load(userList.get(position).getAvatar()).into(holder.img_avatar_detail);
        holder.txt_email_detail.setText(userList.get(position).getId()+Common.prefixEmail);
        holder.txt_name_detail.setText(userList.get(position).getFullname());
        holder.txt_phone_detail.setText(userList.get(position).getSdt());
        holder.txt_role_detail.setText(userList.get(position).getRole());

        holder.txt_phone_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+userList.get(position).getSdt()));
                    context.startActivity(callIntent);
                }
                catch (Exception ex){
                    Common.ShowMessageError(context, "Can't call beacause :" +ex.getMessage(), Toasty.LENGTH_LONG);
                }
            }
        });
    }


    @Override
    public int getItemCount() { return userList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_name_detail, txt_email_detail,txt_role_detail;
        private Button txt_phone_detail;
        private ImageView img_avatar_detail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name_detail = itemView.findViewById(R.id.txt_name_detail);
            txt_phone_detail = itemView.findViewById(R.id.txt_phone_detail);
            txt_role_detail = itemView.findViewById(R.id.txt_role_detail);
            txt_email_detail = itemView.findViewById(R.id.txt_email_detail);
            img_avatar_detail = itemView.findViewById(R.id.img_avatar_detail);
        }
    }
}
