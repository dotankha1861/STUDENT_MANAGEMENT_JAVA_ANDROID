package com.example.studentmanagement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.statistic.ViewStatisticActivity;
import com.example.studentmanagement.models.view.CreditClassItem;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SuppressLint("SetTextI18n")
public class CreditClassForStatisticAdapter extends ArrayAdapter implements Filterable {
    Context context;
    int resource;
    private ArrayList<CreditClassItem> data_org;
    private ArrayList<CreditClassItem> data_view;
    private String searchQuery;

    String lecturerName;

    public CreditClassForStatisticAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.data_org = new ArrayList<>();
        this.data_view = new ArrayList<>();
        this.searchQuery = "";
        this.lecturerName = null;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void setLecturerName(String lecturerName) {
        this.lecturerName = lecturerName;
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
        data_org.add(index, (CreditClassItem) object);
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return data_org.indexOf(item);
    }

    public void setItem(@Nullable CreditClassItem object, int index){
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
                List<CreditClassItem> filteredList;

                String search_constraint = constraint.toString();
                if (search_constraint.isEmpty()) {
                    filteredList = new ArrayList<>(data_org);
                } else {
                    filteredList = data_org.stream().filter((creditClass) ->
                            creditClass.toString().toLowerCase().contains(constraint.toString().toLowerCase())).collect(Collectors.toList());
                }
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data_view = (ArrayList<CreditClassItem>) results.values;
                CreditClassForStatisticAdapter.super.notifyDataSetChanged();
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

        CreditClassItem creditClassItem = data_view.get(position);
        viewHolder.tvMaLTC.setText("Mã LTC: " + creditClassItem.getMaLopTc());
        viewHolder.tvTenHP.setText("Tên HP: " + creditClassItem.getTenMh());
        viewHolder.tvTenGV.setText(lecturerName!=null?lecturerName:"Tên GV: " + creditClassItem.getTenGv());

        viewHolder.lnlItem.setOnClickListener(view -> {
            Intent intent = new Intent(context, ViewStatisticActivity.class);
            intent.putExtra("creditClass", creditClassItem);
            context.startActivity(intent);
        });
        return convertView;
    }
    //
    private class ViewHolder {
        TextView tvMaLTC;
        TextView tvTenGV;
        TextView tvTenHP;
        LinearLayout lnlItem;
        public ViewHolder (View view){
            this.tvMaLTC = view.findViewById(R.id.tvMaLTC);
            this.tvTenHP = view.findViewById(R.id.tvTenHP);
            this.tvTenGV = view.findViewById(R.id.tvTenGV);
            this.lnlItem = view.findViewById(R.id.lnlItem);
        }
    }
}
