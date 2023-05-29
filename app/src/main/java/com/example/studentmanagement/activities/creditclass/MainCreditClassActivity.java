package com.example.studentmanagement.activities.creditclass;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivitySearch;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivitySearchAdd;
import com.example.studentmanagement.activities.lecturer.MainLecturerActivity;
import com.example.studentmanagement.activities.student.AddStudentActivity;
import com.example.studentmanagement.adapter.CreditClassAdapter;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.view.CreditClassItem;
import com.example.studentmanagement.models.view.PracticalClassItem;
import com.example.studentmanagement.models.view.SemesterItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainCreditClassActivity extends CustomAppCompactActivitySearch {
    ListView lvCreditClass;
    Button btnFilter;
    Toolbar toolbar;
    Spinner spnPracticalClass, spnSemester;
    String crtSemesterCode, crtPracticalClassCode;
    CreditClassAdapter creditClassAdapter;
    boolean enableCUD = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_credit_class);
        setControl();
        setEvent();
    }

    private void setEvent() {
        setSupportActionBar(toolbar);
        setDataSourceSemesterSpinner();
        setDataSourcePracticalClassSpinner();

        creditClassAdapter = new CreditClassAdapter(MainCreditClassActivity.this, R.layout.item_listview_credit_class);
        lvCreditClass.setAdapter(creditClassAdapter);

        spnSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SemesterItem semesterItem = (SemesterItem) adapterView.getItemAtPosition(i);
                crtSemesterCode = semesterItem.getMaKeHoach();
                enableCUD = i == 0 && System.currentTimeMillis() <= semesterItem.getTimeStudyEnd().getTime();
                creditClassAdapter.setEnableCUD(enableCUD);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spnPracticalClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PracticalClassItem practicalClassItem = (PracticalClassItem) adapterView.getItemAtPosition(i);
                crtPracticalClassCode = practicalClassItem.getMaLop();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnFilter.setOnClickListener(view -> callCreditClass());
    }

    private void callCreditClass() {
        ProgressDialog progressDialog = CustomDialog.LoadingDialog(MainCreditClassActivity.this,"Loading...");
        progressDialog.show();
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(MainCreditClassActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<CreditClassItem>>> call = apiManager.getApiService().getAllCreditClassByPracticalClass(jwt, crtSemesterCode, crtPracticalClassCode);
        call.enqueue(new Callback<ResponseObject<List<CreditClassItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<CreditClassItem>>> call, @NonNull Response<ResponseObject<List<CreditClassItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<CreditClassItem>> resData = response.body();
                    creditClassAdapter.clear();
                    if(resData.getRetObj()==null || resData.getRetObj().size()==0)
                        Toast.makeText(MainCreditClassActivity.this, "Không có lớp tín chỉ nào", Toast.LENGTH_LONG).show();
                    else creditClassAdapter.addAll(resData.getRetObj());
                    progressDialog.dismiss();
                    creditClassAdapter.notifyDataSetChanged();
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(MainCreditClassActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<CreditClassItem>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(MainCreditClassActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void setDataSourcePracticalClassSpinner() {
        List<PracticalClassItem> practicalClassItemList = (List<PracticalClassItem>) getIntent().getSerializableExtra("listPracticalClassItemSpn");
        ArrayAdapter<PracticalClassItem> practicalClassAdapter = new ArrayAdapter<>(MainCreditClassActivity.this, R.layout.item_selected_spinner, practicalClassItemList);
        practicalClassAdapter.setDropDownViewResource(R.layout.item_dropdown_spinner);
        spnPracticalClass.setAdapter(practicalClassAdapter);
        spnPracticalClass.setDropDownWidth(spnPracticalClass.getWidth());
    }

    private void setDataSourceSemesterSpinner() {
        List<SemesterItem> semesterItemList = (List<SemesterItem>) getIntent().getSerializableExtra("listSemesterItemSpn");
        ArrayAdapter<SemesterItem> adapterSemesterSpinner = new ArrayAdapter<>(MainCreditClassActivity.this, R.layout.item_selected_spinner, semesterItemList);
        adapterSemesterSpinner.setDropDownViewResource(R.layout.item_dropdown_spinner);
        spnSemester.setAdapter(adapterSemesterSpinner);
        spnSemester.setDropDownWidth(spnSemester.getWidth());
    }

    private void setControl() {
        toolbar = findViewById(R.id.toolbar);
        spnPracticalClass = findViewById(R.id.spnListLop);
        spnSemester = findViewById(R.id.spnListKy);
        btnFilter = findViewById(R.id.btnLoc);
        lvCreditClass = findViewById(R.id.lvDsloptinchi);

    }
}