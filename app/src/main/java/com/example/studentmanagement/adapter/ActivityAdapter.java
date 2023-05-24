package com.example.studentmanagement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.models.view.ActivityItem;

import java.util.ArrayList;

@SuppressLint("SetTextI18n")
public class ActivityAdapter extends ArrayAdapter implements Filterable {
    Context context;
    int resource;
    ArrayList<ActivityItem> data_view;

    public ActivityAdapter(Context context, int resource, ArrayList<ActivityItem> data) {
        super(context, resource, data);
        this.context = context;
        this.resource = resource;
        this.data_view = data;
    }

    @Override
    public int getCount() {
        return data_view.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else viewHolder = (ViewHolder) convertView.getTag();

        ActivityItem activityItem = data_view.get(position);
        viewHolder.tvTenHP.setText(activityItem.getTenMh());
        viewHolder.tvTietBD.setText("Tiết bắt đầu: " + activityItem.getTiet());
        viewHolder.tvPhong.setText("Phòng: " + activityItem.getPhong());

        return convertView;
    }
    private static class ViewHolder {
        TextView tvTenHP;
        TextView tvTietBD;
        TextView tvPhong;
        public ViewHolder (View view){
            this.tvTenHP = view.findViewById(R.id.tvTenHP);
            this.tvTietBD= view.findViewById(R.id.tvTietBD);
            this.tvPhong = view.findViewById(R.id.tvPhong);
        }
    }
}
