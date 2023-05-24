package com.example.studentmanagement.activities.enrollcourse;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivitySearch;
import com.example.studentmanagement.adapter.CreditClassForChoseCourseAdapter;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.requestbody.RequestBodyEnroll;
import com.example.studentmanagement.models.responsebody.ResponseBodyEnroll;
import com.example.studentmanagement.models.view.EnrollCourseItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.MyPrefs;
import com.example.studentmanagement.utils.StatusEnroll;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListChoseCourseActivity extends CustomAppCompactActivitySearch {

    Toolbar toolbar;
    TextView tvMaSV, tvTenSV;
    Button btnDK, btnXoa;

    CreditClassForChoseCourseAdapter creditClassForChoseCourseAdapter;
    ListView lvCreditClass;
    List<EnrollCourseItem> listChoseCourse = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chose_course);
        setControl();
        setEvent();
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
                creditClassForChoseCourseAdapter.setSearchQuery(s);
                creditClassForChoseCourseAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(ListChoseCourseActivity.this, EnrollCourseActivity.class);
            intent.putExtra("listChoseCourse", (ArrayList<EnrollCourseItem>) listChoseCourse);
            setResult(RESULT_OK, intent);
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setEvent() {
        setSupportActionBar(toolbar);
        setInforStudent();

        listChoseCourse = (ArrayList<EnrollCourseItem>) getIntent().getSerializableExtra("listChoseCourse");
        creditClassForChoseCourseAdapter = new CreditClassForChoseCourseAdapter(ListChoseCourseActivity.this, R.layout.item_listview_chose_course);
        creditClassForChoseCourseAdapter.addAll(listChoseCourse);
        lvCreditClass.setAdapter(creditClassForChoseCourseAdapter);
        creditClassForChoseCourseAdapter.notifyDataSetChanged();
        btnDK.setOnClickListener(view -> hanldeDK());
        btnXoa.setOnClickListener(view -> hanldeHuyDK());
    }

    private void hanldeHuyDK() {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(ListChoseCourseActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        RequestBodyEnroll requestBodyEnroll = new RequestBodyEnroll();
        requestBodyEnroll.setMaSv(myPrefs.getString(ListChoseCourseActivity.this, "username",""));
        requestBodyEnroll.setMaLopTcList(listChoseCourse.stream()
                .filter(EnrollCourseItem::getChecked)
                .map(EnrollCourseItem::getMaLopTc)
                .collect(Collectors.toList())
        );
        Call<ResponseObject<ResponseBodyEnroll>> call = apiManager.getApiService().cancelEnrollCourse(jwt, requestBodyEnroll);
        call.enqueue(new Callback<ResponseObject<ResponseBodyEnroll>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<ResponseBodyEnroll>> call, @NonNull Response<ResponseObject<ResponseBodyEnroll>> response) {
                if (response.isSuccessful() && response.body() != null&& response.body().getRetObj().getMaLopTcList()!= null) {
                    List<String> listMaLTC = response.body().getRetObj().getMaLopTcList();

                    listChoseCourse = listChoseCourse.stream()
                            .filter(item -> !listMaLTC.contains(item.getMaLopTc()))
                            .peek(item -> item.setChecked(false))
                            .collect(Collectors.toList());

                    creditClassForChoseCourseAdapter.clear();
                    creditClassForChoseCourseAdapter.addAll(listChoseCourse);
                    creditClassForChoseCourseAdapter.notifyDataSetChanged();

                    new CustomDialog.BuliderOKDialog(ListChoseCourseActivity.this)
                            .setMessage("Đã xóa các đăng ký")
                            .setSuccessful(true)
                            .build()
                            .show();
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(ListChoseCourseActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<ResponseBodyEnroll>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(ListChoseCourseActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void hanldeDK() {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(ListChoseCourseActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        RequestBodyEnroll requestBodyEnroll = new RequestBodyEnroll();
        requestBodyEnroll.setMaSv(myPrefs.getString(ListChoseCourseActivity.this, "username",""));
        requestBodyEnroll.setMaLopTcList(listChoseCourse.stream()
                .filter(EnrollCourseItem::getChecked)
                .map(EnrollCourseItem::getMaLopTc)
                .collect(Collectors.toList())
        );
        Call<ResponseObject<ResponseBodyEnroll>> call = apiManager.getApiService().enrollCourse(jwt, requestBodyEnroll);
        call.enqueue(new Callback<ResponseObject<ResponseBodyEnroll>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<ResponseBodyEnroll>> call, @NonNull Response<ResponseObject<ResponseBodyEnroll>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getRetObj().getMaLopTcList()!= null) {
                    List<String> listCreditClassCode = response.body().getRetObj().getMaLopTcList();
                    listChoseCourse = listChoseCourse.stream().peek(item -> {
                        if(listCreditClassCode.contains(item.getMaLopTc())){
                            item.setStatusEnroll(StatusEnroll.DALUU);
                        }
                        item.setChecked(false);
                    }).collect(Collectors.toList());

                    creditClassForChoseCourseAdapter.clear();
                    creditClassForChoseCourseAdapter.addAll(listChoseCourse);
                    creditClassForChoseCourseAdapter.notifyDataSetChanged();

                    new CustomDialog.BuliderOKDialog(ListChoseCourseActivity.this)
                            .setMessage("Đã lưu các đăng ký")
                            .setSuccessful(true)
                            .build()
                            .show();
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(ListChoseCourseActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<ResponseBodyEnroll>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(ListChoseCourseActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void setInforStudent() {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String studentCode = myPrefs.getString(ListChoseCourseActivity.this, "username","");
        String fullName = myPrefs.getString(ListChoseCourseActivity.this, "userFullName", "");
        tvTenSV.setText(fullName);
        tvMaSV.setText(studentCode);
    }


    public void onBackPressed(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    private void setControl() {
        toolbar = findViewById(R.id.toolbar);
        lvCreditClass = findViewById(R.id.lvDsHp);
        tvMaSV = findViewById(R.id.tvMaSV);
        tvTenSV = findViewById(R.id.tvHoTenSV);
        btnDK = findViewById(R.id.btnLuu);
        btnXoa = findViewById(R.id.btnXoa);
    }
}