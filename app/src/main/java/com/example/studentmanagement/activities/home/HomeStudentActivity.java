package com.example.studentmanagement.activities.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.DeniedByServerException;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.authen.LoginActivity;
import com.example.studentmanagement.activities.authen.UserInforActivity;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.activities.enrollcourse.EnrollCourseActivity;
import com.example.studentmanagement.activities.score.MainScoreLecturerActivity;
import com.example.studentmanagement.activities.score.ViewScoreStudentActivity;
import com.example.studentmanagement.activities.statistic.MainStatisticLecturerActivity;
import com.example.studentmanagement.activities.timetable.TimeTableActivity;
import com.example.studentmanagement.adapter.ActivityAdapter;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ERole;
import com.example.studentmanagement.api.ResponseObject;

import com.example.studentmanagement.models.entity.PracticalClass;
import com.example.studentmanagement.models.entity.Student;
import com.example.studentmanagement.models.view.ActivityItem;
import com.example.studentmanagement.models.view.EnrollCourseItem;
import com.example.studentmanagement.models.view.FacultyItem;
import com.example.studentmanagement.models.view.SemesterItem;
import com.example.studentmanagement.models.view.StudentItem;
import com.example.studentmanagement.models.view.TimeTableItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.FormatterDate;
import com.example.studentmanagement.utils.MyFuncButton;
import com.example.studentmanagement.utils.MyPrefs;
import com.example.studentmanagement.utils.StatusEnroll;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("SetTextI18n")
public class HomeStudentActivity extends CustomAppCompactActivity {

    TextView tvUsername, tvUserrole, tvToday, tvThu, tvNotActivity;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    Student student;
    FacultyItem facultyItem;
    int dateOfWeek;
    Date today;
    ListView lvActivity;
    Toolbar toolbar;
    Button btnKhoa, btnThoiKhoaBieu, btnDiem, btnDangKy;

