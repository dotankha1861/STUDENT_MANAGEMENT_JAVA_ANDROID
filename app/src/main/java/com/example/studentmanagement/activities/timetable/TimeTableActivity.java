package com.example.studentmanagement.activities.timetable;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;


import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.creditclass.MainCreditClassActivity;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.adapter.TimeTableAdapter;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ERole;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.view.SemesterItem;
import com.example.studentmanagement.models.view.TimeTableItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.FormatterDate;
import com.example.studentmanagement.utils.MyPrefs;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimeTableActivity extends CustomAppCompactActivity {

    Toolbar toolbar;
    ListView lvTimeTable;
    Spinner spnWeek;
    ArrayAdapter<String> adapterTuanSpinner;
    TimeTableAdapter timeTableAdapter;
    SemesterItem semesterItem;
    TextView tvHK;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_timetable);
        setControl();
        setEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setDataSourceWeekSpinner();
        return super.onCreateOptionsMenu(menu);
    }

    private void setDataSourceWeekSpinner() {
        List<SemesterItem> semesterItemList = (List<SemesterItem>) getIntent().getSerializableExtra("listSemesterItemSpn");
        semesterItem = semesterItemList.get(0);
        tvHK.setText(semesterItem.toString());
        Date today = new Date(System.currentTimeMillis());

        int numberOfWeek = FormatterDate.getWeek(semesterItem.getTimeStudyBegin(), semesterItem.getTimeStudyEnd());
        adapterTuanSpinner = new ArrayAdapter<>(
                TimeTableActivity.this,
                R.layout.item_selected_spinner,
                IntStream.range(1, numberOfWeek + 1)
                        .mapToObj(week -> {
                            Date begin = FormatterDate.getFirstDayOfWeek(semesterItem.getTimeStudyBegin(), week);
                            Date end = FormatterDate.getEndDayOfWeek(semesterItem.getTimeStudyBegin(), week);
                            return "Tuần " + week + " - ("
                                    + FormatterDate.convertDate2String(begin, FormatterDate.dd_slash_MM_slash_yyyy) + " - "
                                    + FormatterDate.convertDate2String(end, FormatterDate.dd_slash_MM_slash_yyyy) + ")";
                        })
                        .collect(Collectors.toList())
        );

        adapterTuanSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnWeek.setAdapter(adapterTuanSpinner);
        spnWeek.setDropDownWidth(spnWeek.getWidth());

        if (today.compareTo(semesterItem.getTimeStudyBegin()) >= 0 && today.compareTo(semesterItem.getTimeStudyEnd()) <= 0) {
            spnWeek.setSelection(FormatterDate.getWeek(semesterItem.getTimeStudyBegin(), today) - 1);
        }
    }

    private void setEvent() {
        setSupportActionBar(toolbar);
        timeTableAdapter= new TimeTableAdapter(TimeTableActivity.this, R.layout.item_timetable, new ArrayList<>());

        spnWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    callTimeTable(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void callTimeTable(int weekCode) {
        ProgressDialog progressDialog = CustomDialog.LoadingDialog(TimeTableActivity.this, "Loading ...");
        progressDialog.show();
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(TimeTableActivity.this, "jwt", "");
        String code = myPrefs.getString(TimeTableActivity.this, "username", "");
        ERole eRole = ERole.valueOf(myPrefs.getString(TimeTableActivity.this, "role", ""));
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<TimeTableItem>>> call = eRole == ERole.GIANGVIEN ? apiManager.getApiService().getTimeTableLecturerByWeek(jwt, code, weekCode +1) :
                apiManager.getApiService().getTimeTableStudentByWeek(jwt, code, weekCode +1);
        call.enqueue(new Callback<ResponseObject<List<TimeTableItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<TimeTableItem>>> call, @NonNull Response<ResponseObject<List<TimeTableItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<TimeTableItem>> resBody = response.body();

                    List<TimeTableItem> dataListView = IntStream.range(0, 6)
                            .mapToObj(index -> {
                                TimeTableItem tkbItem = resBody.getRetObj().get(index);
                                tkbItem.setNgay(FormatterDate.convertDate2String(
                                        FormatterDate.getAnyDayOfWeek(semesterItem.getTimeStudyBegin(), weekCode + 1, index + 1),
                                        FormatterDate.dd_slash_MM_slash_yyyy
                                ));
                                tkbItem.setThu(String.valueOf(index+2));
                                return tkbItem;
                            })
                            .collect(Collectors.toList());
                    TimeTableItem timeTableItem = new TimeTableItem();
                    timeTableItem.setThu("CN");
                    timeTableItem.setNgay(FormatterDate.convertDate2String(
                            FormatterDate.getEndDayOfWeek(semesterItem.getTimeStudyBegin(),weekCode +1),
                            FormatterDate.dd_slash_MM_slash_yyyy
                    ));
                    timeTableItem.setTkbDtoList(new ArrayList<>());
                    dataListView.add(timeTableItem);
                    timeTableAdapter.clear();
                    timeTableAdapter.addAll(dataListView);
                    lvTimeTable.setAdapter(timeTableAdapter);
                    progressDialog.dismiss();
                    timeTableAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Lỗi!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<TimeTableItem>>> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Lỗi kết nối dữ liệu! " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setControl() {
        toolbar = findViewById(R.id.toolbar);
        lvTimeTable = findViewById(R.id.lvTKB);
        spnWeek = findViewById(R.id.spnListTuan);
        tvHK = findViewById(R.id.tvHK);
    }
}