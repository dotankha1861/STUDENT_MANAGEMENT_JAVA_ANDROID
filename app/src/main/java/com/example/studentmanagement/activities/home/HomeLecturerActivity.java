package com.example.studentmanagement.activities.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.authen.ChangePasswordActivity;
import com.example.studentmanagement.activities.authen.LoginActivity;
import com.example.studentmanagement.activities.authen.LogoutActivity;
import com.example.studentmanagement.activities.authen.UserInforActivity;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.activities.notification.MainNotificationActivity;
import com.example.studentmanagement.activities.score.MainScoreLecturerActivity;
import com.example.studentmanagement.activities.statistic.MainStatisticLecturerActivity;
import com.example.studentmanagement.activities.timetable.TimeTableActivity;
import com.example.studentmanagement.adapter.ActivityAdapter;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ERole;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.entity.CreditClass;
import com.example.studentmanagement.models.entity.Lecturer;
import com.example.studentmanagement.models.view.ActivityItem;
import com.example.studentmanagement.models.view.CreditClassItem;
import com.example.studentmanagement.models.view.FacultyItem;
import com.example.studentmanagement.models.view.SemesterItem;
import com.example.studentmanagement.models.view.TimeTableItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.CircleTransformation;
import com.example.studentmanagement.utils.FormatterDate;
import com.example.studentmanagement.utils.MyFuncButton;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("SetTextI18n")
public class HomeLecturerActivity extends CustomAppCompactActivity {
    TextView tvUsername, tvUserrole, tvToday, tvThu, tvNotActivity;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    Toolbar toolbar;
    Button btnThongBao, btnThoiKhoaBieu, btnDiem, btnThongke;
    Lecturer lecturer;
    FacultyItem facultyItem;
    int dateOfWeek;
    Date today;
    ListView lvActivity;

