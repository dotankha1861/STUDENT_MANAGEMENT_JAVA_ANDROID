package com.example.studentmanagement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.student.EditStudentActivity;
import com.example.studentmanagement.activities.student.InforStudentActivity;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.entity.Student;
import com.example.studentmanagement.models.view.StudentItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.MyFuncButton;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
@SuppressLint("SetTextI18n")
public class StudentAdapter extends ArrayAdapter implements Filterable {
    Context context;
    int resource;
    private ArrayList<StudentItem> data_org;
    private ArrayList<StudentItem> data_view;
    private String searchQuery;
    ActivityResultLauncher<Intent> mUpdateSinhVienLauncher;

    public StudentAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.data_org = new ArrayList<>();
        this.data_view = new ArrayList<>();
        this.searchQuery = "";
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }
    public void setmUpdateSinhVienLauncher(ActivityResultLauncher<Intent> mUpdateSinhVienLauncher) {
        this.mUpdateSinhVienLauncher = mUpdateSinhVienLauncher;
    }
    @Override
    public int getCount() {
        return data_view.size();
    }

    @Override
    public void addAll(@NonNull Collection collection) {
        data_org.addAll(collection);
    }

    @Override
    public void insert(@Nullable Object object, int index) {
        data_org.add(index, (StudentItem) object);
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return data_org.indexOf(item);
    }

    public void setItem(@Nullable Object object, int index){
        data_org.set(index, (StudentItem) object);
    }


    @Override
    public void remove(@Nullable Object object) {
        data_org.remove(object);
    }

    @Override
    public void clear() {
        data_org.clear();
    }

    @Override
    public void notifyDataSetChanged() {
        getFilter().filter(searchQuery);
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<StudentItem> filteredList;

                String search_constraint = constraint.toString();
                if (search_constraint.isEmpty()) {
                    filteredList = new ArrayList<>(data_org);
                } else {
                    filteredList = data_org.stream().filter((sinhvien) ->
                            (sinhvien.getMaSv() + " " + sinhvien.getHo() + " "+sinhvien.getTen())
                                    .toLowerCase().contains(constraint.toString().toLowerCase())).collect(Collectors.toList());
                }
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data_view = (ArrayList<StudentItem>) results.values;
                StudentAdapter.super.notifyDataSetChanged();
            }
        };
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else viewHolder = (ViewHolder) convertView.getTag();
        StudentItem studentItem = data_view.get(position);
        viewHolder.tvTenSV.setText(studentItem.getHo() + " " + studentItem.getTen());
        viewHolder.tvMaSV.setText("Mã SV: " + studentItem.getMaSv());

        boolean isMale= studentItem.getPhai().equalsIgnoreCase("nam");
        if(isMale) viewHolder.imvPhai.setImageResource(R.drawable.icon_front_man);
        else viewHolder.imvPhai.setImageResource(R.drawable.icon_fornt_woman);

        viewHolder.ibtXoa.setOnClickListener(view -> handleXoa(studentItem));
        viewHolder.ibtSua.setOnClickListener(view -> handleSua(studentItem));
        viewHolder.tvDetail.setOnClickListener(view -> hanldeViewDetail(studentItem));

        return convertView;
    }
    private void hanldeViewDetail(StudentItem studentItem) {
        callStudent(studentItem, MyFuncButton.VIEW_STUDENT);
    }
    private void handleSua(StudentItem studentItem) {
        callStudent(studentItem, MyFuncButton.EDIT_STUDENT);
    }

    private void handleXoa(StudentItem studentItem) {
        new CustomDialog.BuliderPosNegDialog(context)
                .setMessage("Bạn chắc chắn muốn xóa sinh viên này?")
                .setPositiveButton("Đồng ý", (view) -> handleAgreeDelete(studentItem), dismiss -> true)
                .setNegativeButton("Hủy", null, dismiss -> true)
                .build()
                .show();
    }
    private void handleAgreeDelete(StudentItem studentItem) {
        List<String> listStudentCode = new ArrayList<>();
        listStudentCode.add(studentItem.getId());
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(context,"jwt","");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<String>>> call = apiManager.getApiService().removeStudent(jwt, listStudentCode);
        call.enqueue(new Callback<ResponseObject<List<String>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<String>>> call, @NonNull Response<ResponseObject<List<String>>> response) {
                if (response.isSuccessful()&&response.body()!=null) {
                    ResponseObject<List<String>> dataRes = response.body();
                    if (dataRes.getRetObj()==null || dataRes.getRetObj().size() == 0) {
                        new CustomDialog.BuliderOKDialog(context)
                                .setMessage("không thể xóa sinh viên này")
                                .setSuccessful(false)
                                .build()
                                .show();
                    } else {
                        data_org.remove(studentItem);
                        notifyDataSetChanged();
                        new CustomDialog.BuliderOKDialog(context)
                                .setMessage("Xóa thành công")
                                .setSuccessful(true)
                                .build()
                                .show();
                    }
                }
                else{
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(context)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<List<String>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(context)
                        .setMessage("Lỗi kết nối!" + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void callStudent(StudentItem studentItem, MyFuncButton myFuncButton) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(context, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<Student>> call = apiManager.getApiService().getStudentById(jwt, studentItem.getId());
        call.enqueue(new Callback<ResponseObject<Student>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Student>> call, @NonNull Response<ResponseObject<Student>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Student data = response.body().getRetObj();
                    Intent intent;
                    if (myFuncButton == MyFuncButton.VIEW_STUDENT) {
                        intent = new Intent(context, InforStudentActivity.class);
                        intent.putExtra("student", data);
                        context.startActivity(intent);
                    } else {
                        intent = new Intent(context, EditStudentActivity.class);
                        intent.putExtra("student", data);
                        mUpdateSinhVienLauncher.launch(intent);
                    }
                }
                else{
                    if (response.errorBody() != null) {
                        ResponseObject<Object> errorResponse = new Gson().fromJson(
                                response.errorBody().charStream(),
                                new TypeToken<ResponseObject<Object>>() {
                                }.getType()
                        );
                        new CustomDialog.BuliderOKDialog(context)
                                .setMessage("Lỗi" + errorResponse.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseObject<Student>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(context)
                        .setMessage("Lỗi kết nối!" + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private static class ViewHolder {
        TextView tvTenSV;
        TextView tvMaSV;
        TextView tvDetail;
        ImageView imvPhai;
        ImageButton ibtSua;
        ImageButton ibtXoa;
        public ViewHolder (View view){
            this.tvTenSV = view.findViewById(R.id.tvHoTenSV);
            this.tvMaSV = view.findViewById(R.id.tvMaSV);
            this.imvPhai = view.findViewById(R.id.imvPhai);
            this.ibtSua = view.findViewById(R.id.ibtSua);
            this.ibtXoa = view.findViewById(R.id.ibtXoa);
            this.tvDetail = view.findViewById(R.id.tvInforDetail);
        }
    }
}
