package com.example.studentmanagement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import com.example.studentmanagement.activities.lecturer.EditLecturerActivity;
import com.example.studentmanagement.activities.lecturer.InforLecturerActivity;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.entity.Lecturer;
import com.example.studentmanagement.models.view.LecturerItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.CircleTransformation;
import com.example.studentmanagement.utils.MyFuncButton;
import com.example.studentmanagement.utils.MyPrefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("SetTextI18n")
public class LecturerAdapter extends ArrayAdapter implements Filterable {
    Context context;
    int resource;
    private ArrayList<LecturerItem> data_org;

    private ArrayList<LecturerItem> data_view;

    private String searchQuery;

    ActivityResultLauncher<Intent> mUpdateGiangVienLauncher;

    public LecturerAdapter(Context context, int resource) {
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

    public void setmUpdateGiangVienLauncher(ActivityResultLauncher<Intent> mUpdateGiangVienLauncher) {
        this.mUpdateGiangVienLauncher = mUpdateGiangVienLauncher;
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
        data_org.add(index, (LecturerItem) object);
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return data_org.indexOf(item);
    }

    public void setItem(@Nullable Object object, int index) {
        data_org.set(index, (LecturerItem) object);
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
                List<LecturerItem> filteredList;

                String search_constraint = constraint.toString();
                if (search_constraint.isEmpty()) {
                    filteredList = new ArrayList<>(data_org);
                } else {
                    filteredList = data_org.stream().filter((giangvien) ->
                            (giangvien.getMaGv() + " " + giangvien.getHo() + " " + giangvien.getTen())
                                    .toLowerCase().contains(constraint.toString().toLowerCase())).collect(Collectors.toList());
                }
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data_view = (ArrayList<LecturerItem>) results.values;
                LecturerAdapter.super.notifyDataSetChanged();
            }
        };
    }

    //
//
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else viewHolder = (ViewHolder) convertView.getTag();

        LecturerItem lecturerItem = data_view.get(position);
        viewHolder.tvTenGV.setText(lecturerItem.getHo() + " " + lecturerItem.getTen());
        viewHolder.tvMaGV.setText("Mã GV: " + lecturerItem.getMaGv());
        boolean isMale = lecturerItem.getPhai().equalsIgnoreCase("nam");
        if(isMale) viewHolder.imvPhai.setImageResource(R.drawable.icon_front_man);
        else viewHolder.imvPhai.setImageResource(R.drawable.icon_fornt_woman);
        try {
            Picasso.get()
                    .load(lecturerItem.getHinhAnh())
                    .transform(new CircleTransformation())
                    .placeholder(isMale ? R.drawable.icon_front_man : R.drawable.icon_fornt_woman)
                    .error(isMale ? R.drawable.icon_front_man : R.drawable.icon_fornt_woman)
                    .into(viewHolder.imvPhai);
        } catch (Exception ignored) {}
        viewHolder.ibtXoa.setOnClickListener(view -> handleXoa(lecturerItem));
        viewHolder.ibtSua.setOnClickListener(view -> handleSua(lecturerItem));
        viewHolder.tvDetail.setOnClickListener(view -> hanldeViewDetail(lecturerItem));
        return convertView;
    }

    private void hanldeViewDetail(LecturerItem lecturerItem) {
        callLecturer(lecturerItem, MyFuncButton.VIEW_LECTURER);
    }

    private void handleSua(LecturerItem lecturerItem) {
        callLecturer(lecturerItem, MyFuncButton.EDIT_LECTURER);
    }

    private void handleXoa(LecturerItem lecturerItem) {
        new CustomDialog.BuliderPosNegDialog(context)
                .setMessage("Bạn chắn chắn muốn xóa giảng viên này?")
                .setPositiveButton("Đồng ý", view -> handleAgreeDelete(lecturerItem), dismiss -> true)
                .setNegativeButton("Hủy", null, dismiss -> true)
                .build()
                .show();
    }

    private void handleAgreeDelete(LecturerItem lecturerItem) {
        List<String> listLecturerCode = new ArrayList<>();
        listLecturerCode.add(lecturerItem.getId());
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(context, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<String>>> call = apiManager.getApiService().removeLecturer(jwt, listLecturerCode);
        call.enqueue(new Callback<ResponseObject<List<String>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<String>>> call, @NonNull Response<ResponseObject<List<String>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject<List<String>> resData = response.body();
                    if (resData.getRetObj() == null || resData.getRetObj().size() == 0) {
                        new CustomDialog.BuliderOKDialog(context)
                                .setMessage(resData.getMessage())
                                .setSuccessful(false)
                                .build()
                                .show();
                    } else {
                        data_org.remove(lecturerItem);
                        notifyDataSetChanged();
                        new CustomDialog.BuliderOKDialog(context)
                                .setMessage("Xóa thành công")
                                .setSuccessful(true)
                                .build()
                                .show();
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
            public void onFailure(@NonNull Call<ResponseObject<List<String>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(context)
                        .setMessage("Lỗi kết nối!" + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void callLecturer(LecturerItem lecturerItem, MyFuncButton myFuncButton) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(context, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<Lecturer>> call = apiManager.getApiService().getLecturerById(jwt, lecturerItem.getId());
        call.enqueue(new Callback<ResponseObject<Lecturer>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<Lecturer>> call, @NonNull Response<ResponseObject<Lecturer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Lecturer data = response.body().getRetObj();
                    Intent intent;
                    if (myFuncButton == MyFuncButton.VIEW_LECTURER) {
                        intent = new Intent(context, InforLecturerActivity.class);
                        intent.putExtra("lecturer", data);
                        context.startActivity(intent);
                    } else {
                        intent = new Intent(context, EditLecturerActivity.class);
                        intent.putExtra("lecturer", data);
                        mUpdateGiangVienLauncher.launch(intent);
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
            public void onFailure(@NonNull Call<ResponseObject<Lecturer>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(context)
                        .setMessage("Lỗi kết nối!" + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private static class ViewHolder {
        TextView tvTenGV;
        TextView tvMaGV;
        ImageView imvPhai;
        TextView tvDetail;
        ImageButton ibtSua;
        ImageButton ibtXoa;

        public ViewHolder(View view) {
            this.tvTenGV = view.findViewById(R.id.tvHoTenGV);
            this.tvMaGV = view.findViewById(R.id.tvMaGV);
            this.imvPhai = view.findViewById(R.id.imvPhai);
            this.ibtSua = view.findViewById(R.id.ibtSua);
            this.ibtXoa = view.findViewById(R.id.ibtXoa);
            this.tvDetail = view.findViewById(R.id.tvInforDetail);
        }
    }
}
