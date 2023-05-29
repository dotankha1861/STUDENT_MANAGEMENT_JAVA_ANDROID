package com.example.studentmanagement.activities.course;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivitySearchAdd;
import com.example.studentmanagement.adapter.CourseAdapter;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.entity.Course;
import com.example.studentmanagement.models.view.CourseItem;
import com.example.studentmanagement.models.view.FacultyItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainCourseActivity extends CustomAppCompactActivitySearchAdd {
    Toolbar toolbar;
    Spinner spnFaculty;
    CourseAdapter courseAdapter;
    ListView lvCourse;
    String crtFacultyCode;

    ActivityResultLauncher<Intent> mCreateHocPhanLauncher, mUpdateHocPhanLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_course);
        setControl();
        setEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setDataSourceFacultySpinner();
        if(!super.onCreateOptionsMenu(menu)) return false;
        getSearchView().setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                courseAdapter.setSearchQuery(s);
                courseAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itAdd:
                callAddHP();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callAddHP() {
        Intent intent = new Intent(MainCourseActivity.this, AddCourseActivity.class);
        intent.putExtra("crtFacultyCode", crtFacultyCode);
        mCreateHocPhanLauncher.launch(intent);
    }


    private void setEvent() {
        setSupportActionBar(toolbar);
        courseAdapter = new CourseAdapter(MainCourseActivity.this, R.layout.item_listview_course);
        lvCourse.setAdapter(courseAdapter);

        spnFaculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                FacultyItem facultyItem = (FacultyItem) adapterView.getItemAtPosition(i);
                crtFacultyCode = facultyItem.getMaKhoa();
                if(getSearchItem()!=null) getSearchItem().collapseActionView();
                setListView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mCreateHocPhanLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Course course = (Course) result.getData().getSerializableExtra("newCourse");
                        CourseItem courseItem = new CourseItem();
                        courseItem.setId(course.getId());
                        courseItem.setMaMh(course.getMaMh());
                        courseItem.setTenMh(course.getTenMh());
                        courseAdapter.insert(courseItem, 0);
                        courseAdapter.notifyDataSetChanged();

                        new CustomDialog.BuliderOKDialog(MainCourseActivity.this)
                                .setMessage("Thêm thành công")
                                .setSuccessful(true)
                                .build()
                                .show();
                    }
                }
        );
        mUpdateHocPhanLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Course course = (Course) result.getData().getSerializableExtra("changedCourse");
                        CourseItem courseItem = new CourseItem();
                        courseItem.setTenMh(course.getTenMh());
                        courseItem.setId(course.getId());
                        courseItem.setMaMh(course.getMaMh());
                        courseAdapter.setItem(courseItem, courseAdapter.getPosition(courseItem));
                        courseAdapter.notifyDataSetChanged();

                        new CustomDialog.BuliderOKDialog(MainCourseActivity.this)
                                .setMessage("Lưu thành công")
                                .setSuccessful(true)
                                .build()
                                .show();
                    }
                }
        );
        courseAdapter.setmUpdateHocPhanLauncher(mUpdateHocPhanLauncher);
    }

    private void setListView() {
        ProgressDialog progressDialog = CustomDialog.LoadingDialog(MainCourseActivity.this, "Loading...");
        progressDialog.show();
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(MainCourseActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<CourseItem>>> call = apiManager.getApiService().getAllCourseByFacultyCode(jwt, crtFacultyCode);
        call.enqueue(new Callback<ResponseObject<List<CourseItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<CourseItem>>> call, @NonNull Response<ResponseObject<List<CourseItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<CourseItem>> jwtResponse = response.body();
                    courseAdapter.clear();
                    if(jwtResponse.getRetObj()==null || jwtResponse.getRetObj().size()==0)
                        Toast.makeText(MainCourseActivity.this, "Khoa chưa có học phần nào", Toast.LENGTH_LONG).show();
                    else courseAdapter.addAll(jwtResponse.getRetObj());
                    progressDialog.dismiss();
                    courseAdapter.notifyDataSetChanged();
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(MainCourseActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<CourseItem>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(MainCourseActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void setDataSourceFacultySpinner() {
        List<FacultyItem> facultyItemList = (List<FacultyItem>) getIntent().getSerializableExtra("listFacultyItemSpn");

        ArrayAdapter<FacultyItem> facultyAdapter = new ArrayAdapter<>(MainCourseActivity.this, R.layout.item_selected_spinner, facultyItemList);
        facultyAdapter.setDropDownViewResource(R.layout.item_dropdown_spinner);

        spnFaculty.setAdapter(facultyAdapter);
        spnFaculty.setDropDownWidth(spnFaculty.getWidth());

    }

    private void setControl() {
        toolbar = findViewById(R.id.toolbar);
        spnFaculty = findViewById(R.id.spnListKhoa);
        lvCourse = findViewById(R.id.lvDshp);
    }
}