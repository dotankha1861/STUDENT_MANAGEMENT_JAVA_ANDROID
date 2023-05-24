package com.example.studentmanagement.activities.enrollcourse;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;


import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivitySearch;
import com.example.studentmanagement.adapter.CreditClassForEnrollCourseAdapter;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.entity.Student;
import com.example.studentmanagement.models.view.CourseItem;
import com.example.studentmanagement.models.view.EnrollCourseItem;
import com.example.studentmanagement.models.view.PracticalClassItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.MyPrefs;
import com.example.studentmanagement.utils.StatusEnroll;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnrollCourseActivity extends CustomAppCompactActivitySearch {
    int filterMode;
    Button btnFilter, btnListChoseCourse;
    Toolbar toolbar;
    Spinner spnPracticalClass,  spnFilter;
    ArrayAdapter<PracticalClassItem> adapterPracticalClassSpinner;
    ArrayAdapter<CharSequence> adapterFilterSpinner;
    String practicalClassCode;
    CreditClassForEnrollCourseAdapter creditClassForEnrollCourseAdapter;
    ListView lvCreditClass;
    List<EnrollCourseItem> listChoseCourse = new ArrayList<>();
    ActivityResultLauncher<Intent> mCallListChoseCourseLauncher;
    List<EnrollCourseItem> enrollCourseItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_enroll_course);
        setControl();
        setEvent();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setDataSourceFilterSpinner();
        spnPracticalClass.setDropDownWidth(spnPracticalClass.getWidth());
        if(!super.onCreateOptionsMenu(menu)) return false;
        getSearchView().setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                creditClassForEnrollCourseAdapter.setSearchQuery(s);
                creditClassForEnrollCourseAdapter.notifyDataSetChanged();
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


    private void setEvent() {
        setSupportActionBar(toolbar);

        listChoseCourse.addAll((ArrayList<EnrollCourseItem>) getIntent().getSerializableExtra("listEnrolledCourse"));

        adapterPracticalClassSpinner = new ArrayAdapter<>(EnrollCourseActivity.this, android.R.layout.simple_spinner_item);
        adapterPracticalClassSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPracticalClass.setAdapter(adapterPracticalClassSpinner);

        creditClassForEnrollCourseAdapter = new CreditClassForEnrollCourseAdapter(EnrollCourseActivity.this, R.layout.item_listview_enroll_course);
        creditClassForEnrollCourseAdapter.setListChoseCourse(listChoseCourse);
        lvCreditClass.setAdapter(creditClassForEnrollCourseAdapter);

        spnPracticalClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PracticalClassItem practicalClassItem = (PracticalClassItem) adapterView.getItemAtPosition(i);
                practicalClassCode = practicalClassItem.getMaLop();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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

        btnFilter.setOnClickListener(view -> callCreditClass());

        btnListChoseCourse.setOnClickListener(view -> {
            Intent intent = new Intent(EnrollCourseActivity.this, ListChoseCourseActivity.class);
            intent.putExtra("listChoseCourse",
                    (ArrayList<EnrollCourseItem>) listChoseCourse.stream()
                    .peek(item -> {
                        item.setVisibleCT(false);
                        item.setChecked(item.getStatusEnroll()==StatusEnroll.DACHON);
                    })
                    .collect(Collectors.toList()));
            mCallListChoseCourseLauncher.launch(intent);
        });
        mCallListChoseCourseLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                       listChoseCourse = (ArrayList<EnrollCourseItem>) result.getData().getSerializableExtra("listChoseCourse");
                       creditClassForEnrollCourseAdapter.setListChoseCourse(listChoseCourse);
                       setItemsLv();
                    }
                }
        );
    }

    private void callAllCourse() {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(EnrollCourseActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<List<CourseItem>>>> call = apiManager.getApiService().getAllCourse(jwt);
        call.enqueue(new Callback<ResponseObject<List<List<CourseItem>>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<List<CourseItem>>>> call, @NonNull Response<ResponseObject<List<List<CourseItem>>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<List<CourseItem>>> resData = response.body();
                    List<CourseItem> courseItemList = resData.getRetObj().get(0);

                    //Chuyển đổi từ học phần qua lớp
                    List<PracticalClassItem> practicalClassItemList = courseItemList.stream()
                            .map(course -> new PracticalClassItem(course.getMaMh(), course.getTenMh()))
                            .collect(Collectors.toList());

                    adapterPracticalClassSpinner.clear();
                    adapterPracticalClassSpinner.addAll(practicalClassItemList);
                    adapterPracticalClassSpinner.notifyDataSetChanged();
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(EnrollCourseActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<List<CourseItem>>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(EnrollCourseActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void calAllPracticalClass() {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(EnrollCourseActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<List<PracticalClassItem>>>> call = apiManager.getApiService()
                .getAllPracticalClass(jwt);
        call.enqueue(new Callback<ResponseObject<List<List<PracticalClassItem>>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<List<PracticalClassItem>>>> call,
                                   @NonNull Response<ResponseObject<List<List<PracticalClassItem>>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<List<PracticalClassItem>>> resData = response.body();
                    List<PracticalClassItem> data = resData.getRetObj().get(0);

                    adapterPracticalClassSpinner.clear();
                    adapterPracticalClassSpinner.addAll(data);
                    adapterPracticalClassSpinner.notifyDataSetChanged();

                    PracticalClassItem practicalClassItem = new PracticalClassItem();
                    practicalClassItem.setMaLop(((Student) getIntent().getSerializableExtra("student")).getMaLop());
                    spnPracticalClass.setSelection(data.indexOf(practicalClassItem));
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(EnrollCourseActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<List<PracticalClassItem>>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(EnrollCourseActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void callCreditClass() {
        if(practicalClassCode == null) Toast.makeText(this, "Đang tải dữ liệu vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();;
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(EnrollCourseActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<EnrollCourseItem>>> call = filterMode == 0 ?
                apiManager.getApiService().getAllEnrollCourseByPracticalClass(jwt, null, practicalClassCode) :
                apiManager.getApiService().getAllEnrollCourseByCourseCode(jwt,null, practicalClassCode);
        call.enqueue(new Callback<ResponseObject<List<EnrollCourseItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<EnrollCourseItem>>> call, @NonNull Response<ResponseObject<List<EnrollCourseItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<EnrollCourseItem>> resData= response.body();
                    enrollCourseItemList = resData.getRetObj();
                    setItemsLv();
                } else {
                    Toast.makeText(getApplicationContext(), "Lỗi!" , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<EnrollCourseItem>>> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Lỗi kết nối dữ liệu! " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setItemsLv(){
        if(enrollCourseItemList == null) return;
        enrollCourseItemList = enrollCourseItemList.stream()
                .peek(item -> {
                    Optional<EnrollCourseItem> enrollCourseItem = listChoseCourse.stream()
                            .filter(courseItem -> courseItem.getMaLopTc().equals(item.getMaLopTc()))
                            .findAny();

                    item.setStatusEnroll(enrollCourseItem.isPresent()
                            ? enrollCourseItem.get().getStatusEnroll()
                            : StatusEnroll.CHUACHON);

                    item.setVisibleCT(false);
                })
                .collect(Collectors.toList());

        creditClassForEnrollCourseAdapter.clear();
        creditClassForEnrollCourseAdapter.addAll(enrollCourseItemList);
        creditClassForEnrollCourseAdapter.notifyDataSetChanged();
    }
    private void setControl() {
        toolbar = findViewById(R.id.toolbar);
        spnPracticalClass = findViewById(R.id.spnListLop);
        btnListChoseCourse = findViewById(R.id.btnDSDK);
        btnFilter = findViewById(R.id.btnLoc);
        spnFilter= findViewById(R.id.spnListLoc);
        lvCreditClass = findViewById(R.id.lvDsHp);
    }
}