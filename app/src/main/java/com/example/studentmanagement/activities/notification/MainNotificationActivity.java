package com.example.studentmanagement.activities.notification;

import android.app.role.RoleManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivitySearch;
import com.example.studentmanagement.activities.score.MainScoreLecturerActivity;
import com.example.studentmanagement.adapter.CreditClassForNotificationAdapter;
import com.example.studentmanagement.adapter.CreditClassForScoreAdapter;
import com.example.studentmanagement.api.ERole;
import com.example.studentmanagement.firebase.NotificationData;
import com.example.studentmanagement.models.responsebody.ScoreStudent;
import com.example.studentmanagement.models.view.CreditClassItem;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainNotificationActivity extends CustomAppCompactActivitySearch {

    Toolbar toolbar;
    ListView lvCreditClass;
    CreditClassForNotificationAdapter creditClassForNotificationAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_notification_lecturer);
        setControl();
        setEvent();
    }

    private void setEvent() {
        setSupportActionBar(toolbar);

        MyPrefs myPrefs = MyPrefs.getInstance();
        creditClassForNotificationAdapter= new CreditClassForNotificationAdapter(MainNotificationActivity.this, R.layout.item_listview_credit_class_notification);
        ERole role = ERole.valueOf(myPrefs.getString(MainNotificationActivity.this,"role",""));
        creditClassForNotificationAdapter.seteRole(role);
        if(role == ERole.GIANGVIEN){
            List<CreditClassItem> creditClassItemList = (List<CreditClassItem>)  getIntent().getSerializableExtra("listCreditClass");
            creditClassForNotificationAdapter.addAll(creditClassItemList);
            creditClassForNotificationAdapter.setLecturerName(myPrefs.getString(MainNotificationActivity.this,"userFullName",""));
            lvCreditClass.setAdapter(creditClassForNotificationAdapter);
            creditClassForNotificationAdapter.notifyDataSetChanged();
        }
        else{
            List<ScoreStudent> scoreStudentList = (List<ScoreStudent>) getIntent().getSerializableExtra("listScoreStudent");
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("creditClasses");
            creditClassForNotificationAdapter.setLecturerName(myPrefs.getString(MainNotificationActivity.this,"username",""));
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<CreditClassItem> creditClassItemList = scoreStudentList.stream()
                            .map(item -> {
                                CreditClassItem creditClassItem = new CreditClassItem();
                                creditClassItem.setMaLopTc(item.getMaLopTc());
                                creditClassItem.setTenMh(item.getTenMh());
                                creditClassItem.setTenGv(dataSnapshot.child(item.getMaLopTc()).child("lecturerName").getValue(String.class));
                                return creditClassItem;
                            })
                            .filter(item -> dataSnapshot.child(item.getMaLopTc()).exists())
                            .sorted((creditClassItem, t1) -> (int) (dataSnapshot.child(creditClassItem.getMaLopTc()).child("priority").getValue(Long.class)
                                        - dataSnapshot.child(t1.getMaLopTc()).child("priority").getValue(Long.class))
                            )
                            .collect(Collectors.toList());

                    creditClassForNotificationAdapter.clear();
                    creditClassForNotificationAdapter.addAll(creditClassItemList);
                    lvCreditClass.setAdapter(creditClassForNotificationAdapter);
                    creditClassForNotificationAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!super.onCreateOptionsMenu(menu)) return false;
        getSearchView().setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                creditClassForNotificationAdapter.setSearchQuery(s);
                creditClassForNotificationAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;
    }
    private void setControl() {
        toolbar = findViewById(R.id.toolbar);
        lvCreditClass = findViewById(R.id.lvDsloptinchi);
    }
}
