package com.example.studentmanagement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.studentmanagement.R;
import com.example.studentmanagement.models.responsebody.ScoreStudent;

import java.util.ArrayList;
import java.util.Collection;

@SuppressLint("SetTextI18n")
public class ScoreStudentAdapter extends ArrayAdapter implements Filterable {
    Context context;
    int resource;
    ArrayList<ScoreStudent> data_view;

    public ScoreStudentAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.data_view = new ArrayList<>();
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

        ScoreStudent scoreStudent = data_view.get(position);
        viewHolder.tvMaMH.setText(scoreStudent.getMaMh());
        viewHolder.tvTenMH.setText(scoreStudent.getTenMh());
        viewHolder.tvCC.setText(String.valueOf(scoreStudent.getCc()));
        viewHolder.tvGK.setText(String.valueOf(scoreStudent.getGk()));
        viewHolder.tvCK.setText(String.valueOf(scoreStudent.getCk()));
        viewHolder.tvPerCC.setText("(" + scoreStudent.getPercentCc() + ")");
        viewHolder.tvPerGk.setText("(" + scoreStudent.getPercentGk()+ ")");
        viewHolder.tvPerCK.setText("(" + scoreStudent.getPercentCk()+ ")");
        viewHolder.tvTB.setText(String.valueOf(scoreStudent.getTb()));
        viewHolder.tvXL.setText(String.valueOf(scoreStudent.getXepLoai()));
        
        return convertView;
    }


    private class ViewHolder {
        TextView tvMaMH;
        TextView tvTenMH;
        TextView tvCC;
        TextView tvGK;
        TextView tvCK;
        TextView tvTB;
        TextView tvXL;
        TextView tvPerCC;
        TextView tvPerGk;
        TextView tvPerCK;

        public ViewHolder(View view) {
            this.tvMaMH= view.findViewById(R.id.tvMaSV);
            this.tvTenMH = view.findViewById(R.id.tvHoTen);
            this.tvCC = view.findViewById(R.id.tvCC);
            this.tvGK = view.findViewById(R.id.tvGK);
            this.tvCK = view.findViewById(R.id.tvCK);
            this.tvTB = view.findViewById(R.id.tvTB);
            this.tvXL = view.findViewById(R.id.tvXL);
            this.tvPerCC = view.findViewById(R.id.tvperCC);
            this.tvPerGk = view.findViewById(R.id.tvperGK);
            this.tvPerCK = view.findViewById(R.id.tvperCK);
        }
    }
}
