package com.example.studentmanagement.activities.course;

import android.os.Bundle;

import android.widget.TextView;


import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.models.entity.Course;

public class InforCourseActivity extends CustomAppCompactActivity {

    Toolbar toolbar;
    TextView tvMaHP, tvTenHP, tvMaKhoa, tvSoTc, tvSoTietLT, tvSoTietTH, tvCC, tvGK, tvCK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_infor_course_view);
        setControl();
        setEvent();
    }

    private void setEvent() {
        setSupportActionBar(toolbar);
        getInforFaculty();
    }

    private void getInforFaculty() {
        Course course = (Course) getIntent().getSerializableExtra("course");
        tvMaHP.setText(course.getMaMh());
        tvTenHP.setText(course.getTenMh());
        tvMaKhoa.setText(course.getMaKhoa());
        tvSoTc.setText(String.valueOf(course.getSoTc()));
        tvSoTietLT.setText(String.valueOf(course.getSoTietLt()));
        tvSoTietTH.setText(String.valueOf(course.getSoTietTh()));
        tvCC.setText(String.valueOf(course.getPercentCc()));
        tvGK.setText(String.valueOf(course.getPercentGk()));
        tvCK.setText(String.valueOf(course.getPercentCk()));
    }
    private void setControl() {
        toolbar = findViewById(R.id.toolbar);
        tvMaHP = findViewById(R.id.tvMaHP);
        tvTenHP = findViewById(R.id.tvTenHP);
        tvMaKhoa = findViewById(R.id.tvMaKhoa);
        tvSoTc =findViewById(R.id.tvSoTC);
        tvSoTietLT = findViewById(R.id.tvSoTietLT);
        tvSoTietTH = findViewById(R.id.tvSoTietTH);
        tvCC = findViewById(R.id.tvCC);
        tvGK = findViewById(R.id.tvGK);
        tvCK = findViewById(R.id.tvCK);
    }
}