package com.example.studentmanagement.activities.statistic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivitySearch;
import com.example.studentmanagement.activities.score.MainScoreLecturerActivity;
import com.example.studentmanagement.adapter.CreditClassForStatisticAdapter;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.view.CreditClassItem;
import com.example.studentmanagement.models.view.SemesterItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainStatisticLecturerActivity extends CustomAppCompactActivitySearch {
    Toolbar toolbar;
    Spinner spnSemester;
    ArrayAdapter<SemesterItem> adapterSemesterSpinner;
    CreditClassForStatisticAdapter creditClassForStatisticAdapter;
    ListView lvCreditClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_statistic_lecturer);
        setControl();
        setEvent();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setDataSourceSemesterSpinner();
        if(!super.onCreateOptionsMenu(menu)) return false;
        getSearchView().setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                creditClassForStatisticAdapter.setSearchQuery(s);
                creditClassForStatisticAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;
    }

    private void setDataSourceSemesterSpinner() {
        List<SemesterItem> semesterItemList = (List<SemesterItem>) getIntent().getSerializableExtra("listSemesterItemSpn");
        adapterSemesterSpinner = new ArrayAdapter<>(MainStatisticLecturerActivity.this, R.layout.item_selected_spinner, semesterItemList);
        adapterSemesterSpinner.setDropDownViewResource(R.layout.item_dropdown_spinner);
        spnSemester.setAdapter(adapterSemesterSpinner);
        spnSemester.setDropDownWidth(spnSemester.getWidth());
    }

    private void setEvent() {
        setSupportActionBar(toolbar);

        MyPrefs myPrefs = MyPrefs.getInstance();
        creditClassForStatisticAdapter = new CreditClassForStatisticAdapter(MainStatisticLecturerActivity.this, R.layout.item_listview_credit_class_statistic);
        creditClassForStatisticAdapter.setLecturerName(myPrefs.getString(MainStatisticLecturerActivity.this,"userFullName",""));
        lvCreditClass.setAdapter(creditClassForStatisticAdapter);

        spnSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SemesterItem semesterItem = (SemesterItem) adapterView.getItemAtPosition(i);
                String semesterCode = semesterItem.getMaKeHoach();
                callCreditClass(semesterCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void callCreditClass(String semesterCode) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(MainStatisticLecturerActivity.this, "jwt", "");
        String lecturerCode = myPrefs.getString(MainStatisticLecturerActivity.this, "username", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<CreditClassItem>>> call = apiManager.getApiService().getAllCreditClassByLecturerCode(jwt, lecturerCode, semesterCode);
        call.enqueue(new Callback<ResponseObject<List<CreditClassItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<CreditClassItem>>> call, @NonNull Response<ResponseObject<List<CreditClassItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<CreditClassItem>> resData = response.body();

                    creditClassForStatisticAdapter.clear();
                    creditClassForStatisticAdapter.addAll(resData.getRetObj());
                    lvCreditClass.setAdapter(creditClassForStatisticAdapter);
                    creditClassForStatisticAdapter.notifyDataSetChanged();
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(MainStatisticLecturerActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<CreditClassItem>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(MainStatisticLecturerActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void setControl() {
        toolbar = findViewById(R.id.toolbar);
        spnSemester = findViewById(R.id.spnListKy);
        lvCreditClass = findViewById(R.id.lvDsloptinchi);
    }
}