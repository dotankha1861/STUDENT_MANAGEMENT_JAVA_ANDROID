package com.example.studentmanagement.activities.student;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.customactivity.CustomAppCompactActivity;
import com.example.studentmanagement.activities.practicalclass.EditPracticalClassActivity;
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


public class EditStudentActivity extends CustomAppCompactActivity {
    Button btnLuu;
    Boolean error = false;
    EditText edtMaSV, edtHoSV, edtTenSV, edtSDT, edtNoiSinh, edtDiaChi, edtNgaySinh,  edtEmail;
    RadioButton radNam, radNu;
    TextView tvCalendar;
    Spinner  spnStatus;
    Toolbar toolbar;

    int crtStatus;

    DatePickerDialog.OnDateSetListener setListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_infor_student_edit);
        setControl();
        setEvent();
    }
    private void setEvent() {
        setSupportActionBar(toolbar);
        customToolbar();

        ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(EditStudentActivity.this, R.layout.item_selected_spinner, new ArrayList<>(StatusStudent.status.values()));
        adapterStatus.setDropDownViewResource(R.layout.item_dropdown_spinner);
        spnStatus.setAdapter(adapterStatus);

        setInforStudent();

        Calendar calendar = Calendar.getInstance();
        int year =calendar.get(Calendar.YEAR);
        int month =calendar.get(Calendar.MONTH);
        int day =calendar.get(Calendar.DAY_OF_MONTH);

        tvCalendar.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog=new DatePickerDialog(EditStudentActivity.this, android.R.style.Theme_Holo_Dialog_MinWidth,
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
        student.setTrangThai(crtStatus);
        student.setMaLop(((Student) getIntent().getSerializableExtra("student")).getMaLop());
        student.setId(((Student) getIntent().getSerializableExtra("student")).getId());
        student.setNgaySinh(new FormatterDate.Fomatter(ngaySinh)
                .from(FormatterDate.dd_slash_MM_slash_yyyy)
                .to(FormatterDate.yyyy_dash_MM_dash_dd)
                .format()
        );

        new CustomDialog.BuliderPosNegDialog(EditStudentActivity.this)
                .setMessage("Bạn có muốn lưu thay đổi không?")
                .setPositiveButton("Đồng ý", view -> callUpdateStudent(student), dismiss -> true)
                .setNegativeButton("Hủy", null, dismiss -> true)
                .build()
                .show();
    }

    private void callUpdateStudent(Student student) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(EditStudentActivity.this, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<Student>> call = apiManager.getApiService().updateStudent(jwt, student);
        call.enqueue(new Callback<ResponseObject<Student>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Student>> call, @NonNull Response<ResponseObject<Student>> response) {
                if (response.isSuccessful()&&response.body()!=null) {
                    ResponseObject<Student> resData = response.body();
                    if(resData.getStatus().equals("error")) {
                        new CustomDialog.BuliderOKDialog(EditStudentActivity.this)
                                .setMessage(resData.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                    else {
                        Intent intent = new Intent();
                        intent.putExtra("changedStudent", resData.getRetObj());
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
                        new CustomDialog.BuliderOKDialog(EditStudentActivity.this)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseObject<Student>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(EditStudentActivity.this)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void setInforStudent() {
        Student student = (Student) getIntent().getSerializableExtra("student");
        edtMaSV.setText(student.getMaSv());
        edtHoSV.setText(student.getHo());
        edtTenSV.setText(student.getTen());
        edtSDT.setText(student.getSdt());
        edtNoiSinh.setText(student.getNoiSinh());
        edtDiaChi.setText(student.getDiaChi());
        edtEmail.setText(student.getEmail());
        edtNgaySinh.setText(new FormatterDate.Fomatter(student.getNgaySinh())
                .from(FormatterDate.yyyy_dash_MM_dash_dd)
                .to(FormatterDate.dd_slash_MM_slash_yyyy)
                .format()
        );

        if (student.getPhai().equalsIgnoreCase("nam")) radNam.setChecked(true);
        else radNu.setChecked(true);
        edtMaSV.setEnabled(false);
        spnStatus.setSelection(student.getTrangThai());
    }

    private void customToolbar() {
        TextView tvTitle = toolbar.findViewById(R.id.tvTitle);
        tvTitle.setText("SỬA SINH VIÊN");
    }
    private void setControl() {
        edtNgaySinh=findViewById(R.id.edtNgaySinh);
        tvCalendar=findViewById(R.id.ivcalender);
        edtMaSV=findViewById(R.id.edtMaSV);
        edtHoSV=findViewById(R.id.edtHoSV);
        edtTenSV=findViewById(R.id.edtTenSV);
        edtDiaChi=findViewById(R.id.edtDiaChi);
        edtNoiSinh=findViewById(R.id.edtNoiSinh);
        edtEmail=findViewById(R.id.edtEmail);
        edtSDT=findViewById(R.id.edtSDT);
        radNam=findViewById(R.id.radNam);
        radNu=findViewById(R.id.radNu);
        btnLuu=findViewById(R.id.btnLuu);
        toolbar=findViewById(R.id.toolbar);
        spnStatus = findViewById(R.id.spinnerListTrangThai);
    }
}