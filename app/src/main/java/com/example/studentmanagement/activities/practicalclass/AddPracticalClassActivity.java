package com.example.studentmanagement.activities.practicalclass;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;


import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.entity.PracticalClass;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPracticalClassActivity extends CustomAppCompactActivity {

    Button btnLuu;
    EditText edtTenLop, edtMaLop;
    Toolbar toolbar;
    Boolean error = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_infor_practical_class_edit);
        setControl();
        setEvent();
    }

    private void setEvent() {
        setSupportActionBar(toolbar);
        btnLuu.setOnClickListener( view -> handleLuu());

    }
    private void handleLuu() {
        String maLop = edtMaLop.getText().toString().trim();
        String tenLop = edtTenLop.getText().toString().trim();

        if(TextUtils.isEmpty(tenLop)){
            edtTenLop.setError("Vui lòng nhập tên lớp");
            edtTenLop.requestFocus();
            error = true;
        }
        else if(tenLop.length()<4){
            edtTenLop.setError("Tên lớp phải có tối thiểu 4 kí tự");
            edtTenLop.requestFocus();
            error = true;
        }

        if(TextUtils.isEmpty(maLop)){
            edtMaLop.setError("Vui lòng nhập mã lớp");
            edtMaLop.requestFocus();
            error = true;
        }
        else if(maLop.length()<4){
            edtMaLop.setError("Mã lớp phải có tối thiểu 4 kí tự");
            edtMaLop.requestFocus();
            error = true;
        }

        if(error){
            error=false;
            return;
        }

        PracticalClass practicalClass = new PracticalClass();
        practicalClass.setMaLop(maLop);
        practicalClass.setTenLop(tenLop);
        practicalClass.setMaKhoa(getIntent().getStringExtra("crtFacultyCode"));

        callAddPracticalClass(practicalClass);
    }
    private void callAddPracticalClass(PracticalClass practicalClass) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(AddPracticalClassActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<PracticalClass>> call = apiManager.getApiService().createPracticalClass(jwt, practicalClass);
        call.enqueue(new Callback<ResponseObject<PracticalClass>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<PracticalClass>> call,
                                   @NonNull Response<ResponseObject<PracticalClass>> response) {
                if (response.isSuccessful()&&response.body()!=null) {
                    ResponseObject<PracticalClass> resData = response.body();
                    if(resData.getStatus().equals("error")){
                        new CustomDialog.BuliderOKDialog(AddPracticalClassActivity.this)
                                .setMessage(resData.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                    else {
                        Intent intent = new Intent();
                        intent.putExtra("newPracticalClass", resData.getRetObj());
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
                        new CustomDialog.BuliderOKDialog(AddPracticalClassActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseObject<PracticalClass>> call, @NonNull Throwable t) {
              new CustomDialog.BuliderOKDialog(AddPracticalClassActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }
    private void setControl() {
        btnLuu=findViewById(R.id.btnLuu);
        edtTenLop=findViewById(R.id.edtTenLop);
        edtMaLop=findViewById(R.id.edtMaLop);
        toolbar=findViewById(R.id.toolbar);
    }


}

