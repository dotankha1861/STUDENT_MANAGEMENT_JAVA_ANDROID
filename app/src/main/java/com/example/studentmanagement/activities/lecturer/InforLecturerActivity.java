package com.example.studentmanagement.activities.lecturer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;


import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.models.entity.Lecturer;
import com.example.studentmanagement.utils.FormatterDate;

@SuppressLint("SetTextI18n")
public class InforLecturerActivity extends CustomAppCompactActivity {
    Toolbar toolbar;
    TextView tvMaGV, tvHoTen, tvGioiTinh, tvSDT, tvEmail, tvKhoa,tvNgaySinh, tvVaiTro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_infor_lecturer_view);
        setControl();
        setEvent();
    }

    private void setEvent() {
        setSupportActionBar(toolbar);
        getInforLecturer();
    }

    private void getInforLecturer() {
        Lecturer lecturer= (Lecturer) getIntent().getSerializableExtra("lecturer");
        tvMaGV.setText(lecturer.getMaGv());
        tvHoTen.setText(lecturer.getHo() + " " + lecturer.getTen());
        tvSDT.setText(lecturer.getSdt());
        tvGioiTinh.setText(lecturer.getPhai());
        tvKhoa.setText(lecturer.getMaKhoa());
        tvEmail.setText(lecturer.getEmail());
        tvNgaySinh.setText(new FormatterDate.Fomatter(lecturer.getNgaySinh())
                .from(FormatterDate.ISO_8601)
                .to(FormatterDate.dd_slash_MM_slash_yyyy)
                .format()
        );
    }

    private void setControl() {
        toolbar = findViewById(R.id.toolbar);
        tvMaGV = findViewById(R.id.tvMaGV);
        tvHoTen = findViewById(R.id.tvHoTenGV);
        tvEmail = findViewById(R.id.tvEmail);
        tvSDT = findViewById(R.id.tvSDT);
        tvKhoa = findViewById(R.id.tvKhoa);
        tvNgaySinh = findViewById(R.id.tvNgaySinh);
        tvGioiTinh = findViewById(R.id.tvGT);
        tvVaiTro=findViewById(R.id.tvVaiTro);
    }

}