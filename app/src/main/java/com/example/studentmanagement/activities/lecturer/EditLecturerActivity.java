package com.example.studentmanagement.activities.lecturer;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.entity.Lecturer;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.FormatterDate;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditLecturerActivity extends CustomAppCompactActivity {

    Button btnLuu;
    EditText edtMaGV, edtHoGV, edtTenGV, edtSDT, edtEmail, edtNgaySinh;
    RadioButton radNam, radNu;
    TextView tvCalendar;
    Boolean error = false;
    DatePickerDialog.OnDateSetListener setListener;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_infor_lecturer_edit);
        setControl();
        setEvent();
    }

    private void setEvent() {
        setSupportActionBar(toolbar);
        customToolbar();
        setInforLecturer();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        tvCalendar.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(EditLecturerActivity.this, android.R.style.Theme_DeviceDefault_Dialog,
                    setListener, year, month, day);
            datePickerDialog.show();
        });
        setListener = (view, year1, month1, day1) -> {
            month1 = month1 + 1;
            String date = day1 + "/" + month1 + "/" + year1;
            edtNgaySinh.setText(date);
        };
        btnLuu.setOnClickListener(view -> handleLuu());
    }

    private void handleLuu() {
        String tenGV = edtTenGV.getText().toString().trim();
        String maGV = edtMaGV.getText().toString().trim();
        String hoGV = edtHoGV.getText().toString().trim();
        String sdt = edtSDT.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String ngaysinh = edtNgaySinh.getText().toString().trim();
        boolean isNam = radNam.isChecked();


        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập email của giảng viên");
            edtEmail.requestFocus();
            error = true;
        }
        if (TextUtils.isEmpty(ngaysinh)) {
            edtNgaySinh.setError("Vui lòng nhập ngày sinh của giảng viên");
            edtNgaySinh.requestFocus();
            error = true;
        }
        if (TextUtils.isEmpty(sdt)) {
            edtSDT.setError("Vui lòng nhập SĐT của giảng viên");
            edtSDT.requestFocus();
            error = true;
        }
        if (TextUtils.isEmpty(tenGV)) {
            edtTenGV.setError("Vui lòng nhập tên giảng viên");
            edtTenGV.requestFocus();
            error = true;
        }
        if (TextUtils.isEmpty(hoGV)) {
            edtHoGV.setError("Vui lòng nhập họ giảng viên");
            edtHoGV.requestFocus();
            error = true;
        } else if (maGV.length() < 4) {
            edtMaGV.setError("Mã giảng viên phải có tối thiểu 4 kí tự");
            edtMaGV.requestFocus();
            error = true;
        }

        if (error) {
            error = false;
            return;
        }

        Lecturer lecturer = new Lecturer();
        lecturer.setMaGv(maGV);
        lecturer.setHo(hoGV);
        lecturer.setTen(tenGV);
        lecturer.setPhai(isNam ? "Nam" : "Nữ");
        lecturer.setSdt(sdt);
        lecturer.setEmail(email);
        lecturer.setNgaySinh(new FormatterDate.Fomatter(ngaysinh)
                .from(FormatterDate.dd_slash_MM_slash_yyyy)
                .to(FormatterDate.yyyy_dash_MM_dash_dd)
                .format()
        );
        lecturer.setMaKhoa(((Lecturer) getIntent().getSerializableExtra("lecturer")).getMaKhoa());
        lecturer.setId(((Lecturer) getIntent().getSerializableExtra("lecturer")).getId());

        new CustomDialog.BuliderPosNegDialog(EditLecturerActivity.this)
                .setMessage("Bạn có muốn lưu thay đổi không?")
                .setPositiveButton("Đồng ý", view -> callUpdateLecturer(lecturer), dismiss -> true)
                .setNegativeButton("Hủy", null, dismiss -> true)
                .build()
                .show();
    }

    private void callUpdateLecturer(Lecturer lecturer) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(EditLecturerActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<Lecturer>> call = apiManager.getApiService().updateLecturer(jwt, lecturer);
        call.enqueue(new Callback<ResponseObject<Lecturer>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Lecturer>> call, @NonNull Response<ResponseObject<Lecturer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<Lecturer> resData = response.body();
                    if (resData.getStatus().equals("error")) {
                        new CustomDialog.BuliderOKDialog(EditLecturerActivity.this)
                                .setMessage(resData.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("changedLecturer", resData.getRetObj());
                        setResult(RESULT_OK, intent);
                        onBackPressed();
                    }
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(EditLecturerActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Lecturer>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(EditLecturerActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void setInforLecturer() {
        Lecturer lecturer= (Lecturer) getIntent().getSerializableExtra("lecturer");
        edtMaGV.setText(lecturer.getMaGv());
        edtHoGV.setText(lecturer.getHo());
        edtTenGV.setText(lecturer.getTen());
        edtSDT.setText(lecturer.getSdt());
        edtEmail.setText(lecturer.getEmail());

        edtNgaySinh.setText(new FormatterDate.Fomatter(lecturer.getNgaySinh())
                .from(FormatterDate.yyyy_dash_MM_dash_dd)
                .to(FormatterDate.dd_slash_MM_slash_yyyy)
                .format()
        );

        if (lecturer.getPhai().equalsIgnoreCase("nam")) radNam.setChecked(true);
        else radNu.setChecked(true);
        edtMaGV.setEnabled(false);

    }

    private void customToolbar() {
        TextView tvTitle = toolbar.findViewById(R.id.tvTitle);
        tvTitle.setText("SỬA GIẢNG VIÊN");
    }
    private void setControl() {
        edtNgaySinh = findViewById(R.id.edtNgaySinh);
        tvCalendar = findViewById(R.id.ivcalender);
        edtMaGV = findViewById(R.id.edtMaGV);
        edtHoGV = findViewById(R.id.edtHoGV);
        edtTenGV = findViewById(R.id.edtTenGV);
        edtEmail = findViewById(R.id.edtEmail);
        edtSDT = findViewById(R.id.edtSDT);
        radNam = findViewById(R.id.radNam);
        radNu = findViewById(R.id.radNu);
        btnLuu = findViewById(R.id.btnLuu);
        toolbar = findViewById(R.id.toolbar);
    }
}