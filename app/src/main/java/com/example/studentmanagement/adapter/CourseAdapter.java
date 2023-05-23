package com.example.studentmanagement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.course.EditCourseActivity;
import com.example.studentmanagement.activities.course.InforCourseActivity;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.entity.Course;
import com.example.studentmanagement.models.view.CourseItem;
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
public class CourseAdapter extends ArrayAdapter implements Filterable {
    Context context;
    int resource;
    private ArrayList<CourseItem> data_org;
    private ArrayList<CourseItem> data_view;
    private String searchQuery;

    ActivityResultLauncher<Intent> mUpdateHocPhanLauncher;

    public CourseAdapter(Context context, int resource) {
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

    public void setmUpdateHocPhanLauncher(ActivityResultLauncher<Intent> mUpdateHocPhanLauncher) {
        this.mUpdateHocPhanLauncher = mUpdateHocPhanLauncher;
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
        data_org.add(index, (CourseItem) object);
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return data_org.indexOf(item);
    }

    public void setItem(@Nullable Object object, int index){
        data_org.set(index, (CourseItem) object);
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
                List<CourseItem> filteredList;

                String search_constraint = constraint.toString();
                if (search_constraint.isEmpty()) {
                    filteredList = new ArrayList<>(data_org);
                } else {
                    filteredList = data_org.stream().filter((hocphan) -> (hocphan.getMaMh() + " " + hocphan.getTenMh()).toLowerCase().contains(constraint.toString().toLowerCase())).collect(Collectors.toList());
                }
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data_view = (ArrayList<CourseItem>) results.values;
                CourseAdapter.super.notifyDataSetChanged();
            }
        };
    }

    //
//
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else viewHolder = (ViewHolder) convertView.getTag();
        CourseItem courseItem = data_view.get(position);
        viewHolder.tvTenHP.setText(courseItem.getTenMh());
        viewHolder.tvMaHP.setText("Mã học phần: " + courseItem.getMaMh());
        viewHolder.ibtXoa.setOnClickListener(view -> handleDelete(courseItem));
        viewHolder.ibtSua.setOnClickListener(view -> handleEdit(courseItem));
        viewHolder.tvDetail.setOnClickListener(view -> hanldeViewDetail(courseItem));
        return convertView;
    }

    private void hanldeViewDetail(CourseItem courseItem) {
       callCourse(courseItem, MyFuncButton.VIEW_COURSE);
    }
    private void handleEdit(CourseItem courseItem) {
        callCourse(courseItem, MyFuncButton.EDIT_COURSE);
    }
    private void callCourse(CourseItem courseItem, MyFuncButton myFuncButton){
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(context, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<Course>> call = apiManager.getApiService().getCourseById(jwt, courseItem.getId());
        call.enqueue(new Callback<ResponseObject<Course>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Course>> call, @NonNull Response<ResponseObject<Course>> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    Course course = response.body().getRetObj();
                    Intent intent;
                    if(myFuncButton == MyFuncButton.VIEW_COURSE){
                        intent = new Intent(context, InforCourseActivity.class);
                        intent.putExtra("course", course);
                        context.startActivity(intent);
                    }
                    else{
                        intent = new Intent(context, EditCourseActivity.class);
                        intent.putExtra("course", course);
                        mUpdateHocPhanLauncher.launch(intent);
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
            public void onFailure(@NonNull Call<ResponseObject<Course>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(context)
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void handleDelete(CourseItem courseItem) {
        new CustomDialog.BuliderPosNegDialog(context)
                .setMessage("Bạn chắc chắn muốn xóa học phần này?")
                .setPositiveButton("Đồng ý", view -> handleAgreeDelete(courseItem), dismiss-> true)
                .setNegativeButton("Hủy", null, dismiss -> true)
                .build()
                .show();
    }

    private void handleAgreeDelete(CourseItem courseItem) {
        List<String> listCourseCode = new ArrayList<>();
        listCourseCode.add(courseItem.getId());
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(context, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<String>>> call = apiManager.getApiService().removeCourse(jwt, listCourseCode);
        call.enqueue(new Callback<ResponseObject<List<String>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<String>>> call, @NonNull Response<ResponseObject<List<String>>> response) {
                if (response.isSuccessful()&&response.body()!=null) {
                    ResponseObject<List<String>> resData = response.body();
                    if (resData.getRetObj()==null || resData.getRetObj().size() == 0) {
                        new CustomDialog.BuliderOKDialog(context)
                                .setMessage("Môn học đã được mở lớp")
                                .setSuccessful(false)
                                .build()
                                .show();
                    } else {
                        data_org.remove(courseItem);
                        notifyDataSetChanged();

                        new CustomDialog.BuliderOKDialog(context)
                                .setMessage("Xóa thành công")
                                .setSuccessful(true)
                                .build()
                                .show();
                    }
                }
                else {
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
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private static class ViewHolder {
        TextView tvMaHP;
        TextView tvTenHP;
        ImageButton ibtXoa;
        ImageButton ibtSua;
        TextView tvDetail;

        public ViewHolder(View view) {
            this.tvMaHP = view.findViewById(R.id.tvMaHP);
            this.tvTenHP = view.findViewById(R.id.tvTenHP);
            this.ibtSua = view.findViewById(R.id.ibtSua);
            this.ibtXoa = view.findViewById(R.id.ibtXoa);
            this.tvDetail = view.findViewById(R.id.tvInforDetail);
        }
    }
}