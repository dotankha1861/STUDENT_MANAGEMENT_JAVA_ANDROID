package com.example.studentmanagement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.notification.ViewNotificationActivity;
import com.example.studentmanagement.activities.score.ViewScoreCreditClassActivity;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.firebase.NotificationData;
import com.example.studentmanagement.models.entity.Course;
import com.example.studentmanagement.models.responsebody.ScoreCreditClass;
import com.example.studentmanagement.models.view.CreditClassItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.FormatterDate;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("SetTextI18n")
public class NotificationForCreditClassAdapter extends ArrayAdapter implements Filterable {
    Context context;
    int resource;
    private ArrayList<NotificationData> data_org;
    private ArrayList<NotificationData> data_view;
    private String searchQuery;
    String lecturerName;

    public void setLecturerName(String lecturerName) {
        this.lecturerName = lecturerName;
    }


    public NotificationForCreditClassAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.data_org = new ArrayList<>();
        this.data_view = new ArrayList<>();
        this.searchQuery = "";
        lecturerName = null;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }


    @Override
    public int getCount() {
        return data_view.size();
    }

    @Override
    public void addAll(@NonNull Collection collection) {
        data_org.addAll(collection);
    }

    @Override
    public void insert(@Nullable Object object, int index) {
        data_org.add(index, (NotificationData) object);
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return data_org.indexOf(item);
    }

    public void setItem(@Nullable NotificationData object, int index){
        data_org.set(index, object);
    }

    @Override
    public void remove(@Nullable Object object) {
        data_org.remove(object);
    }

    @Override
    public void clear() {
        data_org.clear();
    }

    @Override
    public void notifyDataSetChanged() {
        getFilter().filter(searchQuery);
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<NotificationData> filteredList;

                String search_constraint = constraint.toString();
                if (search_constraint.isEmpty()) {
                    filteredList = new ArrayList<>(data_org);
                } else {
                    filteredList = data_org.stream().filter((creditClass) ->
                            creditClass.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())).collect(Collectors.toList());
                }
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data_view = (ArrayList<NotificationData>) results.values;
                NotificationForCreditClassAdapter.super.notifyDataSetChanged();
            }
        };
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else viewHolder = (ViewHolder) convertView.getTag();

        NotificationData notificationData = data_view.get(position);
        viewHolder.tvThoiGian.setText(FormatterDate.convertDate2String(new Date(notificationData.getTimeStamp()), FormatterDate.TIME_DATE));
        viewHolder.tvTitle.setText(notificationData.getTitle());
        viewHolder.tvContent.setText(notificationData.getBody());
        return convertView;
    }

    private static class ViewHolder {
        TextView tvThoiGian;
        TextView tvTitle;
        TextView tvContent;
        public ViewHolder (View view){
            this.tvThoiGian = view.findViewById(R.id.tvThoiGian);
            this.tvTitle = view.findViewById(R.id.tvTitle);
            this.tvContent = view.findViewById(R.id.tvContent);
        }
    }
}
