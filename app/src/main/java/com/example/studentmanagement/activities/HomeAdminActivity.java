package com.example.studentmanagement.activities;


import android.os.Bundle;

import android.widget.Button;
import android.widget.TextView;



import androidx.appcompat.app.ActionBarDrawerToggle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.drawerlayout.widget.DrawerLayout;

import com.example.studentmanagement.R;
import com.google.android.material.navigation.NavigationView;


public class HomeAdminActivity extends AppCompatActivity {

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
//        setUpToolbar();
        setControl();
//        setEvent();
    }

//    private void setEvent() {
//        getAdminInfor();
//        disableInforInNav();
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//                    drawerLayout.closeDrawers();
//                }
//                else{
//                    drawerLayout.openDrawer(GravityCompat.START);
//                }
//            }
//        });
////
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                switch (menuItem.getItemId())
//                {
////                    case  R.id.navUserInfor:
////                        startActivity(new Intent(HomeAdminActivity.this, UserInforActivity.class));
////                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
////                        break;
////
//                    case  R.id.nav_ChangePassword:
//                        startActivity(new Intent(HomeAdminActivity.this, ChangePassword.class));
//                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//                        break;
////
////                    case  R.id.nav_Support:
////                        startActivity(new Intent(HomeAdminActivity.this, Support.class));
////                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
////                        break;
////
////                    case  R.id.navAppInfor:
////                        startActivity(new Intent(HomeAdminActivity.this, AppInfor.class));
////                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
////                        break;
////
//                    case  R.id.navLogOut:
//                        startActivity(new Intent(HomeAdminActivity.this, LoginActivity.class));
//                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//                        break;
//                }
//                drawerLayout.closeDrawer(GravityCompat.START);
//                return true;
//            }
//        });
////
//        btnKhoa.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callKhoa(0);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        });
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
//    }
//
//
//    private void callKhoa(int i) {
//        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);;
//        String jwt = sharedPreferences.getString("jwt", "");
//        ApiManager apiManager = ApiManager.getInstance();
//        Call<ResponseObject<List<List<Khoa>>>> call = apiManager.getApiService().getAllKhoa(jwt);
//        call.enqueue(new Callback<ResponseObject<List<List<Khoa>>>>() {
//            @Override
//            public void onResponse(@NonNull Call<ResponseObject<List<List<Khoa>>>> call, @NonNull Response<ResponseObject<List<List<Khoa>>>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ResponseObject<List<List<Khoa>>> jwtResponse = response.body();
//                    List<Khoa> data = jwtResponse.getRetObj().get(0);
//                    Intent intent;
//                    if(i == 0) { // Khoa
//                        intent = new Intent(HomeAdminActivity.this, KhoaActivity.class);
//                        intent.putExtra("listKhoa", (Serializable) data);
//                    }
//                    else{ //Giảngviên
//                        if(i==1) intent = new Intent(HomeAdminActivity.this, LopActivity.class);
//                        else if(i==2) intent = new Intent(HomeAdminActivity.this, GiangVienActivity.class);
//                        else intent = new Intent(HomeAdminActivity.this, HocPhanActivity.class);
//                        ArrayList<KhoaItemSpinner> listKhoaItemSpn = new ArrayList<>();
//                        for (Khoa khoa: data) {
//                            KhoaItemSpinner khoaItemSpinner = new KhoaItemSpinner(khoa.getMaKhoa(), khoa.getTenKhoa());
//                            listKhoaItemSpn.add(khoaItemSpinner);
//                        }
//                        intent.putExtra("listKhoaItemSpinner", listKhoaItemSpn);
//                    }
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(getApplicationContext(), "Lỗi!", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<ResponseObject<List<List<Khoa>>>> call, @NonNull Throwable t) {
//                Toast.makeText(getApplicationContext(), "Lỗi kết nối dữ liệu!", Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    private void getAdminInfor() {
//        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
//        tvUsername.setText(sharedPreferences.getString("username",""));
//        tvUserrole.setText(sharedPreferences.getString("role",""));
//        View viewHeader = navigationView.getHeaderView(0);
//        TextView tvUN_nav = viewHeader.findViewById(R.id.tvUserName);
//        TextView tvEmail_nav = viewHeader.findViewById(R.id.tvEmail);
//        tvUN_nav.setText(tvUsername.getText());
//        tvEmail_nav.setText(sharedPreferences.getString("email",""));
//    }
//
//    //
////
//    private void setUpToolbar() {
//        drawerLayout = findViewById(R.id.drawerlayout);
//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_open, R.string.navigation_close);
////        Drawable drawable=getResources().getDrawable(R.drawable.menu);
////        actionBarDrawerToggle.setDrawerArrowDrawable(R.drawable.baseline_account_circle_24);
//        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
//        actionBarDrawerToggle.getDrawerArrowDrawable().setGapSize(20);
//        actionBarDrawerToggle.getDrawerArrowDrawable().setBarLength(100);
//        actionBarDrawerToggle.getDrawerArrowDrawable().setBarThickness(15);
//        drawerLayout.addDrawerListener(actionBarDrawerToggle);
//        actionBarDrawerToggle.syncState();
//    }
//    public void onBackPressed(){
//        AlertDialog.Builder builder=new AlertDialog.Builder(this);
//        builder.setMessage("Bạn có muốn thoát ứng dụng không?")
//                .setCancelable(false)
//                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finishAffinity();
//                    }
//                })
//                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//        AlertDialog alertDialog=builder.create();
//        alertDialog.show();
//    }
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
    }
}