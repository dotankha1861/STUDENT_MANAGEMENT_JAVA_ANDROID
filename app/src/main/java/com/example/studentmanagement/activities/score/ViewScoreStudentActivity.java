package com.example.studentmanagement.activities.score;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.adapter.ScoreStudentAdapter;
import com.example.studentmanagement.adapter.StudentForScoreAdapter;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.entity.Student;
import com.example.studentmanagement.models.responsebody.ScoreStudent;
import com.example.studentmanagement.models.view.SemesterItem;
import com.example.studentmanagement.models.view.StudentItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("SetTextI18n")
public class ViewScoreStudentActivity extends CustomAppCompactActivity {
    Toolbar toolbar;
    TextView tvMaSV, tvTenSV;
    Spinner  spnSemester;
    ListView lvScore;
    ArrayAdapter<SemesterItem> adapterSemesterSpinner;
    ScoreStudentAdapter scoreStudentAdapter;

    String crtSemesterCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_score_student_view);
        setControl();
        setEvent();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setDataSourceSemesterSpinner();
        return super.onCreateOptionsMenu(menu);
    }
    private void setDataSourceSemesterSpinner() {
        List<SemesterItem> semesterItemList = (List<SemesterItem>) getIntent().getSerializableExtra("listSemesterItemSpn");
        adapterSemesterSpinner = new ArrayAdapter<>(ViewScoreStudentActivity.this, R.layout.item_selected_spinner, semesterItemList);
        adapterSemesterSpinner.setDropDownViewResource(R.layout.item_dropdown_spinner);
        spnSemester.setAdapter(adapterSemesterSpinner);
        spnSemester.setSelection(getIntent().getIntExtra("crtSemester", 0));
        spnSemester.setDropDownWidth(spnSemester.getWidth());
    }
    private void setEvent() {
        setSupportActionBar(toolbar);
        setInforStudent();

        scoreStudentAdapter = new ScoreStudentAdapter(ViewScoreStudentActivity.this, R.layout.item_listview_score_student);
        lvScore.setAdapter(scoreStudentAdapter);
        spnSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SemesterItem semesterItem = (SemesterItem) adapterView.getItemAtPosition(i);
                crtSemesterCode = semesterItem.getMaKeHoach();
                callScoreStudent();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void callScoreStudent() {
        ProgressDialog progressDialog = CustomDialog.LoadingDialog(ViewScoreStudentActivity.this,"Loading...");
        progressDialog.show();
        StudentItem studentItem = (StudentItem) getIntent().getSerializableExtra("studentItem");
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(ViewScoreStudentActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<ScoreStudent>>> call = apiManager.getApiService().getScoreByStudentCode(jwt, studentItem.getMaSv(), crtSemesterCode);
        call.enqueue(new Callback<ResponseObject<List<ScoreStudent>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<ScoreStudent>>> call, @NonNull Response<ResponseObject<List<ScoreStudent>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<ScoreStudent>> resData = response.body();
                    scoreStudentAdapter.clear();
                    scoreStudentAdapter.addAll(resData.getRetObj());
                    progressDialog.dismiss();
                    scoreStudentAdapter.notifyDataSetChanged();
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        progressDialog.dismiss();
                        new CustomDialog.BuliderOKDialog(ViewScoreStudentActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<ScoreStudent>>> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new CustomDialog.BuliderOKDialog(ViewScoreStudentActivity.this)
                        .setMessage("Lỗi kết nối!" + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void setInforStudent() {
        StudentItem studentItem = (StudentItem) getIntent().getSerializableExtra("studentItem");
        tvMaSV.setText(studentItem.getMaSv());
        tvTenSV.setText(studentItem.getHo() + " " + studentItem.getTen());
    }

    private void setControl() {
        toolbar = findViewById(R.id.toolbar);
        spnSemester = findViewById(R.id.spnListKy);
        lvScore = findViewById(R.id.lvDiem);
        tvMaSV = findViewById(R.id.tvMaSV);
        tvTenSV = findViewById(R.id.tvTenSV);
    }
}