package com.example.studentmanagement.activities.score;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.adapter.ScoreCreditClassAdapter;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.entity.Course;
import com.example.studentmanagement.models.responsebody.ScoreCreditClass;
import com.example.studentmanagement.models.view.CreditClassItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("SetTextI18n")
public class ViewScoreCreditClassActivity extends CustomAppCompactActivity {

    TextView tvTenHP, tvMaLop, tvPercent;
    Toolbar toolbar;
    ListView lvScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_score_credit_class_view);
        setControl();
        setEvent();
    }

    private void setEvent() {
        setSupportActionBar(toolbar);
        callCourse();
        callScore();
    }
    private void callCourse() {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(ViewScoreCreditClassActivity.this, "jwt", "");
        CreditClassItem creditClassItem = (CreditClassItem) getIntent().getSerializableExtra("creditClassItem") ;
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<List<Course>>>> call = apiManager.getApiService().getAllCourseFull(jwt);
        call.enqueue(new Callback<ResponseObject<List<List<Course>>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<List<Course>>>> call, @NonNull Response<ResponseObject<List<List<Course>>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<List<Course>>> resData = response.body();
                    List<Course> data = resData.getRetObj().get(0);

                    Course course = new Course();
                    course.setMaMh(creditClassItem.getMaMh());
                    course = data.get(data.indexOf(course));
                    tvTenHP.setText(course.getTenMh());
                    tvMaLop.setText(creditClassItem.getMaLopTc());
                    tvPercent.setText(course.getPercentCc() + " - " + course.getPercentGk() + " - " + course.getPercentCk());
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(ViewScoreCreditClassActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<List<Course>>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(ViewScoreCreditClassActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void callScore() {
        ProgressDialog progressDialog = CustomDialog.LoadingDialog(ViewScoreCreditClassActivity.this, "Loading...");
        progressDialog.show();
        CreditClassItem creditClassItem = (CreditClassItem) getIntent().getSerializableExtra("creditClassItem");
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(ViewScoreCreditClassActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<ScoreCreditClass>>> call = apiManager.getApiService().getScoreByCreditClassCode(jwt, creditClassItem.getMaLopTc());
        call.enqueue(new Callback<ResponseObject<List<ScoreCreditClass>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<ScoreCreditClass>>> call, @NonNull Response<ResponseObject<List<ScoreCreditClass>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<ScoreCreditClass>> responseObject = response.body();
                    List<ScoreCreditClass> data = responseObject.getRetObj();
                    ScoreCreditClassAdapter scoreCreditClassAdapter = new ScoreCreditClassAdapter(ViewScoreCreditClassActivity.this, R.layout.item_listview_score_credit_class, (ArrayList<ScoreCreditClass>) data);
                    lvScore.setAdapter(scoreCreditClassAdapter);
                    progressDialog.dismiss();
                    scoreCreditClassAdapter.notifyDataSetChanged();
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        progressDialog.dismiss();
                        new CustomDialog.BuliderOKDialog(ViewScoreCreditClassActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<ScoreCreditClass>>> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new CustomDialog.BuliderOKDialog(ViewScoreCreditClassActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void setControl() {
        tvTenHP = findViewById(R.id.tvTenHP);
        tvMaLop = findViewById(R.id.tvMaLop);
        toolbar = findViewById(R.id.toolbar);
        tvPercent = findViewById(R.id.tvPercent);
        lvScore = findViewById(R.id.lvDiem);
    }
}