    ActivityAdapter adapterActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home_student);
        setControl();
        setEvent();
    }

    private void setEvent() {
        setSupportActionBar(toolbar);
        setActionBarDrawerToggle();
        setUserInfor();
        setStudentInfor();
        setTodayActivities();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navUserInfor:
                        if (student == null || facultyItem == null) {
                            Toast.makeText(getApplicationContext(), "Đang tải dữ liệu. Vui lòng thử lại sau!", Toast.LENGTH_LONG).show();
                            break;
                        }
                        callActivityInforStudent();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        break;
                    case R.id.navLogOut:
                        startActivity(new Intent(HomeStudentActivity.this, LoginActivity.class));
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        btnThoiKhoaBieu.setOnClickListener(view -> callScheme(MyFuncButton.VIEW_TIMETABLE));
        btnDiem.setOnClickListener(view -> callScheme(MyFuncButton.STUDENT_SCORE));
        btnDangKy.setOnClickListener(view -> callEnrollCourse());
//        btnKhoa.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (khoa == null) {
//                    Toast.makeText(getApplicationContext(), "Đang tải dữ liệu. Vui lòng thử lại sau!", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                Intent intent = new Intent(HomeSVActivity.this, KhoaGVActivity.class);
//                intent.putExtra("khoa", khoa);
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        });
    }

    private void callEnrollCourse() {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(HomeStudentActivity.this, "jwt","");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<EnrollCourseItem>>> call = apiManager.getApiService().getListEnrolledCourse(jwt, student.getMaSv());
        call.enqueue(new Callback<ResponseObject<List<EnrollCourseItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<EnrollCourseItem>>> call, @NonNull Response<ResponseObject<List<EnrollCourseItem>>> response) {
                if(response.isSuccessful()&& response.body()!=null){
                    List<EnrollCourseItem> data = response.body().getRetObj();
                    Intent intent = new Intent(HomeStudentActivity.this, EnrollCourseActivity.class);
                    intent.putExtra("listEnrolledCourse", (ArrayList<EnrollCourseItem>) data.stream()
                            .peek(hp -> hp.setStatusEnroll(StatusEnroll.DALUU))
                            .collect(Collectors.toList()));
                    intent.putExtra("student", student);
                    startActivity(intent);
                }
                else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(HomeStudentActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<EnrollCourseItem>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(HomeStudentActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }
    private void setStudentInfor() {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(HomeStudentActivity.this, "jwt", "");
        String idLogin = myPrefs.getString(HomeStudentActivity.this, "idLogin", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<Student>> call = apiManager.getApiService().getStudentById(jwt, idLogin);
        call.enqueue(new Callback<ResponseObject<Student>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Student>> call, @NonNull Response<ResponseObject<Student>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<Student> resData = response.body();
                    if (resData.getStatus().equals("error")) {
                        Toast.makeText(getApplicationContext(), "User không tồn tại!", Toast.LENGTH_LONG).show();
                    } else {
                        student = resData.getRetObj();
                        getClassInfor(student.getMaLop());
                    }
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(HomeStudentActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<Student>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(HomeStudentActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void getClassInfor(String maLop) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(HomeStudentActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<List<PracticalClass>>>> call = apiManager.getApiService().getAllPracticalClassFull(jwt);
        call.enqueue(new Callback<ResponseObject<List<List<PracticalClass>>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<List<PracticalClass>>>> call, @NonNull Response<ResponseObject<List<List<PracticalClass>>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<List<PracticalClass>>> resData = response.body();
                    List<PracticalClass> data = resData.getRetObj().get(0);
                    getKhoaInfor(data.get(0).getMaKhoa());
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(HomeStudentActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<List<PracticalClass>>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(HomeStudentActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
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

    private void setTodayActivities() {
        today = new Date(System.currentTimeMillis());
        dateOfWeek = FormatterDate.getDateOfWeek(today);
        tvThu.setText(dateOfWeek == 1 ? "Chủ nhật" : "Thứ " + dateOfWeek);
        tvToday.setText(FormatterDate.convertDate2String(today, FormatterDate.dd_slash_MM_slash_yyyy));
        callScheme(MyFuncButton.TODAY_ACTIVITY);
    }

    private void callScheme(MyFuncButton myFuncButton) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(HomeStudentActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<List<SemesterItem>>>> call = apiManager.getApiService().getAllScheme(jwt);
        call.enqueue(new Callback<ResponseObject<List<List<SemesterItem>>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<List<SemesterItem>>>> call, @NonNull Response<ResponseObject<List<List<SemesterItem>>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<List<SemesterItem>>> resData = response.body();
                    List<SemesterItem> data = resData.getRetObj().get(0);
                    if (myFuncButton == MyFuncButton.TODAY_ACTIVITY) setActivities(data);
                    else {
                        Intent intent;
                        if (myFuncButton == MyFuncButton.VIEW_TIMETABLE) {
                            intent = new Intent(HomeStudentActivity.this, TimeTableActivity.class);
                        } else { // myFuncButton==MyFuncButton.VIEW_SCORE
                            intent = new Intent(HomeStudentActivity.this, ViewScoreStudentActivity.class);
                            StudentItem studentItem = new StudentItem();
                            studentItem.setMaSv(student.getMaSv());
                            studentItem.setHo(student.getHo());
                            studentItem.setTen(student.getTen());
                            intent.putExtra("studentItem",studentItem);
                            intent.putExtra("scoreItemLv", new ArrayList<>());
                            intent.putExtra("crtSemester", 0);
                        }
                        intent.putExtra("listSemesterItemSpn", (ArrayList<SemesterItem>) data);
                        startActivity(intent);
                    }
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(HomeStudentActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<List<SemesterItem>>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(HomeStudentActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void setActivities(List<SemesterItem> data) {
        Optional<SemesterItem> semesterItem = data.stream()
                .filter(item -> item.getTimeStudyBegin().compareTo(today) <= 0 && item.getTimeStudyEnd().compareTo(today) >= 0)
                .findAny();
        if (!semesterItem.isPresent() || dateOfWeek == 1) {
            tvNotActivity.setText("Không có hoạt động nào");
        } else {
            int week = FormatterDate.getWeek(semesterItem.get().getTimeStudyBegin(), today);
            MyPrefs myPrefs = MyPrefs.getInstance();
            String jwt = myPrefs.getString(HomeStudentActivity.this, "jwt", "");
            String code = myPrefs.getString(HomeStudentActivity.this, "username", "");
            ApiManager apiManager = ApiManager.getInstance();
            Call<ResponseObject<List<TimeTableItem>>> call = apiManager.getApiService().getTimeTableStudentByWeek(jwt, code, week);
            call.enqueue(new Callback<ResponseObject<List<TimeTableItem>>>() {
                @Override
                public void onResponse(@NonNull Call<ResponseObject<List<TimeTableItem>>> call, @NonNull Response<ResponseObject<List<TimeTableItem>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ResponseObject<List<TimeTableItem>> resData = response.body();
                        List<ActivityItem> todayActivity = resData.getRetObj().get(dateOfWeek - 2).getTkbDtoList();
                        if (todayActivity == null || todayActivity.size() == 0) {
                            tvNotActivity.setText("Không có hoạt động nào");
                        } else {
                            tvNotActivity.setVisibility(View.GONE);
                            adapterActivity = new ActivityAdapter(HomeStudentActivity.this, R.layout.item_activity, (ArrayList<ActivityItem>) todayActivity);
                            lvActivity.setAdapter(adapterActivity);
                            adapterActivity.notifyDataSetChanged();
                        }
                    } else {
                        if (response.errorBody() != null) {
                            ResponseObject<Object> errorResponse = new Gson().fromJson(
                                    response.errorBody().charStream(),
                                    new TypeToken<ResponseObject<Object>>() {
                                    }.getType()
                            );
                            new CustomDialog.BuliderOKDialog(HomeStudentActivity.this)
                                    .setMessage("Lỗi" + errorResponse.getMessage())
                                    .setSuccessful(false)
                                    .build()
                                    .show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseObject<List<TimeTableItem>>> call, @NonNull Throwable t) {
                    new CustomDialog.BuliderOKDialog(HomeStudentActivity.this)
                            .setMessage("Lỗi kết nối! " + t.getMessage())
                            .setSuccessful(false)
                            .build()
                            .show();
                }
            });
        }
    }

    private void callActivityInforStudent() {
        Intent intent = new Intent(HomeStudentActivity.this, UserInforActivity.class);
        intent.putExtra("student", student);
        intent.putExtra("facultyName", facultyItem.getTenKhoa());
        startActivity(intent);
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

    private void getKhoaInfor(String maKhoa) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(HomeStudentActivity.this, "jwt", "");
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
                        new CustomDialog.BuliderOKDialog(HomeStudentActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<List<FacultyItem>>>> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Lỗi kết nối dữ liệu!", Toast.LENGTH_LONG).show();
            }
        });
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
        btnKhoa = findViewById(R.id.btnKhoa);
        btnThoiKhoaBieu = findViewById(R.id.btnTKB);
        btnDiem = findViewById(R.id.btnDiem);
        btnDangKy = findViewById(R.id.btnDKHP);
        lvActivity = findViewById(R.id.lvSchedule);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerlayout);
        tvThu = findViewById(R.id.tvThu);
        tvNotActivity = findViewById(R.id.NotActivity);
    }
}