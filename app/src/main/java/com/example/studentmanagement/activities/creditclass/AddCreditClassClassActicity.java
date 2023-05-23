package com.example.studentmanagement.activities.creditclass;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;

import java.util.Calendar;

public class AddCreditClassClassActicity extends CustomAppCompactActivity {

    LinearLayout layoutList;
    Button btnThembuoihoc;
    EditText edtMaLTC, edtTenHP, edtTenGV, edtMaLop, edtTimeBD, edtTimeKT, edtSoLuong;
    TextView tvCalendarBD, tvCalendarKT;
    DatePickerDialog.OnDateSetListener setListener, setListener1;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.layout_credit_class_edit);
//            setControl();
            setEvent();
    }

    private void setEvent() {
        setSupportActionBar(toolbar);

//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);

//        tvCalendarBD.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DatePickerDialog datePickerDialog=new DatePickerDialog(ThemLTC.this, android.R.style.Theme_Holo_Dialog_MinWidth,
//                        setListener,year,month,day);
//                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                datePickerDialog.show();
//            }
//        });
//        setListener=new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int day) {
//                month=month+1;
//                Log.d(TAG, "onDateSet: dd/mm/yyyy: "+day+"/"+month+"/"+year);
//                String date=day+"/"+month+"/"+year;
//                edtTimeBD.setText(date);
//            }
//        };
//        Calendar calendar1 =Calendar.getInstance();
//        int year1 =calendar.get(Calendar.YEAR);
//        int month1 =calendar.get(Calendar.MONTH);
//        int day1 =calendar.get(Calendar.DAY_OF_MONTH);
//        tvCalendarKT.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DatePickerDialog datePickerDialog=new DatePickerDialog(ThemLTC.this, android.R.style.Theme_Holo_Dialog_MinWidth,
//                        setListener1,year1,month1,day1);
//                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                datePickerDialog.show();
//            }
//        });
//        setListener1=new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int day) {
//                month=month+1;
//                Log.d(TAG, "onDateSet: dd/mm/yyyy: "+day+"/"+month+"/"+year);
//                String date=day+"/"+month+"/"+year;
//                edtTimeKT.setText(date);
//            }
//        };
//        tvMuc.setText((CharSequence) "Thêm lớp tín chỉ");
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//        btnHuy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//        btnLuu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openDialog(R.layout.layout_dialog_luuthanhcong);
//            }
//        });
        btnThembuoihoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                addView();
            }
        });
    }
//    private void addView() {
//        View thembuoihoc =getLayoutInflater().inflate(R.layout.add_room,null,false);
//        EditText edtPhonghoc1=(EditText)thembuoihoc.findViewById(R.id.edtPhonghoc1);
//        Spinner spinnerListThu1=(Spinner)thembuoihoc.findViewById(R.id.spinnerListThu1);
//        Spinner spinnerListTietBD1=(Spinner)thembuoihoc.findViewById(R.id.spinnerListTietBD1);
//        EditText edtSoTiet1=(EditText)thembuoihoc.findViewById(R.id.edtSoTiet1);
//        ImageView imageRemove=(ImageView)thembuoihoc.findViewById(R.id.ivRemove);
//
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.thu, R.layout.my_selected_item);
//        adapter.setDropDownViewResource(R.layout.my_dropdow_item);
//        spinnerListThu1.setAdapter(adapter);
//
//        ArrayAdapter<CharSequence> adapterTiet = ArrayAdapter.createFromResource(this, R.array.listTT, R.layout.my_selected_item);
//        adapter.setDropDownViewResource(R.layout.my_dropdow_item);
//        spinnerListTietBD1.setAdapter(adapterTiet);
//
//
//        imageRemove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                removeView(thembuoihoc);
//            }
//        });
//        layoutList.addView(thembuoihoc);
//    }
//
//    private void removeView(View v) {
//        layoutList.removeView(v);
//    }
//
//    public void openDialog(int view){
//        Dialog dialog=new Dialog(this);
//        dialog.setContentView(view);
//        Window window =dialog.getWindow();
//        if (window==null){
//            return;
//        }
//        //        Chỉnh kích thước hiển thị và màu nền bên ngoài dialog
//        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        Button btnOK=dialog.findViewById(R.id.btnDongY);
//        btnOK.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//        dialog.show();
//    }
//    public void onBackPressed(){
//        super.finish();
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//    }
//    private void setControl() {
//        toolbar = findViewById(R.id.toolbar);
//        edtMaLTC=findViewById(R.id.edtMaLTC);
//        edtTenHP=findViewById(R.id.edtTenHP);
//        edtTenGV=findViewById(R.id.edtTenGV);
//        edtMaLop=findViewById(R.id.edtMaLop);
//        edtTimeBD=findViewById(R.id.edtNgayBD);
//        edtTimeKT=findViewById(R.id.edtNgayKT);
//        edtSoLuong=findViewById(R.id.edtSoLuong);
////        tvCalendarBD=findViewById(R.id.ivcalenderBD);
////        tvCalendarKT=findViewById(R.id.ivcalenderkt);
//        layoutList=findViewById(R.id.layout_list);
//        btnThembuoihoc=findViewById(R.id.btnThembuoihoc);
//    }
}