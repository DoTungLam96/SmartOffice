package com.example.smartoffice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartoffice.R;
import com.example.smartoffice.interfaces.IRemoveItem;
import com.example.smartoffice.model.User;

import java.util.ArrayList;
import java.util.List;

public class AdapterEditMemberJoin extends RecyclerView.Adapter<AdapterEditMemberJoin.ViewHolder> {

    @Override
    public void onBindViewHolder(@NonNull AdapterEditMemberJoin.ViewHolder holder, final int position) {
        holder.txt_delete_attachmet.setText(listUser.get(position).getId());

        holder.img_delete_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iRemoveItem.RemoveItemInListUser(position);
            }
        });
    }
    private List<User> listUser;
    private int layout;
    private Context context;
    private IRemoveItem iRemoveItem;
    public AdapterEditMemberJoin(List<User> stringList, int layout, Context context, IRemoveItem iRemoveItem) {
        this.listUser = stringList;
        this.layout = layout;
        this.context = context;
        this.iRemoveItem = iRemoveItem;
    }

    @NonNull
    @Override
    public AdapterEditMemberJoin.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layout, parent, false);
        AdapterEditMemberJoin.ViewHolder viewHolder = new AdapterEditMemberJoin.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_delete_attachmet;
        private ImageView img_delete_attachment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_delete_attachmet = itemView.findViewById(R.id.txt_edit_attachment);
            img_delete_attachment = itemView.findViewById(R.id.img_delete_attachment_edit);
        }
    }
}