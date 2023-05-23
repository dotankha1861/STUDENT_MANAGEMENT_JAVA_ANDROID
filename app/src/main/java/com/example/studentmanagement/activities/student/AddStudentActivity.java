package com.example.studentmanagement.activities.student;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.activities.lecturer.AddLecturerActivity;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.entity.Student;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.FormatterDate;
import com.example.studentmanagement.utils.MyPrefs;
import com.example.studentmanagement.utils.StatusStudent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddStudentActivity extends CustomAppCompactActivity {

    Button btnLuu;
    EditText edtMaSV, edtHoSV, edtTenSV, edtNgaySinh, edtSDT, edtNoiSinh, edtDiaChi, edtEmail;
    TextView tvCalendar;
    DatePickerDialog.OnDateSetListener setListener;
    Spinner spnStatus;
    RadioButton radNam, radNu;
    int crtStatus;
    Toolbar toolbar;
    Boolean error = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_infor_student_edit);
        setControl();
        setEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        spnStatus.setDropDownWidth(spnStatus.getWidth());
        return super.onCreateOptionsMenu(menu);
    }

    private void setEvent() {
        setSupportActionBar(toolbar);

        ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(AddStudentActivity.this, R.layout.item_selected_spinner, new ArrayList<>(StatusStudent.status.values()));
        adapterStatus.setDropDownViewResource(R.layout.item_dropdown_spinner);
        spnStatus.setAdapter(adapterStatus);
        spnStatus.setSelection(0);

        spnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                crtStatus = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Calendar calendar =Calendar.getInstance();
        int year =calendar.get(Calendar.YEAR);
        int month =calendar.get(Calendar.MONTH);
        int day =calendar.get(Calendar.DAY_OF_MONTH);

        tvCalendar.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog=new DatePickerDialog(AddStudentActivity.this, android.R.style.Theme_Holo_Dialog_MinWidth,
                    setListener,year,month,day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });
        setListener= (view, year1, month1, day1) -> {
            month1 = month1 +1;
            String date= day1 +"/"+ month1 +"/"+ year1;
            edtNgaySinh.setText(date);
        };

        btnLuu.setOnClickListener( view -> handleLuu());
    }


    private void handleLuu() {
        String tenSV = edtTenSV.getText().toString().trim();
        String maSV= edtMaSV.getText().toString().trim();
        String hoSV = edtHoSV.getText().toString().trim();
        String sdt = edtSDT.getText().toString().trim();
        String noiSinh = edtNoiSinh.getText().toString().trim();
        String diaChi = edtDiaChi.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String ngaySinh= edtNgaySinh.getText().toString().trim();
        boolean isMale = radNam.isChecked();

        if(TextUtils.isEmpty(email)){
            edtEmail.setError("Vui lòng nhập email của sinh viên");
            edtEmail.requestFocus();
            error = true;
        }

        if(TextUtils.isEmpty(sdt)){
            edtSDT.setError("Vui lòng nhập SĐT của sinh viên");
            edtSDT.requestFocus();
            error = true;
        }

        if(TextUtils.isEmpty(diaChi)){
            edtDiaChi.setError("Vui lòng nhập địa chỉ của sinh viên");
            edtDiaChi.requestFocus();
            error = true;
        }
        else if(diaChi.length()<4){
            edtDiaChi.setError("Địa chỉ phải có tối thiểu 4 kí tự");
            edtDiaChi.requestFocus();
            error = true;
        }

        if(TextUtils.isEmpty(noiSinh)){
            edtNoiSinh.setError("Vui lòng nhập nơi sinh của sinh viên");
            edtNoiSinh.requestFocus();
            error = true;
        }
        else if(noiSinh.length()<4){
            edtNoiSinh.setError("Nơi sinh phải có tối thiểu 4 kí tự");
            edtNoiSinh.requestFocus();
            error = true;
        }

        if(TextUtils.isEmpty(ngaySinh)){
            edtNgaySinh.setError("Vui lòng nhập ngày sinh của sinh viên");
            edtNgaySinh.requestFocus();
            error = true;
        }

        if(TextUtils.isEmpty(tenSV)){
            edtTenSV.setError("Vui lòng nhập tên sinh viên");
            edtTenSV.requestFocus();
            error = true;
        }

        if(TextUtils.isEmpty(hoSV)){
            edtHoSV.setError("Vui lòng nhập họ sinh viên");
            edtHoSV.requestFocus();
            error = true;
        }

        if(TextUtils.isEmpty(maSV)){
            edtMaSV.setError("Vui lòng nhập mã sinh viên");
            edtMaSV.requestFocus();
            error = true;
        }
        else if(maSV.length()<4){
            edtMaSV.setError("Mã sinh viên phải có tối thiểu 4 kí tự");
            edtMaSV.requestFocus();
            error = true;
        }

        if(error){
            error=false;
            return;
        }

        Student student = new Student();
        student.setMaSv(maSV);
        student.setHo(hoSV);
        student.setTen(tenSV);
        student.setPhai(isMale?"Nam":"Nữ");
        student.setNoiSinh(noiSinh);
        student.setDiaChi(diaChi);
        student.setSdt(sdt);
        student.setEmail(email);
        student.setNgaySinh(new FormatterDate.Fomatter(ngaySinh)
                .from(FormatterDate.dd_slash_MM_slash_yyyy)
                .to(FormatterDate.yyyy_dash_MM_dash_dd)
                .format()
        );
        student.setTrangThai(crtStatus);
        student.setMaLop(getIntent().getStringExtra("crtPracticalClassCode"));
        callAddStudent(student);
    }


    private void callAddStudent(Student student) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(AddStudentActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<Student>> call = apiManager.getApiService().createStudent(jwt, student);
        call.enqueue(new Callback<ResponseObject<Student>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Student>> call, @NonNull Response<ResponseObject<Student>> response) {
                if (response.isSuccessful()&&response.body()!=null) {
                    ResponseObject<Student> resData= response.body();
                    if (resData.getStatus().equals("error")) {
                        new CustomDialog.BuliderOKDialog(AddStudentActivity.this)
                                .setMessage(resData.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                    else{
                        Intent intent = new Intent();
                        intent.putExtra("newStudent", resData.getRetObj());
                        setResult(RESULT_OK, intent);
                        onBackPressed();
                    }
                }
                else {
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(AddStudentActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseObject<Student>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(AddStudentActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }
    private void setControl() {
        btnLuu=findViewById(R.id.btnLuu);
        edtEmail = findViewById(R.id.edtEmail);
        edtMaSV=findViewById(R.id.edtMaSV);
        edtHoSV=findViewById(R.id.edtHoSV);
        edtTenSV=findViewById(R.id.edtTenSV);
        edtNgaySinh=findViewById(R.id.edtNgaySinh);
        edtSDT=findViewById(R.id.edtSDT);
        edtNoiSinh=findViewById(R.id.edtNoiSinh);
        edtDiaChi=findViewById(R.id.edtDiaChi);
        spnStatus=findViewById(R.id.spinnerListTrangThai);
        tvCalendar=findViewById(R.id.ivcalender);
        toolbar=findViewById(R.id.toolbar);
        radNam=findViewById(R.id.radNam);
        radNu=findViewById(R.id.radNu);
    }
}