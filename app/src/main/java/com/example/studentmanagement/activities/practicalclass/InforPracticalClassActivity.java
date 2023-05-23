package com.example.studentmanagement.activities.practicalclass;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.models.entity.PracticalClass;

public class InforPracticalClassActivity extends CustomAppCompactActivity {

    Toolbar toolbar;
    TextView tvMaLop, tvTenLop, tvMaKhoa ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_infor_practical_class_view);
        setControl();
        setEvent();
    }

    private void setEvent() {
        setSupportActionBar(toolbar);
        getInforLop();
    }

    private void getInforLop() {
        Intent intent = getIntent();
        PracticalClass practicalClass = (PracticalClass) intent.getSerializableExtra("practicalClass");
        tvMaLop.setText(practicalClass.getMaLop());
        tvTenLop.setText(practicalClass.getTenLop());
        tvMaKhoa.setText(practicalClass.getMaKhoa());
    }

    private void setControl() {
        toolbar = findViewById(R.id.toolbar);
        tvMaLop = findViewById(R.id.tvMaLop);
        tvTenLop = findViewById(R.id.tvTenLop);
        tvMaKhoa = findViewById(R.id.tvMaKhoa);
    }
}