package com.example.studentmanagement.activities.faculty;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;


import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivitySearchAdd;
import com.example.studentmanagement.adapter.FacultyAdapter;
import com.example.studentmanagement.models.entity.Faculty;
import com.example.studentmanagement.models.view.FacultyItem;
import com.example.studentmanagement.ui.CustomDialog;

import java.util.List;


public class MainFacultyActivity extends CustomAppCompactActivitySearchAdd {
    Toolbar toolbar;
    FacultyAdapter facultyAdapter;
    ListView lvFaculty;
    ActivityResultLauncher<Intent> mCreateKhoaLauncher, mUpdateKhoaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_faculty);
        setControl();
        setEvent();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itAdd:
                callAddFaculty();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!super.onCreateOptionsMenu(menu)) return false;
        getSearchView().setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                facultyAdapter.setSearchQuery(s);
                facultyAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;
    }

    private void callAddFaculty() {
        Intent intent = new Intent(MainFacultyActivity.this, AddFacultyActivity.class);
        mCreateKhoaLauncher.launch(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void setEvent() {
        setSupportActionBar(toolbar);
        setListView();

        mCreateKhoaLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Faculty faculty = null;
                        if (result.getData() != null) {
                            faculty = (Faculty) result.getData().getSerializableExtra("newFaculty");
                            FacultyItem facultyItem = new FacultyItem();
                            facultyItem.setId(faculty.getId());
                            facultyItem.setMaKhoa(faculty.getMaKhoa());
                            facultyItem.setTenKhoa(faculty.getTenKhoa());
                            facultyAdapter.insert(facultyItem,0);
                            facultyAdapter.notifyDataSetChanged();

                            new CustomDialog.BuliderOKDialog(MainFacultyActivity.this)
                                    .setMessage("Thêm thành công")
                                    .setSuccessful(true)
                                    .build()
                                    .show();
                        }
                    }
                }
        );
        mUpdateKhoaLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Faculty faculty = null;
                        if (result.getData() != null) {
                            faculty = (Faculty) result.getData().getSerializableExtra("changedFaculty");
                            FacultyItem facultyItem = new FacultyItem();
                            facultyItem.setId(faculty.getId());
                            facultyItem.setTenKhoa(faculty.getTenKhoa());
                            facultyItem.setMaKhoa(faculty.getMaKhoa());
                            facultyAdapter.setItem(facultyItem, facultyAdapter.getPosition(facultyItem));
                            facultyAdapter.notifyDataSetChanged();

                            new CustomDialog.BuliderOKDialog(MainFacultyActivity.this)
                                    .setMessage("Lưu thành công")
                                    .setSuccessful(true)
                                    .build()
                                    .show();
                        }
                    }
                }
        );
        facultyAdapter.setmUpdateKhoaLauncher(mUpdateKhoaLauncher);
    }

    private void setListView() {
        List<FacultyItem> facultyItems = (List<FacultyItem>) getIntent().getSerializableExtra("listFacultyItemLv");
        facultyAdapter = new FacultyAdapter(MainFacultyActivity.this, R.layout.item_listview_faculty);
        facultyAdapter.addAll(facultyItems);
        lvFaculty.setAdapter(facultyAdapter);
        facultyAdapter.notifyDataSetChanged();
    }

    private void setControl() {
        lvFaculty=findViewById(R.id.lvDskhoa);
        toolbar=findViewById(R.id.toolbar);
    }
}