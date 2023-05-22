package com.example.studentmanagement.activities.authen;

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
import com.example.studentmanagement.models.requestbody.RequestBodyChangePassword;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends CustomAppCompactActivity {
    EditText edtCrtMK, edtNewMK, edtXNMK;
    boolean error = false;
    Button btnLuu;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_change_password);
        setControl();
        setEvent();
    }

    private void setEvent() {
        setSupportActionBar(toolbar);
        btnLuu.setOnClickListener(view -> handleClickBtnLuu());
    }

    private void handleClickBtnLuu() {
        String crtPassword = edtCrtMK.getText().toString().trim();
        String newPassword = edtNewMK.getText().toString().trim();
        String confirmPassword = edtXNMK.getText().toString().trim();

        if (TextUtils.isEmpty(confirmPassword)) {
            edtXNMK.setError("Vui lòng xác nhận lại mật khẩu");
            edtXNMK.requestFocus();
            error = true;
        } else if (!newPassword.equals(confirmPassword)) {
            edtXNMK.setError("Mật khẩu xác nhận không trùng khớp");
            edtXNMK.requestFocus();
            error = true;
        }

        if (TextUtils.isEmpty(newPassword)) {
            edtNewMK.setError("Vui lòng nhập Mật khẩu mới");
            edtNewMK.requestFocus();
            error = true;
        }

        if (TextUtils.isEmpty(crtPassword)) {
            edtCrtMK.setError("Vui lòng nhập mật khẩu hiện tại");
            edtCrtMK.requestFocus();
            error = true;
        }

        if (error) {
            error = false;
            return;
        }

        showPosNegDialogChangePassword(crtPassword, newPassword);
    }

    private void showPosNegDialogChangePassword(String crtPassword, String newPassword) {
        new CustomDialog.Builder(ChangePasswordActivity.this)
                .setImage(R.drawable.icon_question)
                .setMessage("Bạn có muốn thay đổi mật khẩu không?")
                .setPositiveButton("Đồng ý", view -> callChangePassword(crtPassword, newPassword), dismiss -> true)
                .setNegativeButton("Hủy", null, dismiss -> true)
                .build()
                .show();
    }

    private void callChangePassword(String crtPassword, String newPassword) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(getApplicationContext(), "jwt", "");
        String id = myPrefs.getString(getApplicationContext(), "id", "");
        RequestBodyChangePassword requestBodyChangePassword = new RequestBodyChangePassword();
        requestBodyChangePassword.setId(id);
        requestBodyChangePassword.setMatKhauCu(crtPassword);
        requestBodyChangePassword.setMatKhauMoi(newPassword);

        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<Object>> call = apiManager.getApiService().changePassword(jwt, requestBodyChangePassword);
        call.enqueue(new Callback<ResponseObject<Object>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Object>> call, @NonNull Response<ResponseObject<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<Object> resData = response.body();
                    if (resData.getStatus().equals("error")) {
                        new CustomDialog.BuliderOKDialog(ChangePasswordActivity.this)
                                .setMessage(resData.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    } else {
                        new CustomDialog.BuliderOKDialog(ChangePasswordActivity.this)
                                .setMessage("Đổi mật khẩu thành công")
                                .setSuccessful(true)
                                .build()
                                .show();
                        edtCrtMK.setText("");
                        edtNewMK.setText("");
                        edtXNMK.setText("");
                    }
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(ChangePasswordActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Object>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(ChangePasswordActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void setControl() {
        btnLuu = findViewById(R.id.btnLuu);
        toolbar = findViewById(R.id.toolbar);
        edtCrtMK = findViewById(R.id.edtNowPassword);
        edtNewMK = findViewById(R.id.edtNewPassword);
        edtXNMK = findViewById(R.id.edtConfirmPassword);
    }
}