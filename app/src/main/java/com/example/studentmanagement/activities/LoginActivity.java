package com.example.studentmanagement.activities;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanagement.R;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ERole;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.requestbody.RequestBodyLogin;
import com.example.studentmanagement.models.responsebody.ResponseBodyLogin;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.ui.TextWatcherWrapper;
import com.example.studentmanagement.utils.MyPrefs;

import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("SetTextI18n")
public class LoginActivity extends AppCompatActivity {
    boolean error = false;
    TextView tvError, tvVisibleMK;
    EditText edtUsername, edtPassword;
    ToggleButton tgbVisible;
    Button btnLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        setControl();
        setEvent();
    }

    private void setEvent() {
        MyPrefs myPrefs = MyPrefs.getInstance();
        edtUsername.setText(myPrefs.getString(this, "username", ""));
        edtUsername.setSelection(edtUsername.getText().toString().length());

        tgbVisible.setOnCheckedChangeListener(((compoundButton, b) -> {
            if(tgbVisible.isChecked()){
                tgbVisible.setBackgroundResource(R.drawable.baseline_visibility_off_24);
                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                edtPassword.setSelection(edtPassword.getText().toString().length());
                tvVisibleMK.setText("Ẩn mật khẩu");
            }
            else{
                tgbVisible.setBackgroundResource(R.drawable.baseline_visibility_24);
                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                edtPassword.setSelection(edtPassword.getText().toString().length());
                tvVisibleMK.setText("Hiện mật khẩu");
            }
        }));

        edtUsername.addTextChangedListener(new TextWatcherWrapper() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkClearError();
            }
        });

        edtPassword.addTextChangedListener(new TextWatcherWrapper() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkClearError();
            }
        });

        btnLogIn.setOnClickListener(v -> handleClickLogIn());
    }

    private void handleClickLogIn() {
        Optional<RequestBodyLogin> requestBodyLogin = Optional.ofNullable(checkAndGetInforLogin());
        requestBodyLogin.ifPresent(this::callLogin);
    }

    private void callLogin(RequestBodyLogin requestBodyLogin) {
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<ResponseBodyLogin>> call = apiManager.getApiService().login(requestBodyLogin);
        call.enqueue(new Callback<ResponseObject<ResponseBodyLogin>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<ResponseBodyLogin>> call, @NonNull Response<ResponseObject<ResponseBodyLogin>> response) {
                if (response.isSuccessful()&&response.body()!=null) {
                    ResponseObject<ResponseBodyLogin> resData = response.body();
                    if(resData.getStatus().equals("error")){
                        tvError.setText("* Sai thông tin đăng nhập");
                        edtPassword.setText("");
                        edtUsername.requestFocus();
                        error = true;
                    }
                    else{
                        ResponseBodyLogin data = resData.getRetObj();
                        ERole role = data.getRoles().contains("ROLE_ADMIN") ? ERole.ADMIN :
                                data.getRoles().contains("ROLE_GIANGVIEN") ? ERole.GIANGVIEN : ERole.SINHVIEN;

                        MyPrefs myPrefs = MyPrefs.getInstance();
                        myPrefs.putString(LoginActivity.this, "jwt", "Bearer " + data.getJwt());
                        myPrefs.putString(LoginActivity.this, "id", data.getUserDetail().getId());
                        myPrefs.putString(LoginActivity.this, "email",data.getUserDetail().getEmail());
                        myPrefs.putString(LoginActivity.this, "userFullName", data.getUserDetail().getUserFullName());
                        myPrefs.putString(LoginActivity.this, "username", requestBodyLogin.getUsername());
                        myPrefs.putString(LoginActivity.this, "idLogin", data.getUserDetail().getIdLogin());
                        myPrefs.putString(LoginActivity.this, "role", role.toString());

                        Intent intent = null;
                        if(role == ERole.ADMIN) intent = new Intent(getApplicationContext(),HomeAdminActivity.class);
//                        else if(role == ERole.GIANGVIEN) intent = new Intent(getApplicationContext(),HomeGVActivity.class);
//                        else intent = new Intent(getApplicationContext(),HomeSVActivity.class);
                        startActivity(intent);
                    }
                } else {
                    tvError.setText("* Lỗi!");
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseObject<ResponseBodyLogin>> call, @NonNull Throwable t) {
                tvError.setText("* Lỗi! Hiện tại không thể đăng nhập");
            }
        });
    }

    private RequestBodyLogin checkAndGetInforLogin() {
        String username = edtUsername.getText().toString();
        String password= edtPassword.getText().toString();
        if(TextUtils.isEmpty(password)){
            edtPassword.setError("Vui lòng nhập Mật khẩu");
            edtPassword.requestFocus();
            error = true;
        }
        if(TextUtils.isEmpty(username)){
            edtUsername.setError("Vui lòng nhập tài khoản");
            edtUsername.requestFocus();
            error = true;
        }
        if(error){
            tvError.setText("* Vui lòng kiểm tra lại thông tin đăng nhập");
            return null;
        }
        RequestBodyLogin requestBodyLogin = new RequestBodyLogin();
        requestBodyLogin.setUsername(username);
        requestBodyLogin.setPassword(password);
        return requestBodyLogin;
    }

    private void checkClearError() {
        if(error){
            tvError.setText("");
            error = false;
        }
    }

    public void onBackPressed(){
        new CustomDialog.Builder(this)
                .setImage(R.drawable.icon_question)
                .setMessage("Bạn có muốn thoát ứng dụng không?")
                .setPositiveButton("Có", view -> finishAffinity(), dismiss -> true)
                .setNegativeButton("Không", null, dismiss -> true)
                .build()
                .show();
    }
    private void setControl() {
        tvError = findViewById(R.id.tvError);
        tvVisibleMK = findViewById(R.id.tvVisibleMK);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        tgbVisible = findViewById(R.id.tgbVisible);
        btnLogIn = findViewById(R.id.btnDangnhap);
    }
}