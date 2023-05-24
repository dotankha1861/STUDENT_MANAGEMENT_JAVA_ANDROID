package com.example.studentmanagement.activities.score;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivitySearch;
import com.example.studentmanagement.adapter.CreditClassForScoreAdapter;
import com.example.studentmanagement.adapter.StudentForScoreAdapter;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.view.CreditClassItem;
import com.example.studentmanagement.models.view.PracticalClassItem;
import com.example.studentmanagement.models.view.SemesterItem;
import com.example.studentmanagement.models.view.StudentItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainScoreAdminActivity extends CustomAppCompactActivitySearch {
    Toolbar toolbar;
    Spinner spnPracticalClass, spnSemester, spnViewMode;
    ArrayAdapter<PracticalClassItem> adapterPracticalClassSpinner;
    ArrayAdapter<SemesterItem> adapterSemesterSpinner;
    ArrayAdapter<CharSequence> adapterFilterSpinner;
    Button btnFilter;
    String crtSemesterCode, crtPracticalClassCode;
    CreditClassForScoreAdapter creditClassForScoreAdapter;
    StudentForScoreAdapter studentForScoreAdapter;
    ListView lvObject;
    int viewMode = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_score_admin);
        setControl();
        setEvent();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setDataSourceFilterSpinner();
        setDataSourceSemesterSpinner();
        setDataSourcsePracticalClassSpinner();
        spnPracticalClass.setDropDownWidth(spnPracticalClass.getWidth());
        if(!super.onCreateOptionsMenu(menu)) return false;
        getSearchView().setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(viewMode == 0){
                    creditClassForScoreAdapter.setSearchQuery(s);
                    creditClassForScoreAdapter.notifyDataSetChanged();
                }
                else{
                    studentForScoreAdapter.setSearchQuery(s);
                    studentForScoreAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        return true;
    }

    private void setDataSourcsePracticalClassSpinner() {
        List<PracticalClassItem> practicalClassItemList = (List<PracticalClassItem>) getIntent().getSerializableExtra("listPracticalClassItemSpn");
        adapterPracticalClassSpinner = new ArrayAdapter<>(MainScoreAdminActivity.this, R.layout.item_selected_spinner, practicalClassItemList);
        adapterPracticalClassSpinner.setDropDownViewResource(R.layout.item_dropdown_spinner);
        spnPracticalClass.setAdapter(adapterPracticalClassSpinner);
        spnPracticalClass.setDropDownWidth(spnPracticalClass.getWidth());
    }

    private void setDataSourceSemesterSpinner() {
        List<SemesterItem> semesterItemList = (List<SemesterItem>) getIntent().getSerializableExtra("listSemesterItemSpn");
        adapterSemesterSpinner = new ArrayAdapter<>(MainScoreAdminActivity.this, R.layout.item_selected_spinner, semesterItemList);
        adapterSemesterSpinner.setDropDownViewResource(R.layout.item_dropdown_spinner);
        studentForScoreAdapter.setSemesterItemList(semesterItemList);
        spnSemester.setAdapter(adapterSemesterSpinner);
        spnSemester.setDropDownWidth(spnSemester.getWidth());
    }

    private void setDataSourceFilterSpinner() {
        adapterFilterSpinner =  ArrayAdapter.createFromResource(this, R.array.view_mode, R.layout.item_selected_spinner);
        adapterFilterSpinner.setDropDownViewResource(R.layout.item_dropdown_spinner);
        spnViewMode.setAdapter(adapterFilterSpinner);
        spnViewMode.setDropDownWidth(spnViewMode.getWidth());
    }


    private void setEvent() {
        setSupportActionBar(toolbar);
        studentForScoreAdapter = new StudentForScoreAdapter(MainScoreAdminActivity.this, R.layout.item_listview_student_score);
        creditClassForScoreAdapter = new CreditClassForScoreAdapter(MainScoreAdminActivity.this, R.layout.item_listview_credit_class_score);

        spnSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SemesterItem semesterItem = (SemesterItem) adapterView.getItemAtPosition(i);
                crtSemesterCode = semesterItem.getMaKeHoach();
                studentForScoreAdapter.setCrtSemester(crtSemesterCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spnViewMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                viewMode = i;
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

        btnFilter.setOnClickListener((View.OnClickListener) view -> {
            if(viewMode == 0) callCreditClass();
            else callStudent();
        });
    }

    private void callStudent() {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(MainScoreAdminActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<StudentItem>>> call = apiManager.getApiService().getAllStudentByPracticalClassCode(jwt, crtPracticalClassCode);
        call.enqueue(new Callback<ResponseObject<List<StudentItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<StudentItem>>> call, @NonNull Response<ResponseObject<List<StudentItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<StudentItem>> resData = response.body();

                    studentForScoreAdapter.clear();
                    if(resData.getRetObj()==null || resData.getRetObj().size()==0)
                        Toast.makeText(MainScoreAdminActivity.this, "Lớp chưa có sinh viên nào", Toast.LENGTH_LONG).show();
                    else studentForScoreAdapter.addAll(resData.getRetObj());
                    lvObject.setAdapter(studentForScoreAdapter);
                    studentForScoreAdapter.notifyDataSetChanged();
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(MainScoreAdminActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<StudentItem>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(MainScoreAdminActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void callCreditClass() {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(MainScoreAdminActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<CreditClassItem>>> call = apiManager.getApiService().getAllCreditClassByPracticalClass(jwt, crtSemesterCode, crtPracticalClassCode);
        call.enqueue(new Callback<ResponseObject<List<CreditClassItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<CreditClassItem>>> call, @NonNull Response<ResponseObject<List<CreditClassItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<CreditClassItem>> resData = response.body();

                    creditClassForScoreAdapter.clear();
                    creditClassForScoreAdapter.addAll(resData.getRetObj());
                    lvObject.setAdapter(creditClassForScoreAdapter);
                    creditClassForScoreAdapter.notifyDataSetChanged();
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(MainScoreAdminActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<CreditClassItem>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(MainScoreAdminActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void setControl() {
        toolbar = findViewById(R.id.toolbar);
        spnPracticalClass = findViewById(R.id.spnListLop);
        spnSemester = findViewById(R.id.spnListKy);
        btnFilter = findViewById(R.id.btnLoc);
        lvObject = findViewById(R.id.lvDsloptinchi);
        spnViewMode = findViewById(R.id.spnListMode);
    }
}