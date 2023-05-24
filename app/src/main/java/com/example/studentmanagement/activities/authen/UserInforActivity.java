package com.example.studentmanagement.activities.authen;

import android.annotation.SuppressLint;
import android.content.Intent;


import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.api.ERole;
import com.example.studentmanagement.models.entity.Lecturer;
import com.example.studentmanagement.models.entity.Student;
import com.example.studentmanagement.utils.FormatterDate;
import com.example.studentmanagement.utils.MyPrefs;

import java.text.SimpleDateFormat;

@SuppressLint("SetTextI18n")
public class UserInforActivity extends CustomAppCompactActivity {
    TextView tvUsername, tvUserRole, tvIDMa, tvNgaySinh, tvGioiTinh, tvSDT, tvEmail, tvKhoa, tvTypeMa;
    ImageButton btnBack;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_infor_user);
        setControl();
        setEvent();
    }


    private void setEvent() {
        setSupportActionBar(toolbar);
        getInforUser();

    }

    private void getInforUser() {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String role = myPrefs.getString(UserInforActivity.this, "role", "");
        String userFullNane = myPrefs.getString(UserInforActivity.this, "userFullName", "");
        Intent intent = getIntent();
        tvUsername.setText(userFullNane);
        tvUserRole.setText(role);
        if (ERole.valueOf(role) == ERole.GIANGVIEN) {
            tvTypeMa.setText("Mã giảng viên: ");
            Lecturer lecturer = (Lecturer) intent.getSerializableExtra("lecturer");
            tvIDMa.setText(lecturer.getMaGv());
            tvNgaySinh.setText(new FormatterDate.Fomatter(lecturer.getNgaySinh())
                    .from(FormatterDate.yyyy_dash_MM_dash_dd)
                    .to(FormatterDate.dd_slash_MM_slash_yyyy)
                    .format()
            );
            tvGioiTinh.setText(lecturer.getPhai());
            tvSDT.setText(lecturer.getSdt());
            tvEmail.setText(lecturer.getEmail());
            tvKhoa.setText(intent.getStringExtra("facultyName"));
        }
        else if(ERole.valueOf(role)==ERole.SINHVIEN){
            tvTypeMa.setText("Mã sinh viên:");
            Student student = (Student) intent.getSerializableExtra("student");
            tvIDMa.setText(student.getMaSv());
            tvNgaySinh.setText(new FormatterDate.Fomatter(student.getNgaySinh())
                    .from(FormatterDate.yyyy_dash_MM_dash_dd)
                    .to(FormatterDate.dd_slash_MM_slash_yyyy)
                    .format()
            );
            tvGioiTinh.setText(student.getPhai());
            tvSDT.setText(student.getSdt());
            tvEmail.setText(student.getEmail());
            tvKhoa.setText(intent.getStringExtra("facultyName"));
        }
    }


    private void setControl() {
        tvUsername = findViewById(R.id.tvUserName);
        tvUserRole = findViewById(R.id.tvUserRole);
        tvIDMa = findViewById(R.id.tvMaGV);
        tvNgaySinh = findViewById(R.id.tvUserNgaysinh);
        tvGioiTinh = findViewById(R.id.tvUserGioitinh);
        tvSDT = findViewById(R.id.tvUserSDT);
        tvEmail = findViewById(R.id.tvUserEmail);
        tvKhoa = findViewById(R.id.tvUserKhoa);
        toolbar = findViewById(R.id.toolbar);
        tvTypeMa = findViewById(R.id.tvTypeMa);
    }

    public void onBackPressed() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}