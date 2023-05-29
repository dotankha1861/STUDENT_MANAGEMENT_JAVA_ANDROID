package com.example.studentmanagement.activities.authen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.activities.home.HomeAdminActivity;
import com.example.studentmanagement.activities.home.HomeLecturerActivity;
import com.example.studentmanagement.activities.home.HomeStudentActivity;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ERole;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.firebase.MyFirebaseMessagingService;
import com.example.studentmanagement.models.responsebody.ScoreStudent;
import com.example.studentmanagement.models.view.ActivityItem;
import com.example.studentmanagement.models.view.SemesterItem;
import com.example.studentmanagement.models.view.TimeTableItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.ui.TextWatcherWrapper;
import com.example.studentmanagement.utils.FormatterDate;
import com.example.studentmanagement.utils.MyFuncButton;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadingActivity extends CustomAppCompactActivity {
    int i = 0;
    EditText edtCount;
    Intent intent;
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_loading);
        ((TextView)findViewById(R.id.tvLoading)).setText("Xin chào !");
        MyPrefs myPrefs = MyPrefs.getInstance();
        ERole role = ERole.valueOf(myPrefs.getString(LoadingActivity.this, "role",""));
        String username = myPrefs.getString(LoadingActivity.this,"username","");
        edtCount = findViewById(R.id.edtCount);
        if(role == ERole.ADMIN) {
            intent = new Intent(getApplicationContext(), HomeAdminActivity.class);
            Thread thread = new Thread(){
                @Override
                public void run(){
                    try{
                        sleep(3000);
                        startActivity(new Intent(LoadingActivity.this, HomeAdminActivity.class));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
        else if(role == ERole.GIANGVIEN){
            intent = new Intent(getApplicationContext(), HomeLecturerActivity.class);
            callScheme(MyFuncButton.LECTURER_TODAY_ACTIVITY);
        }
        else{
            intent = new Intent(getApplicationContext(), HomeStudentActivity.class);
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String token = task.getResult();
                            MyFirebaseMessagingService.saveTokenToDatabase(username, token);
                        }
                    });
            callStudentHome();
            callScheme(MyFuncButton.STUDENT_TODAY_ACTIVITY);
        }
        edtCount.addTextChangedListener(new TextWatcherWrapper() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                i+=1;
                if(role == ERole.SINHVIEN && i == 2) startActivity(intent);
                if(role == ERole.GIANGVIEN && i ==1) startActivity(intent);
            }
        });
    }

    private void callStudentHome() {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(LoadingActivity.this, "jwt", "");
        String maSV = myPrefs.getString(LoadingActivity.this, "username", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<ScoreStudent>>> call = apiManager.getApiService().getScoreByStudentCode(jwt, maSV, null);
        call.enqueue(new Callback<ResponseObject<List<ScoreStudent>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<ScoreStudent>>> call, @NonNull Response<ResponseObject<List<ScoreStudent>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<ScoreStudent>> resData = response.body();
                    intent.putExtra("listScoreStudent", (ArrayList<ScoreStudent>)resData.getRetObj());
                    edtCount.setText("*");
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(LoadingActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<ScoreStudent>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(LoadingActivity.this)
                        .setMessage("Lỗi kết nối!" + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }
    private void callScheme(MyFuncButton myFuncButton) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(LoadingActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<List<SemesterItem>>>> call = apiManager.getApiService().getAllScheme(jwt);
        call.enqueue(new Callback<ResponseObject<List<List<SemesterItem>>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<List<SemesterItem>>>> call, @NonNull Response<ResponseObject<List<List<SemesterItem>>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<List<SemesterItem>>> resData = response.body();
                    List<SemesterItem> data = resData.getRetObj().get(0);
                    callActivities(data, myFuncButton);
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(LoadingActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<List<SemesterItem>>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(LoadingActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void callActivities(List<SemesterItem> data, MyFuncButton myFuncButton) {
        Date today = new Date(System.currentTimeMillis());
        int dateOfWeek = FormatterDate.getDateOfWeek(today);
        List<TimeTableItem> activityItemList = new ArrayList<>();
        Optional<SemesterItem> semesterItem = data.stream()
                .filter(item -> item.getTimeStudyBegin().compareTo(today) <= 0 && item.getTimeStudyEnd().compareTo(today) >= 0)
                .findAny();
        if (!semesterItem.isPresent() || dateOfWeek == 1) {
            intent.putExtra("todayActivities", (ArrayList<TimeTableItem>) activityItemList);
            edtCount.setText("*");
        } else {
            int week = FormatterDate.getWeek(semesterItem.get().getTimeStudyBegin(), today);
            MyPrefs myPrefs = MyPrefs.getInstance();
            String jwt = myPrefs.getString(LoadingActivity.this, "jwt", "");
            String code = myPrefs.getString(LoadingActivity.this, "username", "");
            ApiManager apiManager = ApiManager.getInstance();
            Call<ResponseObject<List<TimeTableItem>>> call = myFuncButton==MyFuncButton.STUDENT_TODAY_ACTIVITY? apiManager.getApiService().getTimeTableStudentByWeek(jwt, code, week):
                    apiManager.getApiService().getTimeTableLecturerByWeek(jwt, code, week);
            call.enqueue(new Callback<ResponseObject<List<TimeTableItem>>>() {
                @Override
                public void onResponse(@NonNull Call<ResponseObject<List<TimeTableItem>>> call, @NonNull Response<ResponseObject<List<TimeTableItem>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ResponseObject<List<TimeTableItem>> resData = response.body();
                        List<ActivityItem> todayActivity = resData.getRetObj().get(dateOfWeek - 2).getTkbDtoList();
                        if (todayActivity == null || todayActivity.size() == 0) {
                            intent.putExtra("todayActivities", (ArrayList<TimeTableItem>) activityItemList);
                            edtCount.setText("*");
                        } else {
                            intent.putExtra("todayActivities", (ArrayList<TimeTableItem>) resData.getRetObj());
                            edtCount.setText("*");
                        }
                    } else {
                        if (response.errorBody() != null) {
                            ResponseObject<Object> errorResponse = new Gson().fromJson(
                                    response.errorBody().charStream(),
                                    new TypeToken<ResponseObject<Object>>() {
                                    }.getType()
                            );
                            new CustomDialog.BuliderOKDialog(LoadingActivity.this)
                                    .setMessage("Lỗi" + errorResponse.getMessage())
                                    .setSuccessful(false)
                                    .build()
                                    .show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseObject<List<TimeTableItem>>> call, @NonNull Throwable t) {
                    new CustomDialog.BuliderOKDialog(LoadingActivity.this)
                            .setMessage("Lỗi kết nối! " + t.getMessage())
                            .setSuccessful(false)
                            .build()
                            .show();
                }
            });
        }
    }

}
