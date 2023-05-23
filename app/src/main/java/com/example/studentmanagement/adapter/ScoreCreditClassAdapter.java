package com.example.studentmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.models.responsebody.ScoreCreditClass;

import java.util.ArrayList;

public class ScoreCreditClassAdapter extends ArrayAdapter implements Filterable {
    Context context;
    int resource;
    ArrayList<ScoreCreditClass> data_view;

    public ScoreCreditClassAdapter(Context context, int resource, ArrayList<ScoreCreditClass> data) {
        super(context, resource, data);
        this.context = context;
        this.resource = resource;
        this.data_view= data;
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

        ScoreCreditClass scoreCreditClass = data_view.get(position);
        viewHolder.tvMaSV.setText(scoreCreditClass.getMaSv());
        viewHolder.tvHoTen.setText(scoreCreditClass.getTenSv());
        viewHolder.tvCC.setText(String.valueOf(scoreCreditClass.getCc()));
        viewHolder.tvGK.setText(String.valueOf(scoreCreditClass.getGk()));
        viewHolder.tvCK.setText(String.valueOf(scoreCreditClass.getCk()));
        viewHolder.tvTB.setText(String.valueOf(scoreCreditClass.getTb()));
        viewHolder.tvXL.setText(String.valueOf(scoreCreditClass.getXepLoai()));

        return convertView;
    }

    private class ViewHolder {
        TextView tvMaSV;
        TextView tvHoTen;
        TextView tvCC;
        TextView tvGK;
        TextView tvCK;
        TextView tvTB;
        TextView tvXL;

        //
        public ViewHolder(View view) {
            this.tvMaSV = view.findViewById(R.id.tvMaSV);
            this.tvHoTen = view.findViewById(R.id.tvHoTen);
            this.tvCC = view.findViewById(R.id.tvCC);
            this.tvGK = view.findViewById(R.id.tvGK);
            this.tvCK = view.findViewById(R.id.tvCK);
            this.tvTB = view.findViewById(R.id.tvTB);
            this.tvXL = view.findViewById(R.id.tvXL);
        }
    }
}
