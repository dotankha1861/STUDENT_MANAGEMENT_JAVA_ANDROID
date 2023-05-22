package com.example.studentmanagement.activities.faculty;

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
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.entity.Faculty;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditFacultyActivity extends CustomAppCompactActivity {
    Button btnLuu;
    Boolean error = false;
    EditText edtTenKhoa, edtMaKhoa, edtSDT, edtEmail;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_infor_faculty_edit);
        setControl();
        setEvent();
    }

    private void setEvent() {
        setSupportActionBar(toolbar);
        customToolBar();
        setInforKhoa();
        edtMaKhoa.setEnabled(false);
        btnLuu.setOnClickListener( view -> handleLuu());
    }


    private void customToolBar() {
        TextView tvTitle = toolbar.findViewById(R.id.tvTitle);
        tvTitle.setText("SỬA KHOA");
    }

    private void handleLuu() {
        String maKhoa=edtMaKhoa.getText().toString().trim();
        String tenKhoa=edtTenKhoa.getText().toString().trim();
        String sdt=edtSDT.getText().toString().trim();
        String email=edtEmail.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            edtEmail.setError("Vui lòng nhập email khoa");
            edtEmail.requestFocus();
            error = true;
        }

        if(TextUtils.isEmpty(sdt)){
            edtSDT.setError("Vui lòng nhập số điện thoại khoa");
            edtSDT.requestFocus();
            error = true;
        }

        if(TextUtils.isEmpty(tenKhoa)){
            edtTenKhoa.setError("Vui lòng nhập tên khoa");
            edtTenKhoa.requestFocus();
            error = true;
        }

        if(TextUtils.isEmpty(maKhoa)){
            edtMaKhoa.setError("Vui lòng nhập mã khoa");
            edtMaKhoa.requestFocus();
            error = true;
        }
        else if(maKhoa.length()<3){
            edtMaKhoa.setError("Mã khoa phải có tối thiểu 3 kí tự");
            edtMaKhoa.requestFocus();
            error = true;
        }

        if(error){
            error=false;
            return;
        }

        Faculty faculty = new Faculty();
        faculty.setMaKhoa(maKhoa);
        faculty.setTenKhoa(tenKhoa);
        faculty.setSdt(sdt);
        faculty.setEmail(email);
        faculty.setId(((Faculty) getIntent().getSerializableExtra("faculty")).getId());
        new CustomDialog.Builder(EditFacultyActivity.this)
                .setImage(R.drawable.iv_dialog)
                .setMessage("Bạn có muốn lưu thay đổi không?")
                .setPositiveButton("Đồng ý", view -> callUpdateFaculty(faculty), dismiss -> true)
                .setNegativeButton("Hủy", null, dismiss -> true)
                .build()
                .show();
    }

    private void callUpdateFaculty(Faculty faculty) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(EditFacultyActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<Faculty>> call = apiManager.getApiService().updateFaculty(jwt, faculty);
        call.enqueue(new Callback<ResponseObject<Faculty>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Faculty>> call, @NonNull Response<ResponseObject<Faculty>> response) {
                if (response.isSuccessful()&&response.body()!=null) {
                    ResponseObject<Faculty> resData = response.body();
                    if(resData.getStatus().equals("error")) {
                        new CustomDialog.BuliderOKDialog(EditFacultyActivity.this)
                                .setMessage(resData.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                    else {
                        Intent intent = new Intent();
                        intent.putExtra("changedFaculty", resData.getRetObj());
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
                        new CustomDialog.BuliderOKDialog(EditFacultyActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseObject<Faculty>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(EditFacultyActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }
    private void setInforKhoa() {
        Faculty faculty = (Faculty) getIntent().getSerializableExtra("faculty");
        edtMaKhoa.setText(faculty.getMaKhoa());
        edtTenKhoa.setText(faculty.getTenKhoa());
        edtEmail.setText(faculty.getEmail());
        edtSDT.setText(faculty.getSdt());
    }

    private void setControl() {
        btnLuu=findViewById(R.id.btnLuu);
        edtTenKhoa=findViewById(R.id.edtTenKhoa);
        edtMaKhoa=findViewById(R.id.edtMaKhoa);
        edtSDT=findViewById(R.id.edtSDT);
        edtEmail=findViewById(R.id.edtEmail);
        toolbar = findViewById(R.id.toolbar);
    }
}