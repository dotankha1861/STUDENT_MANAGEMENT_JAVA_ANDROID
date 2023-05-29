package com.example.studentmanagement.activities.student;

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

import com.example.studentmanagement.activities.lecturer.MainLecturerActivity;
import com.example.studentmanagement.adapter.StudentAdapter;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.entity.Student;
import com.example.studentmanagement.models.view.FacultyItem;
import com.example.studentmanagement.models.view.LecturerItem;
import com.example.studentmanagement.models.view.PracticalClassItem;
import com.example.studentmanagement.models.view.StudentItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainStudentActivity extends CustomAppCompactActivitySearchAdd {
    Toolbar toolbar;
    Spinner spnPracticalClass;
    StudentAdapter studentAdapter;
    ListView lvStudent;
    String crtPracticalClassCode;

    ActivityResultLauncher<Intent> mCreateSinhVienLauncher, mUpdateSinhVienLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_student);
        setControl();
        setEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setDataSourcePracticalClassSpinner();
        if(!super.onCreateOptionsMenu(menu)) return false;
        getSearchView().setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                studentAdapter.setSearchQuery(s);
                studentAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;
    }

    private void setDataSourcePracticalClassSpinner() {
        List<PracticalClassItem> practicalClassItemList = (List<PracticalClassItem>) getIntent().getSerializableExtra("listPracticalClassItemSpn");

        ArrayAdapter<PracticalClassItem> practicalClassAdapter = new ArrayAdapter<>(MainStudentActivity.this, R.layout.item_selected_spinner, practicalClassItemList);
        practicalClassAdapter.setDropDownViewResource(R.layout.item_dropdown_spinner);

        spnPracticalClass.setAdapter(practicalClassAdapter);
        spnPracticalClass.setDropDownWidth(spnPracticalClass.getWidth());

    }

    @SuppressLint("NonConstantResourceId")
    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itAdd:
                callAddStudent();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void callAddStudent() {
        Intent intent = new Intent(MainStudentActivity.this, AddStudentActivity.class);
        intent.putExtra("crtPracticalClassCode", crtPracticalClassCode);
        mCreateSinhVienLauncher.launch(intent);
    }


    private void setEvent() {
        setSupportActionBar(toolbar);
        studentAdapter = new StudentAdapter(MainStudentActivity.this, R.layout.item_listview_student);
        lvStudent.setAdapter(studentAdapter);

        spnPracticalClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PracticalClassItem practicalClassItem = (PracticalClassItem) adapterView.getItemAtPosition(i);
                crtPracticalClassCode = practicalClassItem.getMaLop();
                if(getSearchItem()!=null) getSearchItem().collapseActionView();
                setListView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mCreateSinhVienLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            Student student = (Student) result.getData().getSerializableExtra("newStudent");
                            StudentItem studentItem = new StudentItem();
                            studentItem.setId(student.getId());
                            studentItem.setMaSv(student.getMaSv());
                            studentItem.setHo(student.getHo());
                            studentItem.setTen(student.getTen());
                            studentItem.setPhai(student.getPhai());
                            studentItem.setHinhAnh(student.getHinhAnh());
                            studentAdapter.insert(studentItem, 0);
                            studentAdapter.notifyDataSetChanged();

                            new CustomDialog.BuliderOKDialog(MainStudentActivity.this)
                                    .setMessage("Thêm thành công")
                                    .setSuccessful(true)
                                    .build()
                                    .show();
                        }
                    }
                }
        );
        mUpdateSinhVienLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            Student student = (Student) result.getData().getSerializableExtra("changedStudent");
                            StudentItem studentItem = new StudentItem();
                            studentItem.setId(student.getId());
                            studentItem.setMaSv(student.getMaSv());
                            studentItem.setHo(student.getHo());
                            studentItem.setTen(student.getTen());
                            studentItem.setPhai(student.getPhai());
                            studentItem.setHinhAnh(student.getHinhAnh());
                            studentAdapter.setItem(studentItem, studentAdapter.getPosition(studentItem));
                            studentAdapter.notifyDataSetChanged();

                            new CustomDialog.BuliderOKDialog(MainStudentActivity.this)
                                    .setMessage("Lưu thành công")
                                    .setSuccessful(true)
                                    .build()
                                    .show();
                        }
                    }
                }
        );
        studentAdapter.setmUpdateSinhVienLauncher(mUpdateSinhVienLauncher);
    }

    private void setListView() {
        ProgressDialog progressDialog = CustomDialog.LoadingDialog(MainStudentActivity.this,"Loading...");
        progressDialog.show();
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(MainStudentActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<StudentItem>>> call = apiManager.getApiService().getAllStudentByPracticalClassCode(jwt, crtPracticalClassCode);
        call.enqueue(new Callback<ResponseObject<List<StudentItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<StudentItem>>> call, @NonNull Response<ResponseObject<List<StudentItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<StudentItem>> resData = response.body();
                    progressDialog.dismiss();
                    studentAdapter.clear();
                    if(resData.getRetObj()==null || resData.getRetObj().size()==0)
                        Toast.makeText(MainStudentActivity.this, "Lớp chưa có sinh viên nào", Toast.LENGTH_LONG).show();
                    else studentAdapter.addAll(resData.getRetObj());
                    studentAdapter.notifyDataSetChanged();
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(MainStudentActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<StudentItem>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(MainStudentActivity.this)
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
        lvStudent = findViewById(R.id.lvDsSV);
    }
}