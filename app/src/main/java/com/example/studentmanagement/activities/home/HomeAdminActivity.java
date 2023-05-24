package com.example.studentmanagement.activities.home;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;

import androidx.appcompat.widget.Toolbar;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.authen.ChangePasswordActivity;
import com.example.studentmanagement.activities.course.MainCourseActivity;
import com.example.studentmanagement.activities.creditclass.MainCreditClassActivity;
import com.example.studentmanagement.activities.faculty.MainFacultyActivity;
import com.example.studentmanagement.activities.authen.LoginActivity;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.activities.lecturer.MainLecturerActivity;
import com.example.studentmanagement.activities.practicalclass.MainPracticalClassActivity;
import com.example.studentmanagement.activities.score.MainScoreAdminActivity;
import com.example.studentmanagement.activities.statistic.MainStatisticAdminActivity;
import com.example.studentmanagement.activities.student.MainStudentActivity;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.view.FacultyItem;
import com.example.studentmanagement.models.view.PracticalClassItem;
import com.example.studentmanagement.models.view.SemesterItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.MyFuncButton;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("SetTextI18n")
public class HomeAdminActivity extends CustomAppCompactActivity {
    TextView tvUsername, tvUserrole;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    Toolbar toolbar;
    Button btnKhoa, btnLop, btnGiangVien, btnSinhVien, btnHocPhan, btnLopTinChi, btnDiem, btnThongke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home_admin);
        setControl();
        setEvent();
    }

    @SuppressLint("NonConstantResourceId")
    private void setEvent() {
        setSupportActionBar(toolbar);
        setActionBarDrawerToggle();
        navigationView.getMenu().getItem(0).setVisible(false);
        setUpInforAdmin();

        toolbar.setNavigationOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawers();
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_ChangePassword:
                    startActivity(new Intent(HomeAdminActivity.this, ChangePasswordActivity.class));
                    break;
                case R.id.navLogOut:
                    startActivity(new Intent(HomeAdminActivity.this, LoginActivity.class));
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        btnKhoa.setOnClickListener(view -> callFaculty(MyFuncButton.ADMIN_FACULTY_MANAGEMENT));
        btnHocPhan.setOnClickListener(view -> callFaculty(MyFuncButton.ADMIN_COURSE_MANAGEMENT));
        btnGiangVien.setOnClickListener(view -> callFaculty(MyFuncButton.ADMIN_LECTURER_MANAGEMENT));
        btnLop.setOnClickListener(view -> callFaculty(MyFuncButton.ADMIN_PRACTICAL_CLASS_MANAGEMENT));
        btnSinhVien.setOnClickListener(view -> callPracticalClass(MyFuncButton.ADMIN_STUDENT_MANAGEMENT));
        btnThongke.setOnClickListener(view -> callPracticalClass(MyFuncButton.ADMIN_STATISTIC));
        btnDiem.setOnClickListener(view -> callPracticalClass(MyFuncButton.ADMIN_SCORE));
        btnLopTinChi.setOnClickListener(view -> callPracticalClass(MyFuncButton.ADMIN_CREDIT_CLASS_MANAGEMENT));
    }

    private void callScheme(MyFuncButton myFuncButton, List<PracticalClassItem> practicalClassItemList) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(HomeAdminActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<List<SemesterItem>>>> call = apiManager.getApiService().getAllScheme(jwt);
        call.enqueue(new Callback<ResponseObject<List<List<SemesterItem>>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<List<SemesterItem>>>> call, @NonNull Response<ResponseObject<List<List<SemesterItem>>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<List<SemesterItem>>> resData = response.body();
                    List<SemesterItem> data = resData.getRetObj().get(0);
                    Intent intent;
                    if (myFuncButton == MyFuncButton.ADMIN_STATISTIC) {
                        intent = new Intent(HomeAdminActivity.this, MainStatisticAdminActivity.class);
                    } else if(myFuncButton == MyFuncButton.ADMIN_SCORE){
                        intent = new Intent(HomeAdminActivity.this, MainScoreAdminActivity.class);
                        intent.putExtra("listPracticalClassItemSpn", (ArrayList<PracticalClassItem>) practicalClassItemList);
                    } else { // myFuncButton == MyFuncButton.ADMIN_CREDIT_CLASS_MANAGEMENT
                        intent = new Intent(HomeAdminActivity.this, MainCreditClassActivity.class);
                        intent.putExtra("listPracticalClassItemSpn", (ArrayList<PracticalClassItem>) practicalClassItemList);
                    }
                    intent.putExtra("listSemesterItemSpn", (ArrayList<SemesterItem>) data);
                    startActivity(intent);
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(HomeAdminActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<List<SemesterItem>>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(HomeAdminActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void callPracticalClass(MyFuncButton myFuncButton) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(HomeAdminActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<List<PracticalClassItem>>>> call = apiManager.getApiService().getAllPracticalClass(jwt);
        call.enqueue(new Callback<ResponseObject<List<List<PracticalClassItem>>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<List<PracticalClassItem>>>> call,
                                   @NonNull Response<ResponseObject<List<List<PracticalClassItem>>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<List<PracticalClassItem>>> resData = response.body();
                    List<PracticalClassItem> data = resData.getRetObj().get(0);
                    Intent intent;
                    if (myFuncButton == MyFuncButton.ADMIN_STUDENT_MANAGEMENT) {
                        intent = new Intent(HomeAdminActivity.this, MainStudentActivity.class);
                        intent.putExtra("listPracticalClassItemSpn", (ArrayList<PracticalClassItem>) data);
                        startActivity(intent);
                    } else callScheme(myFuncButton, data);
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(HomeAdminActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<List<PracticalClassItem>>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(HomeAdminActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void callFaculty(MyFuncButton myFuncButton) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(HomeAdminActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<List<FacultyItem>>>> call = apiManager.getApiService().getAllFaculty(jwt);
        call.enqueue(new Callback<ResponseObject<List<List<FacultyItem>>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<List<FacultyItem>>>> call, @NonNull Response<ResponseObject<List<List<FacultyItem>>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<List<FacultyItem>>> resData = response.body();
                    List<FacultyItem> data = resData.getRetObj().get(0);
                    Intent intent;
                    if (myFuncButton == MyFuncButton.ADMIN_FACULTY_MANAGEMENT) {
                        intent = new Intent(HomeAdminActivity.this, MainFacultyActivity.class);
                        intent.putExtra("listFacultyItemLv", (ArrayList<FacultyItem>) data);
                    } else if (myFuncButton == MyFuncButton.ADMIN_COURSE_MANAGEMENT) {
                        intent = new Intent(HomeAdminActivity.this, MainCourseActivity.class);
                        intent.putExtra("listFacultyItemSpn", (ArrayList<FacultyItem>) data);
                    } else if (myFuncButton == MyFuncButton.ADMIN_LECTURER_MANAGEMENT) {
                        intent = new Intent(HomeAdminActivity.this, MainLecturerActivity.class);
                        intent.putExtra("listFacultyItemSpn", (ArrayList<FacultyItem>) data);
                    } else{ // myFuncButton == MyFuncButton.ADMIN_PRACTICAL_CLASS_MANAGEMENT) {
                        intent = new Intent(HomeAdminActivity.this, MainPracticalClassActivity.class);
                        intent.putExtra("listFacultyItemSpn", (ArrayList<FacultyItem>) data);
                    }
                    startActivity(intent);
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(HomeAdminActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<List<FacultyItem>>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(HomeAdminActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void setUpInforAdmin() {
        View viewHeader = navigationView.getHeaderView(0);
        TextView tvUN_nav = viewHeader.findViewById(R.id.tvUserName);
        TextView tvEmail_nav = viewHeader.findViewById(R.id.tvEmail);

        MyPrefs myPrefs = MyPrefs.getInstance();
        tvUsername.setText(myPrefs.getString(getApplicationContext(), "username", ""));
        tvUserrole.setText(myPrefs.getString(getApplicationContext(), "role", ""));
        tvUN_nav.setText(myPrefs.getString(getApplicationContext(), "username", ""));
        tvEmail_nav.setText(myPrefs.getString(getApplicationContext(), "email", ""));
    }

    private void setActionBarDrawerToggle() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_open, R.string.navigation_close);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        actionBarDrawerToggle.getDrawerArrowDrawable().setGapSize(10);
        actionBarDrawerToggle.getDrawerArrowDrawable().setBarLength(50);
        actionBarDrawerToggle.getDrawerArrowDrawable().setBarThickness(10);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    public void onBackPressed() {
        new CustomDialog.Builder(this)
                .setImage(R.drawable.icon_question)
                .setMessage("Bạn có muốn thoát ứng dụng không?")
                .setPositiveButton("Có", view -> finishAffinity(), dismiss -> true)
                .setNegativeButton("Không", null, dismiss -> true)
                .build()
                .show();
    }

    private void setControl() {
        tvUsername = findViewById(R.id.tvUserName);
        tvUserrole = findViewById(R.id.tvUserRole);
        navigationView = findViewById(R.id.navigation_menu);
        btnKhoa = findViewById(R.id.btnKhoa);
        btnLop = findViewById(R.id.btnLop);
        btnGiangVien = findViewById(R.id.btnGiangVien);
        btnSinhVien = findViewById(R.id.btnSinhVien);
        btnHocPhan = findViewById(R.id.btnHocPhan);
        btnLopTinChi = findViewById(R.id.btnLopTinChi);
        btnDiem = findViewById(R.id.btnDiem);
        btnThongke = findViewById(R.id.btnThongKe);
        drawerLayout = findViewById(R.id.drawerlayout);
        toolbar = findViewById(R.id.toolbar);
    }
}