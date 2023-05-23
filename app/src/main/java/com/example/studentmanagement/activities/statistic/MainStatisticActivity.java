package com.example.studentmanagement.activities.statistic;

import android.os.Bundle;
import android.view.Menu;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivitySearch;
import com.example.studentmanagement.adapter.CreditClassForStatisticAdapter;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.view.CourseItem;
import com.example.studentmanagement.models.view.CreditClassItem;
import com.example.studentmanagement.models.view.PracticalClassItem;
import com.example.studentmanagement.models.view.SemesterItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainStatisticActivity extends CustomAppCompactActivitySearch {
    Toolbar toolbar;
    Spinner spnPracticalClass, spnSemester, spnFilter;
    ArrayAdapter<PracticalClassItem> adapterPracticalClassSpinner;
    ArrayAdapter<SemesterItem> adapterSemesterSpinner;
    ArrayAdapter<CharSequence> adapterFilterSpinner;
    Button btnFilter;
    String crtSchemeCode, crtPracticalClassCode;
    CreditClassForStatisticAdapter creditClassForStatisticAdapter;
    ListView lvCreditClass;
    int filterMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_statistic_admin);
        setControl();
        setEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setDataSourceFilterSpinner();
        setDataSourceSemesterSpinner();
        spnPracticalClass.setDropDownWidth(spnPracticalClass.getWidth());
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

    private void setDataSourceFilterSpinner() {
        adapterFilterSpinner =  ArrayAdapter.createFromResource(this, R.array.filter_credit_class, R.layout.item_selected_spinner);
        adapterFilterSpinner.setDropDownViewResource(R.layout.item_dropdown_spinner);
        spnFilter.setAdapter(adapterFilterSpinner);
        spnFilter.setDropDownWidth(spnFilter.getWidth());
    }

    private void setDataSourceSemesterSpinner() {
        List<SemesterItem> semesterItemList = (List<SemesterItem>) getIntent().getSerializableExtra("listSemesterItemSpn");
        adapterSemesterSpinner = new ArrayAdapter<>(MainStatisticActivity.this, R.layout.item_selected_spinner, semesterItemList);
        adapterSemesterSpinner.setDropDownViewResource(R.layout.item_dropdown_spinner);
        spnSemester.setAdapter(adapterSemesterSpinner);
        spnSemester.setDropDownWidth(spnSemester.getWidth());
    }


    private void setEvent() {
        setSupportActionBar(toolbar);

        creditClassForStatisticAdapter = new CreditClassForStatisticAdapter(MainStatisticActivity.this, R.layout.item_listview_credit_class_statistic);
        lvCreditClass.setAdapter(creditClassForStatisticAdapter);

        spnSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SemesterItem semesterItem = (SemesterItem) adapterView.getItemAtPosition(i);
                crtSchemeCode = semesterItem.getMaKeHoach();
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

        spnFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filterMode = i;
                if(filterMode == 0) calAllPracticalClass();
                else callAllCourse();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void callAllCourse() {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(MainStatisticActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<List<CourseItem>>>> call = apiManager.getApiService().getAllCourse(jwt);
        call.enqueue(new Callback<ResponseObject<List<List<CourseItem>>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<List<CourseItem>>>> call, @NonNull Response<ResponseObject<List<List<CourseItem>>>> response) {
                if(response.isSuccessful()&& response.body()!=null){
                    List<CourseItem> data = response.body().getRetObj().get(0);

                    //Chuyển đổi từ học phần qua lớp
                    List<PracticalClassItem> practicalClassItemList = data.stream()
                            .map(courseItem -> new PracticalClassItem(courseItem.getMaMh(), courseItem.getTenMh()))
                            .collect(Collectors.toList());

                    adapterPracticalClassSpinner = new ArrayAdapter<>(MainStatisticActivity.this, R.layout.item_selected_spinner, practicalClassItemList);
                    adapterPracticalClassSpinner.setDropDownViewResource(R.layout.item_dropdown_spinner);
                    spnPracticalClass.setAdapter(adapterPracticalClassSpinner);
                }
                else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(MainStatisticActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<List<CourseItem>>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(MainStatisticActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void calAllPracticalClass() {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(MainStatisticActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<List<PracticalClassItem>>>> call = apiManager.getApiService().getAllPracticalClass(jwt);
        call.enqueue(new Callback<ResponseObject<List<List<PracticalClassItem>>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<List<PracticalClassItem>>>> call, @NonNull Response<ResponseObject<List<List<PracticalClassItem>>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<List<PracticalClassItem>>> resData = response.body();
                    List<PracticalClassItem> data = resData.getRetObj().get(0);
                    adapterPracticalClassSpinner = new ArrayAdapter<>(MainStatisticActivity.this, R.layout.item_selected_spinner, data);
                    adapterPracticalClassSpinner.setDropDownViewResource(R.layout.item_dropdown_spinner);
                    spnPracticalClass.setAdapter(adapterPracticalClassSpinner);
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(MainStatisticActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<List<PracticalClassItem>>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(MainStatisticActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }
    private void callCreditClass() {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(MainStatisticActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<CreditClassItem>>> call = filterMode == 0 ?
                apiManager.getApiService().getAllCreditClassByPracticalClass(jwt, crtSchemeCode, crtPracticalClassCode) :
                apiManager.getApiService().getAllCreditClassByCourseCode(jwt, crtSchemeCode, crtPracticalClassCode);
        call.enqueue(new Callback<ResponseObject<List<CreditClassItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<CreditClassItem>>> call, @NonNull Response<ResponseObject<List<CreditClassItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<CreditClassItem>> resData = response.body();

                    creditClassForStatisticAdapter.clear();
                    creditClassForStatisticAdapter.addAll(resData.getRetObj());
                    creditClassForStatisticAdapter.notifyDataSetChanged();
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(MainStatisticActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<CreditClassItem>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(MainStatisticActivity.this)
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
        lvCreditClass = findViewById(R.id.lvDsloptinchi);
        spnFilter = findViewById(R.id.spnListLoc);
    }
}