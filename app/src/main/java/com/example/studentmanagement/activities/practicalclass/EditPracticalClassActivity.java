package com.example.studentmanagement.activities.practicalclass;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.course.EditCourseActivity;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.adapter.PracticalClassAdapter;
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

public class EditPracticalClassActivity extends CustomAppCompactActivity {
    Button btnLuu;
    EditText edtTenLop, edtMaLop;
    Boolean error = false;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_infor_practical_class_edit);
        setControl();
        setEvent();
    }

    private void setEvent() {
        setSupportActionBar(toolbar);
        customToolbar();
        setInforPracticalClass();

        btnLuu.setOnClickListener(view -> handleLuu());
    }

    private void setInforPracticalClass() {
        PracticalClass practicalClass = (PracticalClass) getIntent().getSerializableExtra("practicalClass");
        edtTenLop.setText(practicalClass.getTenLop());
        edtMaLop.setText(practicalClass.getMaLop());
    }

    private void customToolbar() {
        TextView tvTitle = toolbar.findViewById(R.id.tvTitle);
        tvTitle.setText("SỬA LỚP");
    }

    private void handleLuu() {
        String maLop = edtMaLop.getText().toString().trim();
        String tenLop = edtTenLop.getText().toString().trim();

        if (TextUtils.isEmpty(tenLop)) {
            edtTenLop.setError("Vui lòng nhập tên lớp");
            edtTenLop.requestFocus();
            error = true;
        }

        if (error) {
            error = false;
            return;
        }

        PracticalClass practicalClass = new PracticalClass();
        practicalClass.setMaLop(maLop);
        practicalClass.setTenLop(maLop);
        practicalClass.setMaKhoa(((PracticalClass) getIntent().getSerializableExtra("practicalClass")).getMaKhoa());
        practicalClass.setId(((PracticalClass) getIntent().getSerializableExtra("practicalClass")).getId());

        new CustomDialog.BuliderPosNegDialog(EditPracticalClassActivity.this)
                .setMessage("Bạn có muốn lưu thay đổi không?")
                .setPositiveButton("Đồng ý", view -> callUpdatePracticalClass(practicalClass), dismiss -> true)
                .setNegativeButton("Hủy", null, dismiss -> true)
                .build()
                .show();
    }

    private void callUpdatePracticalClass(PracticalClass practicalClass) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(EditPracticalClassActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<PracticalClass>> call = apiManager.getApiService().updatePracticalClass(jwt, practicalClass);
        call.enqueue(new Callback<ResponseObject<PracticalClass>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<PracticalClass>> call, @NonNull Response<ResponseObject<PracticalClass>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<PracticalClass> resData = response.body();
                    if (resData.getStatus().equals("error")) {
                        new CustomDialog.BuliderOKDialog(EditPracticalClassActivity.this)
                                .setMessage(resData.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();

                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("changedPracticalClass", resData.getRetObj());
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
                        new CustomDialog.BuliderOKDialog(EditPracticalClassActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<PracticalClass>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(EditPracticalClassActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void setControl() {
        btnLuu = findViewById(R.id.btnLuu);
        edtTenLop = findViewById(R.id.edtTenLop);
        edtMaLop = findViewById(R.id.edtMaLop);
        toolbar = findViewById(R.id.toolbar);
    }
}