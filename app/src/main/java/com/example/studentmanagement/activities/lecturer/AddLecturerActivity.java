package com.example.studentmanagement.activities.lecturer;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.firebase.UpLoadImage;
import com.example.studentmanagement.models.entity.Lecturer;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.CircleTransformation;
import com.example.studentmanagement.utils.FormatterDate;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLecturerActivity extends CustomAppCompactActivity {
    Button btnLuu, btnAvatar;
    ImageView imvAvatar;
    EditText edtMaGV, edtHoGV, edtTenGV, edtNgaySinh, edtSDT, edtEmail;
    RadioButton radNam, radNu;
    TextView tvCalendar;
    DatePickerDialog.OnDateSetListener setListener;
    Toolbar toolbar;
    Boolean error = false;
    boolean isSetImage = false;
    Uri uriPickedImage = null;
    ActivityResultLauncher<String> pickImageLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_infor_lecturer_edit);
        setControl();
        setEvent();

    }

    private void setEvent() {
        setSupportActionBar(toolbar);
        radNam.setChecked(true);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        tvCalendar.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(AddLecturerActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });

        setListener = (view, year1, month1, day1) -> {
            month1 = month1 + 1;
            String date = day1 + "/" + month1 + "/" + year1;
            edtNgaySinh.setText(date);
        };

        btnLuu.setOnClickListener(view -> handleLuuLecturer());
        radNam.setOnCheckedChangeListener((compoundButton, b) -> {
            if (radNam.isChecked() && !isSetImage)
                imvAvatar.setImageResource(R.drawable.icon_front_man);
        });
        radNu.setOnCheckedChangeListener((compoundButton, b) -> {
            if (radNu.isChecked() && !isSetImage)
                imvAvatar.setImageResource(R.drawable.icon_fornt_woman);
        });
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        Picasso.get()
                                .load(uri)
                                .transform(new CircleTransformation())
                                .into(imvAvatar);
                        isSetImage = true;
                        uriPickedImage = uri;
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                }
        );
        btnAvatar.setOnClickListener(view -> {
            pickImageLauncher.launch("image/*");
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void handleLuuLecturer() {
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
        }
        if (TextUtils.isEmpty(maGV)) {
            edtMaGV.setError("Vui lòng nhập mã giảng viên");
            edtMaGV.requestFocus();
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

        lecturer.setMaKhoa(getIntent().getStringExtra("crtFacultyCode"));
        if (uriPickedImage != null) {
            UploadTask uploadTask = UpLoadImage.saveImageToDatabase(lecturer.getMaGv(), uriPickedImage);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> downloadUrlTask = UpLoadImage.storageRef.child(lecturer.getMaGv()).getDownloadUrl();
                        downloadUrlTask.addOnSuccessListener(uriImage -> {
                            lecturer.setHinhAnh(uriImage.toString());
                            callAddLecturer(lecturer);
                        }).addOnFailureListener(exception -> new CustomDialog.BuliderOKDialog(AddLecturerActivity.this)
                                .setMessage("Hiện tại không thể thêm hình ảnh")
                                .setSuccessful(false)
                                .build()
                                .show()
                        );
                    }
            );
        } else callAddLecturer(lecturer);
    }


    private void callAddLecturer(Lecturer lecturer) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(AddLecturerActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<Lecturer>> call = apiManager.getApiService().createLecturer(jwt, lecturer);
        call.enqueue(new Callback<ResponseObject<Lecturer>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Lecturer>> call, @NonNull Response<ResponseObject<Lecturer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<Lecturer> resData = response.body();
                    if (resData.getStatus().equals("error")) {
                        new CustomDialog.BuliderOKDialog(AddLecturerActivity.this)
                                .setMessage(resData.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("newLecturer", resData.getRetObj());
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
                        new CustomDialog.BuliderOKDialog(AddLecturerActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Lecturer>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(AddLecturerActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void setControl() {
        btnLuu = findViewById(R.id.btnLuu);
        edtMaGV = findViewById(R.id.edtMaGV);
        edtHoGV = findViewById(R.id.edtHoGV);
        edtTenGV = findViewById(R.id.edtTenGV);
        radNam = findViewById(R.id.radNam);
        radNu = findViewById(R.id.radNu);
        edtNgaySinh = findViewById(R.id.edtNgaySinh);
        edtSDT = findViewById(R.id.edtSDT);
        edtEmail = findViewById(R.id.edtEmail);
        tvCalendar = findViewById(R.id.ivcalender);
        toolbar = findViewById(R.id.toolbar);
        imvAvatar = findViewById(R.id.imvAvatar);
        btnAvatar = findViewById(R.id.btnAvatar);
    }
}