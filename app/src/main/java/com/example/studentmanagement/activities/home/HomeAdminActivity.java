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
import com.example.studentmanagement.activities.faculty.MainFacultyActivity;
import com.example.studentmanagement.activities.authen.LoginActivity;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.view.FacultyItemLv;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.MyFuncButton;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.android.material.navigation.NavigationView;

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
            }
            else{
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId())
            {
                case  R.id.nav_ChangePassword:
                    startActivity(new Intent(HomeAdminActivity.this, ChangePasswordActivity.class));
                    break;
                case  R.id.navLogOut:
                    startActivity(new Intent(HomeAdminActivity.this, LoginActivity.class));
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        btnKhoa.setOnClickListener(v -> callKhoa(MyFuncButton.ADMIN_FACULTY_MAGNAGEMENT));

//        btnLop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callKhoa(1);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        });
//        btnGiangVien.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callKhoa(2);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        });
//        btnSinhVien.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callLop(0);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        });
//        btnHocPhan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callKhoa(3);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        });
//        btnLopTinChi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callLop(1);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        });
//        btnDiem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callLop(2);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        });
//        btnThongke.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callLop(3);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        });
//    }
//
//    private void disableInforInNav() {
//        navigationView.getMenu().getItem(0).setVisible(false);
//    }
//
//    private void callLop(int i) {
//        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);;
//        String jwt = sharedPreferences.getString("jwt", "");
//        ApiManager apiManager = ApiManager.getInstance();
//        Call<ResponseObject<List<List<Lop>>>> call = apiManager.getApiService().getAllLop(jwt);
//        call.enqueue(new Callback<ResponseObject<List<List<Lop>>>>() {
//            @Override
//            public void onResponse(@NonNull Call<ResponseObject<List<List<Lop>>>> call, @NonNull Response<ResponseObject<List<List<Lop>>>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ResponseObject<List<List<Lop>>> jwtResponse = response.body();
//                    List<Lop> data = jwtResponse.getRetObj().get(0);
//                    ArrayList<LopItemSpinner> lopItemSpinners = new ArrayList<>();
//                    for (Lop lop: data) {
//                        LopItemSpinner lopItemSpinner = new LopItemSpinner(lop.getMaLop(), lop.getTenLop());
//                        lopItemSpinners.add(lopItemSpinner);
//                    }
//                    Intent intent;
//                    if(i==0){
//                        intent = new Intent(HomeAdminActivity.this, SinhVienActivity.class);
//                        intent.putExtra("listLopItemSpinner", lopItemSpinners);
//                        startActivity(intent);
//                    }
//                    else {
//                        callKeHoach(lopItemSpinners, i);
//                    }
//                } else {
//                    Toast.makeText(getApplicationContext(), "Lỗi!", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<ResponseObject<List<List<Lop>>>> call, @NonNull Throwable t) {
//                Toast.makeText(getApplicationContext(), "Lỗi kết nối dữ liệu!", Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    private void callKeHoach(ArrayList<LopItemSpinner> lopItemSpinners, int i) {
//        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);;
//        String jwt = sharedPreferences.getString("jwt", "");
//        ApiManager apiManager = ApiManager.getInstance();
//        Call<ResponseObject<List<List<KyItemSpinner>>>> call = apiManager.getApiService().getAllKeHoach(jwt);
//        call.enqueue(new Callback<ResponseObject<List<List<KyItemSpinner>>>>() {
//            @Override
//            public void onResponse(@NonNull Call<ResponseObject<List<List<KyItemSpinner>>>> call, @NonNull Response<ResponseObject<List<List<KyItemSpinner>>>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ResponseObject<List<List<KyItemSpinner>>> jwtResponse = response.body();
//                    List<KyItemSpinner> data = jwtResponse.getRetObj().get(0);
//                    Intent intent;
//                    if(i==1){
//                        intent = new Intent(HomeAdminActivity.this, LopTinChiActivity.class);
//                    }
//                    else if(i==2){
//                        intent = new Intent(HomeAdminActivity.this, DiemAdminActivity.class);
//                    }
//                    else{
//                        intent = new Intent(HomeAdminActivity.this, ThongKeActivity.class);
//                        Toast.makeText(getApplicationContext(), "Not Ok" + i, Toast.LENGTH_LONG).show();
//                    }
//                    intent.putExtra("listLopItemSpinner", lopItemSpinners);
//                    intent.putExtra("listKyItemSpinner", (ArrayList<KyItemSpinner>) data);
//                    startActivity(intent);
//
//                } else {
//                    Toast.makeText(getApplicationContext(), "Lỗi!", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<ResponseObject<List<List<KyItemSpinner>>>> call, @NonNull Throwable t) {
//                Toast.makeText(getApplicationContext(), "Lỗi kết nối dữ liệu!", Toast.LENGTH_LONG).show();
//            }
//        });
    }

    private void callKhoa(MyFuncButton myFuncButton) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(HomeAdminActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<List<FacultyItemLv>>>> call = apiManager.getApiService().getAllFaculty(jwt);
        call.enqueue(new Callback<ResponseObject<List<List<FacultyItemLv>>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<List<FacultyItemLv>>>> call, @NonNull Response<ResponseObject<List<List<FacultyItemLv>>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<List<FacultyItemLv>>> resData = response.body();
                    List<FacultyItemLv> data = resData.getRetObj().get(0);
                    Intent intent = null;
                    if(myFuncButton == MyFuncButton.ADMIN_FACULTY_MAGNAGEMENT) { // Khoa
                        intent = new Intent(HomeAdminActivity.this, MainFacultyActivity.class);
                        intent.putExtra("listFacultyItemLv", (ArrayList<FacultyItemLv>) data);
                    }
//                    else{ //Giảngviên
//                        if(i==1) intent = new Intent(HomeAdminActivity.this, LopActivity.class);
//                        else if(i==2) intent = new Intent(HomeAdminActivity.this, GiangVienActivity.class);
//                        else intent = new Intent(HomeAdminActivity.this, HocPhanActivity.class);
//                        ArrayList<KhoaItemSpinner> listKhoaItemSpn = new ArrayList<>();
//                        for (Khoa khoa: data) {
//                            KhoaItemSpinner khoaItemSpinner = new KhoaItemSpinner(khoa.getMaKhoa(), khoa.getTenKhoa());
//                            listKhoaItemSpn.add(khoaItemSpinner);
//                        }
//                        intent.putExtra("listFaculty", listKhoaItemSpn);
//                    }
                    startActivity(intent);
                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<List<FacultyItemLv>>>> call, @NonNull Throwable t) {
//                Toast.makeText(getApplicationContext(), "Lỗi kết nối dữ liệu!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUpInforAdmin() {
        View viewHeader = navigationView.getHeaderView(0);
        TextView tvUN_nav = viewHeader.findViewById(R.id.tvUserName);
        TextView tvEmail_nav = viewHeader.findViewById(R.id.tvEmail);

        MyPrefs myPrefs = MyPrefs.getInstance();
        tvUsername.setText(myPrefs.getString(getApplicationContext(),"username",""));
        tvUserrole.setText(myPrefs.getString(getApplicationContext(),"role",""));
        tvUN_nav.setText(myPrefs.getString(getApplicationContext(),"username", ""));
        tvEmail_nav.setText(myPrefs.getString(getApplicationContext(),"email",""));
    }

    private void setActionBarDrawerToggle() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_open, R.string.navigation_close);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        actionBarDrawerToggle.getDrawerArrowDrawable().setGapSize(20);
        actionBarDrawerToggle.getDrawerArrowDrawable().setBarLength(100);
        actionBarDrawerToggle.getDrawerArrowDrawable().setBarThickness(15);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
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
        tvUsername=findViewById(R.id.tvUserName);
        tvUserrole=findViewById(R.id.tvUserRole);
        navigationView=findViewById(R.id.navigation_menu);
        btnKhoa=findViewById(R.id.btnKhoa);
        btnLop=findViewById(R.id.btnLop);
        btnGiangVien=findViewById(R.id.btnGiangVien);
        btnSinhVien=findViewById(R.id.btnSinhVien);
        btnHocPhan=findViewById(R.id.btnHocPhan);
        btnLopTinChi=findViewById(R.id.btnLopTinChi);
        btnDiem=findViewById(R.id.btnDiem);
        btnThongke=findViewById(R.id.btnThongKe);
        drawerLayout = findViewById(R.id.drawerlayout);
        toolbar = findViewById(R.id.toolbar);
    }
}