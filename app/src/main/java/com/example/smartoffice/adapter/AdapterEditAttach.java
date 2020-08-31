package com.example.smartoffice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartoffice.R;
import com.example.smartoffice.common.Common;
import com.example.smartoffice.interfaces.IRemoveItem;
import com.example.smartoffice.model.AttachmentFiles;

import java.util.List;

public class AdapterEditAttach extends RecyclerView.Adapter<AdapterEditAttach.ViewHolder> {

    private List<AttachmentFiles> attachmentFiles;
    private int layout;
    private Context context;
    private IRemoveItem iRemoveItem;
    public AdapterEditAttach(List<AttachmentFiles> stringList, int layout, Context context, IRemoveItem iRemoveItem) {
        this.attachmentFiles = stringList;
        this.layout = layout;
        this.context = context;
        this.iRemoveItem = iRemoveItem;
    }

    public AdapterEditAttach() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layout, parent, false);
        AdapterEditAttach.ViewHolder viewHolder = new AdapterEditAttach.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.txt_delete_attachmet.setText(Common.cutStringIfSoLong(20,attachmentFiles.get(position).getName()));

        holder.img_delete_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iRemoveItem.RemoveItemInListAttachment(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return attachmentFiles.size();
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
