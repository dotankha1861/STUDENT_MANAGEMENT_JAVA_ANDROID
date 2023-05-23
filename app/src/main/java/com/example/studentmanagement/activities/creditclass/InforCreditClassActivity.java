package com.example.studentmanagement.activities.creditclass;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.models.entity.CreditClass;
import com.example.studentmanagement.models.entity.DetailCreditClass;
import com.example.studentmanagement.utils.FormatterDate;

import java.util.ArrayList;
import java.util.List;

public class InforCreditClassActivity extends CustomAppCompactActivity {
    Toolbar toolbar;
    TextView  tvMaLopTC, tvHocPhan, tvGiangVien, tvLop, tvSoLuongTD ;
    LinearLayout lnlSchedule;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_credit_class_view);
        setControl();
        setEvent();
    }

    private void setEvent() {
        setSupportActionBar(toolbar);
        getInforCreditClass();
    }

    private void getInforCreditClass() {
        CreditClass creditClass = (CreditClass) getIntent().getSerializableExtra("creditClass");
        List<DetailCreditClass> creditClassList = (ArrayList<DetailCreditClass>) getIntent().getSerializableExtra("detailCreditClass");

        tvMaLopTC.setText(creditClass.getMaLopTc());
        tvHocPhan.setText(creditClass.getMaMh());
        tvGiangVien.setText(creditClass.getMaGv());
        tvLop.setText(creditClass.getMaLop());
        tvSoLuongTD.setText(creditClass.getSoLuong());

        creditClassList.forEach(this::addView);
    }

    private void addView(DetailCreditClass detailCreditClass) {
        @SuppressLint("InflateParams") View lichHoc = getLayoutInflater().inflate(R.layout.item_view_schedule,null,false);
        ((TextView) lichHoc.findViewById(R.id.tvThu)).setText(detailCreditClass.getThu());
        ((TextView) lichHoc.findViewById(R.id.tvTiet)).setText(String.valueOf(detailCreditClass.getTiet()));
        ((TextView) lichHoc.findViewById(R.id.tvSoTiet)).setText(String.valueOf(detailCreditClass.getSoTiet()));
        ((TextView) lichHoc.findViewById(R.id.tvPhong)).setText(detailCreditClass.getPhong());
        ((TextView) lichHoc.findViewById(R.id.tvNgayBD)).setText(
                new FormatterDate.Fomatter(detailCreditClass.getTimeBd())
                        .from(FormatterDate.yyyy_dash_MM_dash_dd)
                        .to(FormatterDate.dd_slash_MM_slash_yyyy)
                        .format()
        );
        ((TextView) lichHoc.findViewById(R.id.tvNgayKT)).setText(
                new FormatterDate.Fomatter(detailCreditClass.getTimeKt())
                        .from(FormatterDate.yyyy_dash_MM_dash_dd)
                        .to(FormatterDate.dd_slash_MM_slash_yyyy)
                        .format()
        );
        lnlSchedule.addView(lichHoc);
    }

    private void setControl() {
        toolbar = findViewById(R.id.toolbar);
        tvMaLopTC = findViewById(R.id.tvMaLopTC);
        tvHocPhan = findViewById(R.id.tvHP);
        tvGiangVien = findViewById(R.id.tvGV);
        tvLop = findViewById(R.id.tvLop);
        tvSoLuongTD = findViewById(R.id.tvSoLuongTD);
        lnlSchedule = findViewById(R.id.lnlLich);
    }
}