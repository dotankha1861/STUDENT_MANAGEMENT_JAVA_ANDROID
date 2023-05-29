package com.example.studentmanagement.activities.authen;

import android.annotation.SuppressLint;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.home.HomeAdminActivity;
import com.example.studentmanagement.activities.home.HomeLecturerActivity;
import com.example.studentmanagement.activities.home.HomeStudentActivity;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ERole;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.firebase.MyFirebaseMessagingService;
import com.example.studentmanagement.models.requestbody.RequestBodyLogin;
import com.example.studentmanagement.models.responsebody.ResponseBodyLogin;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.ui.TextWatcherWrapper;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

        tgbVisible.setOnCheckedChangeListener(((compoundButton, b) -> handleTgbVisibleCheckedChange()));
        btnLogIn.setOnClickListener(v -> handleClickLogIn());
    }

    private void handleTgbVisibleCheckedChange() {
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
    }

    private void handleClickLogIn() {
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
            return;
        }
        RequestBodyLogin requestBodyLogin = new RequestBodyLogin();
        requestBodyLogin.setUsername(username);
        requestBodyLogin.setPassword(password);
        callLogin(requestBodyLogin);
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
                        myPrefs.putString(getApplicationContext(), "jwt", "Bearer " + data.getJwt());
                        myPrefs.putString(getApplicationContext(), "id", data.getUserDetail().getId());
                        myPrefs.putString(getApplicationContext(), "email",data.getUserDetail().getEmail());
                        myPrefs.putString(getApplicationContext(), "userFullName", data.getUserDetail().getUserFullName());
                        myPrefs.putString(getApplicationContext(), "username", requestBodyLogin.getUsername());
                        myPrefs.putString(getApplicationContext(), "idLogin", data.getUserDetail().getIdLogin());
                        myPrefs.putString(getApplicationContext(), "role", role.toString());

                        startActivity(new Intent(LoginActivity.this, LoadingActivity.class));
                    }
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        tvError.setText("* Lỗi! " + errorResponse.getMessage());
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseObject<ResponseBodyLogin>> call, @NonNull Throwable t) {
                tvError.setText("* Lỗi! Hiện tại không thể đăng nhập. " + t.getMessage());
            }
        });
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