    ActivityAdapter adapterActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home_lecturer);
        setControl();
        setEvent();
    }

    @SuppressLint("NonConstantResourceId")
    private void setEvent() {
        setSupportActionBar(toolbar);
        setActionBarDrawerToggle();
        setUserInfor();
        setLecturerInfor();
        setTodayActivities();
        toolbar.setNavigationOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawers();
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navUserInfor:
                        if (lecturer == null || facultyItem == null) {
                            Toast.makeText(getApplicationContext(), "Đang tải dữ liệu. Vui lòng thử lại sau!", Toast.LENGTH_LONG).show();
                            break;
                        }
                        callActivityInforLectuter();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        break;

                    case R.id.nav_ChangePassword:
                        startActivity(new Intent(HomeLecturerActivity.this, ChangePasswordActivity.class));
                        break;
                    case R.id.navLogOut:
                        startActivity(new Intent(HomeLecturerActivity.this, LogoutActivity.class));
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }

        });
        btnThoiKhoaBieu.setOnClickListener(view -> callScheme(MyFuncButton.VIEW_TIMETABLE));
        btnDiem.setOnClickListener(view -> callScheme(MyFuncButton.LECTURER_SCORE));
        btnThongke.setOnClickListener(view -> callScheme(MyFuncButton.LECTURER_STATISTIC));
        btnThongBao.setOnClickListener(view -> callCreditClass());
    }

    private void callCreditClass() {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(HomeLecturerActivity.this, "jwt", "");
        String lecturerCode = myPrefs.getString(HomeLecturerActivity.this, "username", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<CreditClassItem>>> call = apiManager.getApiService().getAllCreditClassByLecturerCodeLatest(jwt, lecturerCode);
        call.enqueue(new Callback<ResponseObject<List<CreditClassItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<CreditClassItem>>> call, @NonNull Response<ResponseObject<List<CreditClassItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<CreditClassItem>> resData = response.body();
                    List<CreditClassItem> creditClassItemList = resData.getRetObj();
                    Intent intent = new Intent(HomeLecturerActivity.this, MainNotificationActivity.class);
                    intent.putExtra("listCreditClass", (ArrayList<CreditClassItem>) creditClassItemList);
                    startActivity(intent);
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(HomeLecturerActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<CreditClassItem>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(HomeLecturerActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void callActivityInforLectuter() {
        Intent intent = new Intent(HomeLecturerActivity.this, UserInforActivity.class);
        intent.putExtra("lecturer", lecturer);
        intent.putExtra("facultyName", facultyItem.getTenKhoa());
        startActivity(intent);
    }

    private void getFacultyInfor(String maKhoa) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(HomeLecturerActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<List<FacultyItem>>>> call = apiManager.getApiService().getAllFaculty(jwt);
        call.enqueue(new Callback<ResponseObject<List<List<FacultyItem>>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<List<FacultyItem>>>> call, @NonNull Response<ResponseObject<List<List<FacultyItem>>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<List<FacultyItem>>> resData = response.body();
                    List<FacultyItem> data = resData.getRetObj().get(0);
                    facultyItem = new FacultyItem();
                    facultyItem.setMaKhoa(maKhoa);
                    facultyItem = data.get(data.indexOf(facultyItem));
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(HomeLecturerActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<List<FacultyItem>>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(HomeLecturerActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void setLecturerInfor() {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(HomeLecturerActivity.this, "jwt", "");
        String idLogin = myPrefs.getString(HomeLecturerActivity.this, "idLogin", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<Lecturer>> call = apiManager.getApiService().getLecturerById(jwt, idLogin);
        call.enqueue(new Callback<ResponseObject<Lecturer>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Lecturer>> call, @NonNull Response<ResponseObject<Lecturer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<Lecturer> resData = response.body();
                    if (resData.getStatus().equals("error")) {
                        Toast.makeText(getApplicationContext(), "User không tồn tại!", Toast.LENGTH_LONG).show();
                    } else {
                        lecturer = resData.getRetObj();
                        View headerView = navigationView.getHeaderView(0);
                        try {
                            Picasso.get()
                                    .load(lecturer.getHinhAnh())
                                    .transform(new CircleTransformation())
                                    .placeholder(R.drawable.baseline_account_circle_24)
                                    .error(R.drawable.baseline_account_circle_24)
                                    .into((ImageView) headerView.findViewById(R.id.imvAvatar));
                        } catch (Exception ignored) {}
                        getFacultyInfor(lecturer.getMaKhoa());
                    }
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(HomeLecturerActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Lecturer>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(HomeLecturerActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void callScheme(MyFuncButton myFuncButton) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(HomeLecturerActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<List<SemesterItem>>>> call = apiManager.getApiService().getAllScheme(jwt);
        call.enqueue(new Callback<ResponseObject<List<List<SemesterItem>>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<List<SemesterItem>>>> call, @NonNull Response<ResponseObject<List<List<SemesterItem>>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<List<SemesterItem>>> resData = response.body();
                    List<SemesterItem> data = resData.getRetObj().get(0);

                    Intent intent;
                    if (myFuncButton == MyFuncButton.VIEW_TIMETABLE) {
                        intent = new Intent(HomeLecturerActivity.this, TimeTableActivity.class);
                    } else if (myFuncButton == MyFuncButton.LECTURER_STATISTIC) {
                        intent = new Intent(HomeLecturerActivity.this, MainStatisticLecturerActivity.class);
                    } else { // myFuncButton==MyFuncButton.VIEW_SCORE
                        intent = new Intent(HomeLecturerActivity.this, MainScoreLecturerActivity.class);
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
                        new CustomDialog.BuliderOKDialog(HomeLecturerActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<List<SemesterItem>>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(HomeLecturerActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void setActivities() {
        List<TimeTableItem> tableItemList = (List<TimeTableItem>) getIntent().getSerializableExtra("todayActivities");

        if(tableItemList.size()==0) {
            tvNotActivity.setText("Không có hoạt động nào");
            return;
        }

        List<ActivityItem> todayActivity = tableItemList.get(dateOfWeek - 2).getTkbDtoList();
        if (todayActivity == null || todayActivity.size() == 0) {
            tvNotActivity.setText("Không có hoạt động nào");
        } else {
            tvNotActivity.setVisibility(View.GONE);
            adapterActivity = new ActivityAdapter(HomeLecturerActivity.this, R.layout.item_activity, (ArrayList<ActivityItem>) todayActivity);
            lvActivity.setAdapter(adapterActivity);
            adapterActivity.notifyDataSetChanged();
        }
    }

    private void setTodayActivities() {
        today = new Date(System.currentTimeMillis());
        dateOfWeek = FormatterDate.getDateOfWeek(today);
        tvThu.setText(dateOfWeek == 1 ? "Chủ nhật" : "Thứ " + dateOfWeek);
        tvToday.setText(FormatterDate.convertDate2String(today, FormatterDate.dd_slash_MM_slash_yyyy));
        setActivities();
    }

    private void setUserInfor() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        tvUsername.setText(sharedPreferences.getString("userFullName", ""));
        tvUserrole.setText(sharedPreferences.getString("role", ""));
        View headerView = navigationView.getHeaderView(0);
        TextView tvUN_nav = headerView.findViewById(R.id.tvUserName);
        TextView tvEmail_nav = headerView.findViewById(R.id.tvEmail);
        tvUN_nav.setText(tvUsername.getText());
        tvEmail_nav.setText(sharedPreferences.getString("email", ""));
    }

    private void setActionBarDrawerToggle() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_open, R.string.navigation_close);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        actionBarDrawerToggle.getDrawerArrowDrawable().setGapSize(12);
        actionBarDrawerToggle.getDrawerArrowDrawable().setBarLength(70);
        actionBarDrawerToggle.getDrawerArrowDrawable().setBarThickness(15);
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
        tvToday = findViewById(R.id.tvToday);
        tvUsername = findViewById(R.id.tvUserName);
        tvUserrole = findViewById(R.id.tvUserRole);
        navigationView = findViewById(R.id.navigation_menu);
        btnThongBao = findViewById(R.id.btnThongBao);
        btnThoiKhoaBieu = findViewById(R.id.btnTKB);
        btnDiem = findViewById(R.id.btnDiem);
        btnThongke = findViewById(R.id.btnThongKe);
        lvActivity = findViewById(R.id.lvSchedule);
        tvThu = findViewById(R.id.tvThu);
        drawerLayout = findViewById(R.id.drawerlayout);
        toolbar = findViewById(R.id.toolbar);
        tvNotActivity = findViewById(R.id.NotActivity);
    }
}