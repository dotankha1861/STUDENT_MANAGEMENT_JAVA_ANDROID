package com.example.studentmanagement.activities.authen;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.ListFormatter;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.creditclass.MainCreditClassActivity;
import com.example.studentmanagement.activities.home.HomeAdminActivity;
import com.example.studentmanagement.activities.home.HomeLecturerActivity;
import com.example.studentmanagement.activities.home.HomeStudentActivity;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ERole;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.firebase.MyFirebaseMessagingService;
import com.example.studentmanagement.models.requestbody.RequestBodyLogin;
import com.example.studentmanagement.models.responsebody.ResponseBodyLogin;
import com.example.studentmanagement.models.view.CreditClassItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.ui.TextWatcherWrapper;
import com.example.studentmanagement.utils.MyFuncButton;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogoutActivity extends AppCompatActivity {

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_loading);
        ((TextView) findViewById(R.id.tvLoading)).setText("Tạm biệt !");
        MyPrefs myPrefs = MyPrefs.getInstance();
        ERole role = ERole.valueOf(myPrefs.getString(LogoutActivity.this, "role", ""));
        String username = myPrefs.getString(LogoutActivity.this, "username", "");
        callLogout();
        if(role==ERole.SINHVIEN) MyFirebaseMessagingService.removeTokenToDatabase(username);
    }

    private void callLogin() {
        Thread thread = new Thread(){
            @Override
            public void run(){
                try{
                    sleep(3000);
                    startActivity(new Intent(LogoutActivity.this, LoginActivity.class));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private void callLogout() {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(LogoutActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<Object>> call = apiManager.getApiService().logout(jwt);
        call.enqueue(new Callback<ResponseObject<Object>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Object>> call, @NonNull Response<ResponseObject<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callLogin();
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(LogoutActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Object>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(LogoutActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }
}
