package com.example.studentmanagement.activities.statistic;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.activities.practicalclass.EditPracticalClassActivity;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.responsebody.ScoreStatistic;
import com.example.studentmanagement.models.view.ColScoreItem;
import com.example.studentmanagement.models.view.CreditClassItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.ColsScore;
import com.example.studentmanagement.utils.MyPrefs;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewStatisticActivity extends CustomAppCompactActivity {

    TextView tvTenHP, tvMaLop, tvTenGV;
    BarChart barChart;
    Toolbar toolbar;
    Spinner spnColScore;
    ArrayAdapter<ColScoreItem> adapterColScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_statistic_view);
        setControl();
        setEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setColSocreSpinner();
        return super.onCreateOptionsMenu(menu);
    }

    private void setEvent() {
        setSupportActionBar(toolbar);
        setInforCreditClass();

        spnColScore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ColScoreItem colScoreItem = (ColScoreItem) adapterView.getItemAtPosition(i);
                callStatistic(colScoreItem.getTenCot());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void callStatistic(String colName) {
        CreditClassItem creditClassItem = (CreditClassItem) getIntent().getSerializableExtra("creditClass");
        String idCreditClass = creditClassItem.getId();
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(ViewStatisticActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<ScoreStatistic>>> call = apiManager.getApiService().getScoreStatisticByCreditClassCode(jwt, idCreditClass, colName);
        call.enqueue(new Callback<ResponseObject<List<ScoreStatistic>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<ScoreStatistic>>> call, @NonNull Response<ResponseObject<List<ScoreStatistic>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<ScoreStatistic>> jwtResponse = response.body();
                    barChart.setVisibility(View.VISIBLE);
                    callChart(jwtResponse.getRetObj());
                } else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(ViewStatisticActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<ScoreStatistic>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(ViewStatisticActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void callChart(List<ScoreStatistic> data) {
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        };

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            barEntries.add(new BarEntry(i, data.get(i).getSoLuong()));
            labels.add(data.get(i).getType());
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Điểm");
        barDataSet.setColors(Color.rgb(0, 90, 102));

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);
        barData.setValueTextSize(10f);
        barData.setValueFormatter(formatter);
        BarChart barChart = findViewById(R.id.barChart);
        barChart.setData(barData);
        barChart.getXAxis().setAxisMinimum(-1f);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setLabelCount(11);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getAxisLeft().setGranularity(1f);
        barChart.getAxisLeft().setValueFormatter(formatter);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void setColSocreSpinner() {
        adapterColScore = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ColsScore.getListColScore());
        adapterColScore.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnColScore.setAdapter(adapterColScore);
        spnColScore.setDropDownWidth(spnColScore.getWidth());
    }

    private void setInforCreditClass() {
        CreditClassItem creditClassItem = (CreditClassItem) getIntent().getSerializableExtra("creditClass");
        tvTenHP.setText(creditClassItem.getTenMh());
        tvMaLop.setText(creditClassItem.getMaLopTc());
        tvTenGV.setText(creditClassItem.getTenGv());
    }

    private void setControl() {
        tvTenHP = findViewById(R.id.tvTenHP);
        tvTenGV = findViewById(R.id.tvTenGV);
        tvMaLop = findViewById(R.id.tvMaLop);
        toolbar = findViewById(R.id.toolbar);
        spnColScore = findViewById(R.id.spnColDiem);
        barChart = findViewById(R.id.barChart);
    }
}