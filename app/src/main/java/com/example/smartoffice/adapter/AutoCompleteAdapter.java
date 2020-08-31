package com.example.smartoffice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smartoffice.R;
import com.example.smartoffice.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<User> {
    private List<User> countryListFull;
    public AutoCompleteAdapter(@NonNull Context context, @NonNull List<User> countryList) {
        super(context, 0, countryList);
        countryListFull = new ArrayList<>(countryList);
    }
    @NonNull
    @Override
    public Filter getFilter() {
        return countryFilter;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.custom_layout_search_user, parent, false
            );
        }
        TextView txt_search_user = convertView.findViewById(R.id.txt_search_username);
        TextView txt_search_fullname = convertView.findViewById(R.id.txt_search_fullname);
        ImageView imageView = convertView.findViewById(R.id.img_search_avatar);
        User user = getItem(position);
        txt_search_user.setText("("+user.getId()+")");
        txt_search_fullname.setText(user.getFullname());
        Picasso.get().load(user.getAvatar()).into(imageView);

        return convertView;
    }
    private Filter countryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<User> suggestions = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(countryListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (User item : countryListFull) {
                    if (item.getId().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }
            results.values = suggestions;
            results.count = suggestions.size();
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((User) resultValue).getId();
        }
    };
}
