package com.example.studentmanagement.activities.course;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.entity.Course;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCourseActivity extends CustomAppCompactActivity {
    Toolbar toolbar;
    Button btnLuu;
    Boolean error = false;
    EditText edtTenHP, edtMaHP, edtSTC, edtSoTietLT, edtSoTietTH, edtHeSoCC, edtHeSoGK, edtHeSoCK;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_infor_course_edit);
        setControl();
        setEvent();
    }

    private void setEvent() {
        setSupportActionBar(toolbar);
        btnLuu.setOnClickListener( view -> handleLuu());
    }

    private void handleLuu() {
        String tenHP = edtTenHP.getText().toString().trim();
        String maHP= edtMaHP.getText().toString().trim();
        String soTC = edtSTC.getText().toString().trim();
        String soTietLT = edtSoTietLT.getText().toString().trim();
        String soTietTH = edtSoTietTH.getText().toString().trim();
        String heSoCC = edtHeSoCC.getText().toString().trim();
        String heSoGK = edtHeSoGK.getText().toString().trim();
        String heSoCK = edtHeSoCK.getText().toString().trim();

        if(TextUtils.isEmpty(heSoCK)){
            edtHeSoCK.setError("Vui lòng nhập hệ số cuối kỳ");
            edtHeSoCK.requestFocus();
            error = true;
        }
        else if(Integer.parseInt(heSoCK) <= 0 ){
            edtHeSoCK.setError("Hệ số cuối kỳ lớn hơn 0");
            edtHeSoCK.requestFocus();
            error = true;
        }
        else if(Integer.parseInt(heSoCC) +  Integer.parseInt(heSoGK)+ Integer.parseInt(heSoCK) != 100){
            edtHeSoCK.setError("Tổng hệ số phải bằng 100");
            edtHeSoCK.requestFocus();
            error = true;
        }
        if(TextUtils.isEmpty(heSoGK)){
            edtHeSoGK.setError("Vui lòng nhập hệ số giữa kỳ");
            edtHeSoGK.requestFocus();
            error = true;
        }

        else if(Integer.parseInt(heSoGK) <= 0 ){
            edtHeSoGK.setError("Hệ số giữa kỳ lớn hơn 0");
            edtHeSoGK.requestFocus();
            error = true;
        }

        if(TextUtils.isEmpty(heSoCC)){
            edtHeSoCC.setError("Vui lòng nhập hệ số chuyên cần");
            edtHeSoCC.requestFocus();
            error = true;
        }
        else if(Integer.parseInt(heSoCC) <= 0 ){
            edtHeSoCC.setError("Hệ số chuyên cần lớn hơn 0");
            edtHeSoCC.requestFocus();
            error = true;
        }

        if(TextUtils.isEmpty(soTietTH)){
            edtSoTietTH.setError("Vui lòng nhập số tiết TH");
            edtSoTietTH.requestFocus();
            error = true;
        }

        else if((Integer.parseInt(soTietTH) + Integer.parseInt(soTietLT)) > (Integer.parseInt(soTC)*15)){
            edtSoTietTH.setError("Tổng số tiết phải nhỏ hơn hoặc bằng " + (Integer.parseInt(soTC)*15));
            edtSoTietTH.requestFocus();
            error = true;
        }


        if(TextUtils.isEmpty(soTietLT)){
            edtSoTietLT.setError("Vui lòng nhập số tiết LT");
            edtSoTietLT.requestFocus();
            error = true;
        }

        if(TextUtils.isEmpty(soTC)){
            edtSTC.setError("Vui lòng nhập số tín chỉ");
            edtSTC.requestFocus();
            error = true;
        }
        else if(Integer.parseInt(soTC) <= 0 || Integer.parseInt(soTC) >10){
            edtSTC.setError("Số tín chỉ > 0 và <= 10");
            edtSTC.requestFocus();
            error = true;
        }

        if(TextUtils.isEmpty(maHP)){
            edtMaHP.setError("Vui lòng nhập mã học phần");
            edtMaHP.requestFocus();
            error = true;
        }

        if(TextUtils.isEmpty(tenHP)){
            edtTenHP.setError("Vui lòng nhập tên học phần");
            edtTenHP.requestFocus();
            error = true;
        }
        if(error){
            error = false;
            return;
        }

        Course course = new Course();
        course.setMaMh(maHP);
        course.setTenMh(tenHP);
        course.setSoTc(Integer.parseInt(soTC));
        course.setSoTietLt(Integer.parseInt(soTietLT));
        course.setSoTietTh(Integer.parseInt(soTietTH));
        course.setPercentCc(Integer.parseInt(heSoCC));
        course.setPercentGk(Integer.parseInt(heSoGK));
        course.setPercentCk(Integer.parseInt(heSoCK));
        course.setMaKhoa(getIntent().getStringExtra("crtFacultyCode"));

        callAddCourse(course);
    }


    private void callAddCourse(Course course) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(AddCourseActivity.this, "jwt","");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<Course>> call = apiManager.getApiService().createCourse(jwt, course);
        call.enqueue(new Callback<ResponseObject<Course>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Course>> call, @NonNull Response<ResponseObject<Course>> response) {
                if (response.isSuccessful()&&response.body()!=null) {
                    ResponseObject<Course> resData = response.body();
                    if(resData.getStatus().equals("error")) {
                        new CustomDialog.BuliderOKDialog(AddCourseActivity.this)
                                .setMessage(resData.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                    else {
                        Intent intent = new Intent();
                        intent.putExtra("newCourse", resData.getRetObj());
                        setResult(RESULT_OK, intent);
                        onBackPressed();
                    }
                }
                else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(AddCourseActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseObject<Course>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(AddCourseActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void setControl() {
        btnLuu = findViewById(R.id.btnLuu);
        edtTenHP = findViewById(R.id.edtTenHP);
        edtMaHP = findViewById(R.id.edtMaHP);
        edtSTC = findViewById(R.id.edtSTC);
        edtHeSoCC=findViewById(R.id.edtHeSoCC);
        edtHeSoCK=findViewById(R.id.edtHeSoCK);
        edtHeSoGK=findViewById(R.id.edtHeSoGK);
        edtSoTietLT=findViewById(R.id.edtSotietLT);
        edtSoTietTH=findViewById(R.id.edtSoTietTH);
        toolbar = findViewById(R.id.toolbar);
    }
}