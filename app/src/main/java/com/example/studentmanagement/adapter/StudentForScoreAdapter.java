package com.example.studentmanagement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.score.ViewScoreStudentActivity;
import com.example.studentmanagement.activities.student.EditStudentActivity;
import com.example.studentmanagement.activities.student.InforStudentActivity;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.entity.Student;
import com.example.studentmanagement.models.responsebody.ScoreStudent;
import com.example.studentmanagement.models.view.SemesterItem;
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
public class StudentForScoreAdapter extends ArrayAdapter implements Filterable {
    Context context;
    int resource;
    private ArrayList<StudentItem> data_org;
    private ArrayList<StudentItem> data_view;
    private String searchQuery;
    String crtSemester;
    List<SemesterItem> semesterItemList;

    public StudentForScoreAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.data_org = new ArrayList<>();
        this.data_view = new ArrayList<>();
        this.searchQuery = "";
    }

    public void setSemesterItemList(List<SemesterItem> semesterItemList) {
        this.semesterItemList = semesterItemList;
    }

    public void setCrtSemester(String crtSemester) {
        this.crtSemester = crtSemester;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
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
                StudentForScoreAdapter.super.notifyDataSetChanged();
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

        viewHolder.btnXemDiem.setOnClickListener(view -> callScore(studentItem));

        return convertView;
    }

    private void callScore(StudentItem studentItem) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(context, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<ScoreStudent>>> call = apiManager.getApiService().getScoreByStudentCode(jwt, studentItem.getMaSv(), crtSemester);
        call.enqueue(new Callback<ResponseObject<List<ScoreStudent>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<ScoreStudent>>> call, @NonNull Response<ResponseObject<List<ScoreStudent>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<ScoreStudent>> resData = response.body();
                    Intent intent = new Intent(context, ViewScoreStudentActivity.class);
                    intent.putExtra("studentItem", studentItem);
                    intent.putExtra("scoreItemLv", (ArrayList<ScoreStudent>)resData.getRetObj());
                    intent.putExtra("crtSemester", crtSemester);
                    intent.putExtra("listSemesterItemSpn", (ArrayList<SemesterItem>)semesterItemList);
                    context.startActivity(intent);
                } else {
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
            public void onFailure(@NonNull Call<ResponseObject<List<ScoreStudent>>> call, @NonNull Throwable t) {
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
        Button btnXemDiem;
        public ViewHolder (View view){
            this.tvTenSV = view.findViewById(R.id.tvHoTenSV);
            this.tvMaSV = view.findViewById(R.id.tvMaSV);
            this.btnXemDiem = view.findViewById(R.id.btnDiem);
        }
    }
}
