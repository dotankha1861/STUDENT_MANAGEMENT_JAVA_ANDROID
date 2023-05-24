package com.example.studentmanagement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.studentmanagement.R;
import com.example.studentmanagement.models.view.TimeTableItem;

import java.util.ArrayList;
import java.util.Collection;

@SuppressLint("SetTextI18n")
public class TimeTableAdapter extends ArrayAdapter {
    Context context;
    int resource;
    private ArrayList<TimeTableItem> data_view;

    public TimeTableAdapter(Context context, int resource, ArrayList<TimeTableItem> data) {
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
    public void clear() {
        data_view.clear();
    }

    @Override
    public void addAll(@NonNull Collection collection) {
        data_view.addAll(collection);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else viewHolder = (ViewHolder) convertView.getTag();

        TimeTableItem timeTableItem = data_view.get(position);
        viewHolder.tvThu.setText(timeTableItem.getThu().equals("CN")?"Chủ nhật":"Thứ " + timeTableItem.getThu());
        viewHolder.tvNgay.setText(timeTableItem.getNgay());
        if(timeTableItem.getTkbDtoList().size()==0) {
            viewHolder.tvNotActivity.setText("Trống lịch");
            viewHolder.tvNotActivity.setVisibility(View.VISIBLE);
        }
        else viewHolder.tvNotActivity.setVisibility(View.GONE);
        viewHolder.lnlActivity.removeAllViews();

        timeTableItem.getTkbDtoList().forEach(item -> {
            @SuppressLint("InflateParams") View activity = LayoutInflater.from(context).inflate(R.layout.item_activity, null);
            ((TextView) activity.findViewById(R.id.tvTenHP)).setText(item.getTenMh());
            ((TextView) activity.findViewById(R.id.tvTietBD)).setText("Tiết bắt đầu: " + item.getTiet());
            ((TextView) activity.findViewById(R.id.tvPhong)).setText("Phòng: " + item.getPhong());
            viewHolder.lnlActivity.addView(activity);
        });
        return convertView;
    }

    private static class ViewHolder {
        TextView tvThu;
        TextView tvNgay;
        LinearLayout lnlActivity;
        TextView tvNotActivity;
        public ViewHolder(View view) {
            this.tvThu = view.findViewById(R.id.tvThu);
            this.tvNgay = view.findViewById(R.id.tvNgay);
            this.lnlActivity = view.findViewById(R.id.listActivities);
            this.tvNotActivity = view.findViewById(R.id.tvNotActivity);
        }
    }
}
