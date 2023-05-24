package com.example.studentmanagement.activities.creditclass;

import android.annotation.SuppressLint;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivitySearchAdd;
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

public class MainCreditClassActivity extends CustomAppCompactActivitySearchAdd {
    ListView lvCreditClass;
    Button btnFilter;
    Toolbar toolbar;
    Spinner spnPracticalClass, spnSemester;
    String crtSemesterCode, crtPracticalClassCode;
    CreditClassAdapter creditClassAdapter;
    ActivityResultLauncher<Intent> mCreateCreditClassLauncher, mUpdateCreditClassLauncher;
    boolean enableCUD = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_credit_class);
        setControl();
        setEvent();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setDataSourceSemesterSpinner();
        setDataSourcePracticalClassSpinner();
        if(!super.onCreateOptionsMenu(menu)) return false;
        getSearchView().setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                creditClassAdapter.setSearchQuery(s);
                creditClassAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itAdd:
                CallAddCreditClass();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void CallAddCreditClass() {
        Intent intent = new Intent(MainCreditClassActivity.this, AddStudentActivity.class);
        intent.putExtra("crtPracticalClassCode", crtPracticalClassCode);
        mCreateCreditClassLauncher.launch(intent);
    }

    private void setEvent() {
        setSupportActionBar(toolbar);

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
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(MainCreditClassActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<CreditClassItem>>> call = apiManager.getApiService().getAllCreditClassByPracticalClass(jwt, crtSemesterCode, crtPracticalClassCode);
        call.enqueue(new Callback<ResponseObject<List<CreditClassItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<CreditClassItem>>> call, @NonNull Response<ResponseObject<List<CreditClassItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<CreditClassItem>> resData = response.body();

                    toolbar.findViewById(R.id.itAdd).setEnabled(false);
                    creditClassAdapter.clear();
                    creditClassAdapter.addAll(resData.getRetObj());
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