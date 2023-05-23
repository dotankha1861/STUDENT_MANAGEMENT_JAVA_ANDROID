package com.example.studentmanagement.activities.practicalclass;

import android.annotation.SuppressLint;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.course.MainCourseActivity;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivitySearchAdd;
import com.example.studentmanagement.adapter.PracticalClassAdapter;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.entity.PracticalClass;
import com.example.studentmanagement.models.view.FacultyItem;
import com.example.studentmanagement.models.view.PracticalClassItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPracticalClassActivity extends CustomAppCompactActivitySearchAdd {
    Toolbar toolbar;
    Spinner spnFaculty;
    PracticalClassAdapter practicalClassAdapter;
    ListView lvPracticalClass;
    String crtFacultyCode;
    ActivityResultLauncher<Intent> mCreateLopLauncher, mUpdateLopLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_practical_class);
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
                practicalClassAdapter.setSearchQuery(s);
                practicalClassAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itAdd:
                callAddPracticalClass();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callAddPracticalClass() {
        Intent intent = new Intent(MainPracticalClassActivity.this, AddPracticalClassActivity.class);
        intent.putExtra("crtFacultyCode", crtFacultyCode);
        mCreateLopLauncher.launch(intent);
    }

    private void setEvent() {
        setSupportActionBar(toolbar);

        practicalClassAdapter = new PracticalClassAdapter(MainPracticalClassActivity.this, R.layout.item_listview_practical_class);
        lvPracticalClass.setAdapter(practicalClassAdapter);

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
        mCreateLopLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            PracticalClass practicalClass = (PracticalClass) result.getData()
                                    .getSerializableExtra("newPracticalClass");

                            PracticalClassItem practicalClassItem =new PracticalClassItem();
                            practicalClassItem.setId(practicalClass.getId());
                            practicalClassItem.setMaLop(practicalClass.getMaLop());
                            practicalClassItem.setTenLop(practicalClass.getTenLop());
                            practicalClassAdapter.insert(practicalClassItem,0);
                            practicalClassAdapter.notifyDataSetChanged();

                            new CustomDialog.BuliderOKDialog(MainPracticalClassActivity.this)
                                    .setMessage("Thêm thành công")
                                    .setSuccessful(true)
                                    .build()
                                    .show();
                        }
                    }
                }
        );
        mUpdateLopLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            PracticalClass practicalClass = (PracticalClass) result.getData()
                                    .getSerializableExtra("changedPracticalClass");
                            PracticalClassItem practicalClassItem = new PracticalClassItem();
                            practicalClassItem.setId(practicalClass.getId());
                            practicalClassItem.setMaLop(practicalClass.getMaLop());
                            practicalClassItem.setTenLop(practicalClass.getTenLop());
                            practicalClassAdapter.setItem(practicalClassItem, practicalClassAdapter.getPosition(practicalClassItem));
                            practicalClassAdapter.notifyDataSetChanged();

                            new CustomDialog.BuliderOKDialog(MainPracticalClassActivity.this)
                                    .setMessage("Lưu thành công")
                                    .setSuccessful(true)
                                    .build()
                                    .show();
                        }
                    }
                }
        );
        practicalClassAdapter.setmUpdateLopLauncher(mUpdateLopLauncher);
    }

    private void setListView() {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(MainPracticalClassActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<PracticalClassItem>>> call = apiManager.getApiService()
                .getAllPracticalClassByFacultyCode(jwt, crtFacultyCode);
        call.enqueue(new Callback<ResponseObject<List<PracticalClassItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<PracticalClassItem>>> call,
                                   @NonNull Response<ResponseObject<List<PracticalClassItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<PracticalClassItem>> resData = response.body();

                    practicalClassAdapter.clear();
                    practicalClassAdapter.addAll(resData.getRetObj());
                    practicalClassAdapter.notifyDataSetChanged();
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(MainPracticalClassActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<PracticalClassItem>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(MainPracticalClassActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void setDataSourceFacultySpinner() {
        List<FacultyItem> facultyItemList = (List<FacultyItem>) getIntent().getSerializableExtra("listFacultyItemSpn");

        ArrayAdapter<FacultyItem> facultyAdapter = new ArrayAdapter<>(MainPracticalClassActivity.this, R.layout.item_selected_spinner, facultyItemList);
        facultyAdapter.setDropDownViewResource(R.layout.item_dropdown_spinner);

        spnFaculty.setAdapter(facultyAdapter);
        spnFaculty.setDropDownWidth(spnFaculty.getWidth());

    }
    private void setControl() {
        lvPracticalClass=findViewById(R.id.lvDsLop);
        spnFaculty = findViewById(R.id.spnListKhoa);
        toolbar = findViewById(R.id.toolbar);
    }
}