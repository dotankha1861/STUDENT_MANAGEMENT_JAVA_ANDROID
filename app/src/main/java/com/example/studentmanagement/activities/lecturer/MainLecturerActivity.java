package com.example.studentmanagement.activities.lecturer;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.course.MainCourseActivity;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivitySearchAdd;
import com.example.studentmanagement.adapter.LecturerAdapter;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.entity.Lecturer;
import com.example.studentmanagement.models.view.FacultyItem;
import com.example.studentmanagement.models.view.LecturerItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainLecturerActivity extends CustomAppCompactActivitySearchAdd {

    Toolbar toolbar;
    Spinner spnFaculty;
    LecturerAdapter lecturerAdapter;
    ListView lvLecturer;
    String crtFacultyCode;

    ActivityResultLauncher<Intent> mCreateGiangVienLauncher, mUpdateGiangVienLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_lecturer);
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
                lecturerAdapter.setSearchQuery(s);
                lecturerAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;
    }

    private void setDataSourceFacultySpinner() {
        List<FacultyItem> facultyItemList = (List<FacultyItem>) getIntent().getSerializableExtra("listFacultyItemSpn");

        ArrayAdapter<FacultyItem> facultyAdapter = new ArrayAdapter<>(MainLecturerActivity.this, R.layout.item_selected_spinner, facultyItemList);
        facultyAdapter.setDropDownViewResource(R.layout.item_dropdown_spinner);

        spnFaculty.setAdapter(facultyAdapter);
        spnFaculty.setDropDownWidth(spnFaculty.getWidth());

    }

    @SuppressLint("NonConstantResourceId")
    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itAdd:
                callAddLecturer();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void callAddLecturer() {
        Intent intent = new Intent(MainLecturerActivity.this, AddLecturerActivity.class);
        intent.putExtra("crtFacultyCode", crtFacultyCode);
        mCreateGiangVienLauncher.launch(intent);
    }


    private void setEvent() {
        setSupportActionBar(toolbar);
        lecturerAdapter = new LecturerAdapter(MainLecturerActivity.this, R.layout.item_listview_lecturer);
        lvLecturer.setAdapter(lecturerAdapter);

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
        mCreateGiangVienLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Lecturer lecturer = (Lecturer) result.getData().getSerializableExtra("newLecturer");;
                        LecturerItem lecturerItem = new LecturerItem();
                        lecturerItem.setId(lecturer.getId());
                        lecturerItem.setMaGv(lecturer.getMaGv());
                        lecturerItem.setHo(lecturer.getHo());
                        lecturerItem.setTen(lecturer.getTen());
                        lecturerItem.setPhai(lecturer.getPhai());
                        lecturerAdapter.insert(lecturerItem, 0);
                        lecturerAdapter.notifyDataSetChanged();

                        new CustomDialog.BuliderOKDialog(MainLecturerActivity.this)
                                .setMessage("Thêm thành công")
                                .setSuccessful(true)
                                .build()
                                .show();
                    }
                }
        );
        mUpdateGiangVienLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Lecturer lecturer = (Lecturer) result.getData().getSerializableExtra("changedLecturer");
                        LecturerItem lecturerItem = new LecturerItem();
                        lecturerItem.setId(lecturer.getId());
                        lecturerItem.setMaGv(lecturer.getMaGv());
                        lecturerItem.setHo(lecturer.getHo());
                        lecturerItem.setTen(lecturer.getTen());
                        lecturerItem.setPhai(lecturer.getPhai());
                        lecturerAdapter.setItem(lecturerItem, lecturerAdapter.getPosition(lecturerItem));
                        lecturerAdapter.notifyDataSetChanged();

                        new CustomDialog.BuliderOKDialog(MainLecturerActivity.this)
                                .setMessage("Lưu thành công")
                                .setSuccessful(true)
                                .build()
                                .show();
                    }
                }
        );
        lecturerAdapter.setmUpdateGiangVienLauncher(mUpdateGiangVienLauncher);
    }

    private void setListView() {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(MainLecturerActivity.this, "jwt","");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<LecturerItem>>> call = apiManager.getApiService().getAllLecturerByFacultyCode(jwt, crtFacultyCode);
        call.enqueue(new Callback<ResponseObject<List<LecturerItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<LecturerItem>>> call, @NonNull Response<ResponseObject<List<LecturerItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<LecturerItem>> resData = response.body();
                    lecturerAdapter.clear();
                    if(resData.getRetObj()==null || resData.getRetObj().size()==0)
                        Toast.makeText(MainLecturerActivity.this, "Khoa chưa có giảng viên nào", Toast.LENGTH_LONG).show();
                    else lecturerAdapter.addAll(resData.getRetObj());
                    lecturerAdapter.notifyDataSetChanged();
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(MainLecturerActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<LecturerItem>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(MainLecturerActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void setControl() {
        toolbar = findViewById(R.id.toolbar);
        spnFaculty = findViewById(R.id.spnListKhoa);
        lvLecturer = findViewById(R.id.lvDsGiangVien);
    }
}