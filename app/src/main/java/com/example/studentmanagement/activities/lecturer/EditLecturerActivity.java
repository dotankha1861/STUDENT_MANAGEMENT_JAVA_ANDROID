package com.example.studentmanagement.activities.lecturer;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

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
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditLecturerActivity extends CustomAppCompactActivity {

    Button btnLuu, btnAvatar;
    ImageView imvAvatar;
    EditText edtMaGV, edtHoGV, edtTenGV, edtSDT, edtEmail, edtNgaySinh;
    RadioButton radNam, radNu;
    TextView tvCalendar;
    Boolean error = false;
    DatePickerDialog.OnDateSetListener setListener;

    Toolbar toolbar;
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
                        imvAvatar.requestFocus();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                }
        );
        btnAvatar.setOnClickListener(view -> {
            pickImageLauncher.launch("image/*");
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
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
        try {
            lecturer.setNgaySinh(new FormatterDate.Fomatter(ngaysinh)
                    .from(FormatterDate.dd_slash_MM_slash_yyyy)
                    .to(FormatterDate.yyyy_dash_MM_dash_dd)
                    .format()
            );
        }
        catch (RuntimeException e){
            edtNgaySinh.setError("Ngày sinh phải định dạng dd/MM/yyyy");
            edtNgaySinh.requestFocus();
            return;
        }
        lecturer.setMaKhoa(((Lecturer) getIntent().getSerializableExtra("lecturer")).getMaKhoa());
        lecturer.setId(((Lecturer) getIntent().getSerializableExtra("lecturer")).getId());
        lecturer.setHinhAnh(((Lecturer) getIntent().getSerializableExtra("lecturer")).getHinhAnh());
        if (uriPickedImage != null) {
            UploadTask uploadTask = UpLoadImage.saveImageToDatabase(lecturer.getMaGv(), uriPickedImage);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> downloadUrlTask = UpLoadImage.storageRef.child(lecturer.getMaGv()).getDownloadUrl();
                        downloadUrlTask.addOnSuccessListener(uriImage -> {
                            lecturer.setHinhAnh(uriImage.toString());
                            callUpdate(lecturer);
                        }).addOnFailureListener(exception -> new CustomDialog.BuliderOKDialog(EditLecturerActivity.this)
                                .setMessage("Hiện tại không thể thêm hình ảnh")
                                .setSuccessful(false)
                                .build()
                                .show()
                        );
                    }
            );
        } else callUpdate(lecturer);
    }

    private void callUpdate(Lecturer lecturer) {
        new CustomDialog.BuliderPosNegDialog(EditLecturerActivity.this)
                .setMessage("Bạn có muốn lưu thay đổi không?")
                .setPositiveButton("Đồng ý", view -> callUpdateLecturer(lecturer), dismiss -> true)
                .setNegativeButton("Hủy", null, dismiss -> true)
                .build()
                .show();
    }

    private void callUpdateLecturer(Lecturer lecturer) {
        ProgressDialog progressDialog = CustomDialog.LoadingDialog(EditLecturerActivity.this,"Loading...");
        progressDialog.show();
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(EditLecturerActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<Lecturer>> call = apiManager.getApiService().updateLecturer(jwt, lecturer);
        call.enqueue(new Callback<ResponseObject<Lecturer>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Lecturer>> call, @NonNull Response<ResponseObject<Lecturer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<Lecturer> resData = response.body();
                    progressDialog.dismiss();
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
        boolean isMale = lecturer.getPhai().equalsIgnoreCase("nam");
        if (isMale) radNam.setChecked(true);
        else radNu.setChecked(true);
        edtMaGV.setEnabled(false);
        try {
            Picasso.get()
                    .load(lecturer.getHinhAnh())
                    .transform(new CircleTransformation())
                    .placeholder(isMale ? R.drawable.icon_front_man : R.drawable.icon_fornt_woman)
                    .error(isMale ? R.drawable.icon_front_man : R.drawable.icon_fornt_woman)
                    .into(imvAvatar);
            if(lecturer.getHinhAnh()!=null) isSetImage = true;
        } catch (Exception ignored) {
            imvAvatar.setImageResource(isMale ? R.drawable.icon_front_man : R.drawable.icon_fornt_woman);
        }
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
        btnAvatar = findViewById(R.id.btnAvatar);
        imvAvatar = findViewById(R.id.imvAvatar);
    }
}