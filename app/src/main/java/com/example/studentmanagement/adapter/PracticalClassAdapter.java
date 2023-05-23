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
import com.example.studentmanagement.activities.practicalclass.EditPracticalClassActivity;
import com.example.studentmanagement.activities.practicalclass.InforPracticalClassActivity;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.entity.PracticalClass;
import com.example.studentmanagement.models.view.PracticalClassItem;
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
public class PracticalClassAdapter extends ArrayAdapter implements Filterable {
    Context context;
    int resource;
    private ArrayList<PracticalClassItem> data_org;

    private ArrayList<PracticalClassItem> data_view;

    private String searchQuery;
    ActivityResultLauncher<Intent> mUpdateLopLauncher;

    public PracticalClassAdapter(Context context, int resource) {
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

    public void setmUpdateLopLauncher(ActivityResultLauncher<Intent> mUpdateLopLauncher) {
        this.mUpdateLopLauncher = mUpdateLopLauncher;
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
        data_org.add(index, (PracticalClassItem) object);
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return data_org.indexOf(item);
    }

    public void setItem(@Nullable Object object, int index){
        data_org.set(index, (PracticalClassItem) object);
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
                List<PracticalClassItem> filteredList;

                String search_constraint = constraint.toString();
                if (search_constraint.isEmpty()) {
                    filteredList = new ArrayList<>(data_org);
                } else {
                    filteredList = data_org.stream().filter((lop) ->
                            (lop.getMaLop() + " " + lop.getTenLop())
                                    .toLowerCase().contains(constraint.toString().toLowerCase())).collect(Collectors.toList());
                }
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data_view = (ArrayList<PracticalClassItem>) results.values;
                PracticalClassAdapter.super.notifyDataSetChanged();
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

        PracticalClassItem practicalClassItem = data_view.get(position);
        viewHolder.tvMaLop.setText("Mã lớp: " + practicalClassItem.getMaLop());
        viewHolder.tvTenLop.setText(practicalClassItem.getTenLop());

        viewHolder.ibtXoa.setOnClickListener(view -> handleXoa(practicalClassItem));
        viewHolder.ibtSua.setOnClickListener(view -> handleSua(practicalClassItem));
        viewHolder.tvDetail.setOnClickListener(view -> hanldeViewDetail(practicalClassItem));

        return convertView;
    }

    private void hanldeViewDetail(PracticalClassItem practicalClassItem) {
        callPracticalClass(practicalClassItem, MyFuncButton.VIEW_PRACTICAL_CLASS);
    }
    private void handleSua(PracticalClassItem practicalClassItem) {
        callPracticalClass(practicalClassItem, MyFuncButton.EDIT_PRACTICAL_CLASS);
    }

    private void handleXoa(PracticalClassItem practicalClassItem) {
        new CustomDialog.BuliderPosNegDialog(context)
                .setMessage("Bạn chắc chắn muốn xóa lớp này?")
                .setPositiveButton("Đồng ý", (view) -> handleAgreeDelete(practicalClassItem), dismiss -> true)
                .setNegativeButton("Hủy", null, dismiss -> true)
                .build()
                .show();
    }
    private void handleAgreeDelete(PracticalClassItem practicalClassItem) {
        List<String> listPracticalClassCode = new ArrayList<>();
        listPracticalClassCode.add(practicalClassItem.getId());
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(context, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<String>>> call = apiManager.getApiService().removePracticalClass(jwt, listPracticalClassCode);
        call.enqueue(new Callback<ResponseObject<List<String>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<String>>> call, @NonNull Response<ResponseObject<List<String>>> response) {
                if (response.isSuccessful()&&response.body()!=null) {
                    ResponseObject<List<String>> dataRes = response.body();
                    if (dataRes.getRetObj()==null || dataRes.getRetObj().size() == 0) {
                        new CustomDialog.BuliderOKDialog(context)
                                .setMessage("Không thể xóa lớp này")
                                .setSuccessful(false)
                                .build()
                                .show();
                    } else {
                        data_org.remove(practicalClassItem);
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
            public void onFailure(Call<ResponseObject<List<String>>> call, Throwable t) {
                new CustomDialog.BuliderOKDialog(context)
                        .setMessage("Lỗi kết nối!" + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void callPracticalClass(PracticalClassItem practicalClassItem, MyFuncButton myFuncButton) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(context, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<PracticalClass>> call = apiManager.getApiService().getPracticalClassById(jwt, practicalClassItem.getId());
        call.enqueue(new Callback<ResponseObject<PracticalClass>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<PracticalClass>> call, @NonNull Response<ResponseObject<PracticalClass>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PracticalClass data = response.body().getRetObj();
                    Intent intent;
                    if (myFuncButton == MyFuncButton.VIEW_PRACTICAL_CLASS) {
                        intent = new Intent(context, InforPracticalClassActivity.class);
                        intent.putExtra("practicalClass", data);
                        context.startActivity(intent);
                    } else {
                        intent = new Intent(context, EditPracticalClassActivity.class);
                        intent.putExtra("practicalClass", data);
                        mUpdateLopLauncher.launch(intent);
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
            public void onFailure(@NonNull Call<ResponseObject<PracticalClass>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(context)
                        .setMessage("Lỗi kết nối!" + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }


    //
    private static class ViewHolder {
        TextView tvTenLop;
        TextView tvMaLop;

        TextView tvDetail;
        //
        ImageButton ibtSua;
        ImageButton ibtXoa;
        public ViewHolder (View view){
            this.tvTenLop = view.findViewById(R.id.tvTenLop);
            this.tvMaLop = view.findViewById(R.id.tvMaLop);
            this.ibtSua = view.findViewById(R.id.ibtSua);
            this.ibtXoa = view.findViewById(R.id.ibtXoa);
            this.tvDetail=view.findViewById(R.id.tvInforDetail);
        }
    }
}
