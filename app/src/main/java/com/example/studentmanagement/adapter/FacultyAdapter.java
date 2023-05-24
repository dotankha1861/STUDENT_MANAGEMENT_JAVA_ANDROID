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
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.faculty.EditFacultyActivity;
import com.example.studentmanagement.activities.faculty.InforFacultyActivity;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.entity.Faculty;
import com.example.studentmanagement.models.view.FacultyItem;
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
public class FacultyAdapter extends ArrayAdapter implements Filterable {
    Context context;
    int resource;
    private ArrayList<FacultyItem> data_org;
    private ArrayList<FacultyItem> data_view;
    private String searchQuery;

    ActivityResultLauncher<Intent> mUpdateKhoaLauncher;

    public FacultyAdapter(Context context, int resource) {
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

    public void setmUpdateKhoaLauncher(ActivityResultLauncher<Intent> mUpdateKhoaLauncher) {
        this.mUpdateKhoaLauncher = mUpdateKhoaLauncher;
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
        data_org.add(index, (FacultyItem) object);
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return data_org.indexOf(item);
    }

    public void setItem(@Nullable FacultyItem object, int index){
        data_org.set(index, object);
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
                List<FacultyItem> filteredList;

                String search_constraint = constraint.toString();
                if (search_constraint.isEmpty()) {
                    filteredList = new ArrayList<>(data_org);
                } else {
                    filteredList = data_org.stream().filter((khoa) ->
                            (khoa.getMaKhoa() + " " + khoa.getTenKhoa())
                                    .toLowerCase().contains(constraint.toString().toLowerCase())).collect(Collectors.toList());
                }
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data_view = (ArrayList<FacultyItem>) results.values;
                FacultyAdapter.super.notifyDataSetChanged();
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
        FacultyItem facultyItem =data_view.get(position);
        viewHolder.tvTenKhoa.setText(facultyItem.getTenKhoa());
        viewHolder.tvMaKhoa.setText("Mã khoa: " + facultyItem.getMaKhoa());
        viewHolder.ibtXoa.setOnClickListener(view -> handleXoa(facultyItem));
        viewHolder.ibtSua.setOnClickListener(view -> handleSua(facultyItem));
        viewHolder.tvDetail.setOnClickListener(view -> hanldeViewDetail(facultyItem));

        return convertView;
    }

    private void hanldeViewDetail(FacultyItem facultyItem) {
        callFaculty(facultyItem, MyFuncButton.VIEW_FACULTY);
    }
    private void handleSua(FacultyItem facultyItem) {
        callFaculty(facultyItem, MyFuncButton.EDIT_FACULTY);
    }

    private void handleXoa(FacultyItem facultyItem) {
        new CustomDialog.BuliderPosNegDialog(context)
                .setMessage("Bạn chắc chắn muốn xóa khoa này?")
                .setPositiveButton("Đồng ý", (view) -> handleAgreeDelete(facultyItem), dismiss -> true)
                .setNegativeButton("Hủy", null, dismiss -> true)
                .build()
                .show();
    }

    private void callFaculty(FacultyItem facultyItem, MyFuncButton myFuncButton) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(context, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<Faculty>> call = apiManager.getApiService().getFacultyById(jwt, facultyItem.getId());
        call.enqueue(new Callback<ResponseObject<Faculty>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Faculty>> call, @NonNull Response<ResponseObject<Faculty>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Faculty data = response.body().getRetObj();
                    Intent intent;
                    if(myFuncButton == MyFuncButton.VIEW_FACULTY){
                        intent = new Intent(context, InforFacultyActivity.class);
                        intent.putExtra("faculty", data);
                        context.startActivity(intent);
                    }
                    else{
                        intent = new Intent(context, EditFacultyActivity.class);
                        intent.putExtra("faculty", data);
                        mUpdateKhoaLauncher.launch(intent);
                    }
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
            public void onFailure(@NonNull Call<ResponseObject<Faculty>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(context)
                        .setMessage("Lỗi kết nối!" + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }
    private void handleAgreeDelete(FacultyItem facultyItem) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(context, "jwt", "");
        List<String> listFacultyCode = new ArrayList<>();
        listFacultyCode.add(facultyItem.getId());
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<String>>> call = apiManager.getApiService().removeFaculty(jwt, listFacultyCode);
        call.enqueue(new Callback<ResponseObject<List<String>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<String>>> call, @NonNull Response<ResponseObject<List<String>>> response) {
                if (response.isSuccessful()&&response.body()!=null) {
                    ResponseObject<List<String>> dataRes = response.body();
                    if (dataRes.getRetObj() == null || dataRes.getRetObj().size() == 0) {
                        new CustomDialog.BuliderOKDialog(context)
                                .setMessage("Không thể xóa khoa này")
                                .setSuccessful(false)
                                .build()
                                .show();
                    } else {
                        data_org.remove(facultyItem);
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
                        .setMessage("Lỗi kết nối! " + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private static class ViewHolder {
        TextView tvTenKhoa;
        TextView tvMaKhoa;
        TextView tvDetail;
        ImageButton ibtSua;
        ImageButton ibtXoa;

        //
        public ViewHolder(View view) {
            this.tvTenKhoa = view.findViewById(R.id.tvTenKhoa);
            this.tvMaKhoa = view.findViewById(R.id.tvMaKhoa);
            this.tvDetail=view.findViewById(R.id.tvInforDetail);
            this.ibtSua = view.findViewById(R.id.ibtSua);
            this.ibtXoa = view.findViewById(R.id.ibtXoa);
        }
    }
}
