package com.example.studentmanagement.activities.notification;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivitySearchAdd;
import com.example.studentmanagement.activities.score.ViewScoreCreditClassActivity;
import com.example.studentmanagement.adapter.NotificationForCreditClassAdapter;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ERole;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.firebase.NotificationData;
import com.example.studentmanagement.firebase.NotificationSender;
import com.example.studentmanagement.models.responsebody.ScoreCreditClass;
import com.example.studentmanagement.models.view.CreditClassItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewNotificationActivity extends CustomAppCompactActivitySearchAdd {
    Toolbar toolbar;
    ListView lvNotification;
    NotificationForCreditClassAdapter notificationForCreditClassAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_notification_credit_class);
        setControl();
        setEnvent();
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itAdd:
                showDialog();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        new CustomDialog.Builder(ViewNotificationActivity.this)
                .setMessage("GỬI THÔNG BÁO")
                .setEdtTitle("Tiêu đề","")
                .setEdtBody("Nội dung", "")
                .setPositiveButton("Gửi", this::SendNotification, dismiss -> true)
                .setNegativeButton("Hủy", null, dismiss -> true)
                .build()
                .show();
    }

    private void SendNotification(View view) {
        ProgressDialog progressDialog = CustomDialog.LoadingDialog(ViewNotificationActivity.this, "Đang gửi thông báo ...");
        progressDialog.show();
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(ViewNotificationActivity.this, "jwt", "");
        CreditClassItem creditClassItem = (CreditClassItem) getIntent().getSerializableExtra("creditClass");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<ScoreCreditClass>>> call = apiManager.getApiService().getScoreByCreditClassCode(jwt, creditClassItem.getMaLopTc());
        call.enqueue(new Callback<ResponseObject<List<ScoreCreditClass>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<ScoreCreditClass>>> call, @NonNull Response<ResponseObject<List<ScoreCreditClass>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<ScoreCreditClass>> responseObject = response.body();
                    List<ScoreCreditClass> data = responseObject.getRetObj();
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReference("tokens");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            LinearLayout lnlParent = (LinearLayout) view.getParent().getParent();
                            EditText edtTitle = lnlParent.findViewById(R.id.editText);
                            EditText edtBody = lnlParent.findViewById(R.id.edtBody);
                            CreditClassItem creditClassItem = (CreditClassItem) getIntent().getSerializableExtra("creditClass");
                            NotificationData notificationData = new NotificationData(edtTitle.getText().toString(), edtBody.getText().toString(), System.currentTimeMillis());
                            NotificationSender.sendNotification(creditClassItem.getMaLopTc(),
                                    myPrefs.getString(ViewNotificationActivity.this,"userFullName",""), notificationData,
                                    data.stream().map(item -> dataSnapshot.child(item.getMaSv()).getValue(String.class))
                                            .collect(Collectors.toList()));
                            progressDialog.dismiss();
                            new CustomDialog.BuliderOKDialog(ViewNotificationActivity.this)
                                    .setMessage("Gửi thông báo thành công")
                                    .setSuccessful(true)
                                    .build()
                                    .show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            progressDialog.dismiss();
                            new CustomDialog.BuliderOKDialog(ViewNotificationActivity.this)
                                    .setMessage("Lỗi! Không gửi được thông báo")
                                    .setSuccessful(false)
                                    .build()
                                    .show();
                        }
                    });
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        progressDialog.dismiss();
                        new CustomDialog.BuliderOKDialog(ViewNotificationActivity.this)
                                .setMessage("Lỗi! Không gửi được thông báo")
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<ScoreCreditClass>>> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new CustomDialog.BuliderOKDialog(ViewNotificationActivity.this)
                        .setMessage("Lỗi! Không gửi được thông báo")
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!super.onCreateOptionsMenu(menu)) return false;
        MyPrefs myPrefs = MyPrefs.getInstance();
        ERole eRole = ERole.valueOf(myPrefs.getString(ViewNotificationActivity.this, "role",""));
        if(eRole == ERole.SINHVIEN)  menu.findItem(R.id.itAdd).setVisible(false);
        getSearchView().setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                notificationForCreditClassAdapter.setSearchQuery(s);
                notificationForCreditClassAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;
    }
    private void setEnvent() {
        setSupportActionBar(toolbar);
        notificationForCreditClassAdapter = new NotificationForCreditClassAdapter(ViewNotificationActivity.this,R.layout.item_listview_notification_credit_class);
        lvNotification.setAdapter(notificationForCreditClassAdapter);
        CreditClassItem creditClassItem = (CreditClassItem) getIntent().getSerializableExtra("creditClass");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("creditClasses/" + creditClassItem.getMaLopTc() + "/notifications");
        databaseReference.orderByChild("prioritySort").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<NotificationData> notificationDataList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NotificationData notificationData = new NotificationData();
                    notificationData.setTitle(snapshot.child("title").getValue(String.class));
                    notificationData.setBody(snapshot.child("body").getValue(String.class));
                    notificationData.setTimeStamp(snapshot.child("timeStamp").getValue(Long.class));
                    MyPrefs myPrefs = MyPrefs.getInstance();
                    databaseReference.child(snapshot.getKey()).child("isRead").child(myPrefs.getString(ViewNotificationActivity.this, "username",""))
                                    .setValue("X");
                    notificationDataList.add(notificationData);
                }

                notificationForCreditClassAdapter.clear();
                notificationForCreditClassAdapter.addAll(notificationDataList);
                notificationForCreditClassAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }

    private void setControl() {
        toolbar = findViewById(R.id.toolbar);
        lvNotification = findViewById(R.id.lvNotificaton);
    }
}
