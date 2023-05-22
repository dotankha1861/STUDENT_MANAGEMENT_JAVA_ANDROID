package com.example.studentmanagement.activities.faculty;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.models.entity.Faculty;

public class InforFacultyActivity extends CustomAppCompactActivity {
    Toolbar toolbar;
    TextView tvMaKhoa, tvTenKhoa, tvSDT, tvEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_infor_faculty_view);
        setControl();
        setEvent();

    }

    private void setEvent() {
        setSupportActionBar(toolbar);
        getInforKhoa();
    }

    private void getInforKhoa() {
        Faculty faculty = (Faculty) getIntent().getSerializableExtra("faculty");
        tvMaKhoa.setText(faculty.getMaKhoa());
        tvTenKhoa.setText(faculty.getTenKhoa());
        tvSDT.setText(faculty.getSdt());
        tvEmail.setText(faculty.getEmail());
    }

    private void setControl() {
        toolbar = findViewById(R.id.toolbar);
        tvMaKhoa = findViewById(R.id.tvMaKhoa);
        tvTenKhoa= findViewById(R.id.tvTenKhoa);
        tvSDT = findViewById(R.id.tvSDT);
        tvEmail =findViewById(R.id.tvEmail);
    }

}