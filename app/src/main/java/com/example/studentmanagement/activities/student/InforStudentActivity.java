package com.example.studentmanagement.activities.student;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.models.entity.Student;
import com.example.studentmanagement.utils.FormatterDate;
import com.example.studentmanagement.utils.StatusStudent;

import java.text.SimpleDateFormat;
@SuppressLint("SetTextI18n")
public class InforStudentActivity extends CustomAppCompactActivity {
    Toolbar toolbar;
    TextView tvMaSV, tvHoTen, tvNgaySinh, tvGioiTinh, tvMaLop, tvEmail, tvSDT, tvTrangThai, tvDiaChi, tvNoiSinh ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_infor_student_view);
        setControl();
        setEvent();
    }
    private void setEvent() {
        setSupportActionBar(toolbar);
        getInforStudent();
    }
    private void getInforStudent() {
        Student student = (Student) getIntent().getSerializableExtra("student");
        tvMaSV.setText(student.getMaSv());
        tvHoTen.setText(student.getHo() + " " + student.getTen());
        tvSDT.setText(student.getSdt());
        tvGioiTinh.setText(student.getPhai());
        tvMaLop.setText(student.getMaLop());
        tvEmail.setText(student.getEmail());
        tvDiaChi.setText(student.getDiaChi());
        tvNoiSinh.setText(student.getNoiSinh());
        tvTrangThai.setText(StatusStudent.status.get(student.getTrangThai()));
        tvNgaySinh.setText(new FormatterDate.Fomatter(student.getNgaySinh())
                .from(FormatterDate.ISO_8601)
                .to(FormatterDate.dd_slash_MM_slash_yyyy)
                .format()
        );
    }

    private void setControl() {
        toolbar = findViewById(R.id.toolbar);
        tvMaSV = findViewById(R.id.tvMaSV);
        tvHoTen = findViewById(R.id.tvHoTenSV);
        tvMaLop = findViewById(R.id.tvMaLop);
        tvNgaySinh = findViewById(R.id.tvNgaySinh);
        tvNoiSinh = findViewById(R.id.tvNoiSinh);
        tvTrangThai = findViewById(R.id.tvTT);
        tvSDT = findViewById(R.id.tvSDT);
        tvEmail = findViewById(R.id.tvEmail);
        tvGioiTinh = findViewById(R.id.tvGT);
        tvDiaChi = findViewById(R.id.tvDiaChi);
    }

}