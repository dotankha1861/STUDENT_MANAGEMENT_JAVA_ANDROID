package com.example.studentmanagement.activities.score;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.adapter.ScoreCreditClassAdapter;
import com.example.studentmanagement.models.entity.Course;
import com.example.studentmanagement.models.responsebody.ScoreCreditClass;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("SetTextI18n")
public class ViewScoreCreditClassActivity extends CustomAppCompactActivity {

    TextView tvTenHP, tvMaLop, tvPercent;
    Toolbar toolbar;
    ListView lvScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_score_credit_class_view);
        setControl();
        setEvent();
    }

    private void setEvent() {
        setSupportActionBar(toolbar);
        setInforScore();

    }

    private void setInforScore() {
        List<ScoreCreditClass> scoreCreditClassList = (ArrayList<ScoreCreditClass>) getIntent().getSerializableExtra("ScoreItemLv");
        Course course = (Course) getIntent().getSerializableExtra("course");
        String creditClassCode = getIntent().getStringExtra("creditClassCode");
        tvTenHP.setText(course.getTenMh());
        tvMaLop.setText(creditClassCode);
        tvPercent.setText(course.getPercentCc()+" - " + course.getPercentGk() + " - " + course.getPercentCk());

        ScoreCreditClassAdapter scoreCreditClassAdapter = new ScoreCreditClassAdapter(ViewScoreCreditClassActivity.this, R.layout.item_listview_score_credit_class, (ArrayList<ScoreCreditClass>) scoreCreditClassList);
        lvScore.setAdapter(scoreCreditClassAdapter);
        scoreCreditClassAdapter.notifyDataSetChanged();
    }
    private void setControl() {
        tvTenHP=findViewById(R.id.tvTenHP);
        tvMaLop=findViewById(R.id.tvMaLop);
        toolbar = findViewById(R.id.toolbar);
        tvPercent = findViewById(R.id.tvPercent);
        lvScore = findViewById(R.id.lvDiem);
    }
}