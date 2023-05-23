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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.creditclass.InforCreditClassActivity;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.entity.CreditClass;
import com.example.studentmanagement.models.entity.DetailCreditClass;
import com.example.studentmanagement.models.view.CreditClassItem;
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
public class CreditClassAdapter extends ArrayAdapter implements Filterable {
    Context context;
    int resource;
    private ArrayList<CreditClassItem> data_org;
    private ArrayList<CreditClassItem> data_view;
    private String searchQuery;
    private boolean enableCUD;

    public CreditClassAdapter(Context context, int resource) {
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

    public void setEnableCUD(boolean enableCUD) {
        this.enableCUD = enableCUD;
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
        data_org.add(index, (CreditClassItem) object);
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return data_org.indexOf(item);
    }

    public void setItem(@Nullable CreditClassItem object, int index) {
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
                List<CreditClassItem> filteredList;

                String search_constraint = constraint.toString();
                if (search_constraint.isEmpty()) {
                    filteredList = new ArrayList<>(data_org);
                } else {
                    filteredList = data_org.stream().filter((creditClass) ->
                            creditClass.toString().toLowerCase().contains(constraint.toString().toLowerCase())).collect(Collectors.toList());
                }
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data_view = (ArrayList<CreditClassItem>) results.values;
                CreditClassAdapter.super.notifyDataSetChanged();
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

        CreditClassItem creditClassItem = data_view.get(position);
        viewHolder.tvMaLTC.setText("Mã LTC: " + creditClassItem.getMaLopTc());
        viewHolder.tvTenHP.setText("Tên HP: " + creditClassItem.getTenMh());
        viewHolder.tvTenGV.setText("Tên GV: " + creditClassItem.getTenGv());

//        viewHolder.ibtXoa.setOnClickListener(view -> handleXoa(studentItem));
//        viewHolder.ibtSua.setOnClickListener(view -> handleSua(studentItem));
        viewHolder.tvDetail.setOnClickListener(view -> hanldeViewDetail(creditClassItem));
//        viewHolder.btnXemDiem.setOnClickListener(view -> callCourse(creditClassItem));
        return convertView;
    }

    private void hanldeViewDetail(CreditClassItem creditClassItem) {
        callCreditClass(creditClassItem, MyFuncButton.VIEW_CREDIT_CLASS);
    }

    private void callCreditClass(CreditClassItem creditClassItem, MyFuncButton myFuncButton) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(context, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<CreditClass>> call = apiManager.getApiService().getCreditClassById(jwt, creditClassItem.getId());
        call.enqueue(new Callback<ResponseObject<CreditClass>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<CreditClass>> call, @NonNull Response<ResponseObject<CreditClass>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CreditClass data = response.body().getRetObj();
                    callDetailCreditClass(data, myFuncButton);
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
            public void onFailure(@NonNull Call<ResponseObject<CreditClass>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(context)
                        .setMessage("Lỗi kết nối!" + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private void callDetailCreditClass(CreditClass creditClass, MyFuncButton myFuncButton) {
        MyPrefs myPrefs = MyPrefs.getInstance();
        String jwt = myPrefs.getString(context, "jwt", "");
        ApiManager apiManager = ApiManager.getInstance();
        Call<ResponseObject<List<DetailCreditClass>>> call = apiManager.getApiService().getDetailByCreditClassCode(jwt, creditClass.getMaLopTc());
        call.enqueue(new Callback<ResponseObject<List<DetailCreditClass>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<List<DetailCreditClass>>> call, @NonNull Response<ResponseObject<List<DetailCreditClass>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DetailCreditClass> data = response.body().getRetObj();
                    Intent intent;
                    if (myFuncButton == MyFuncButton.VIEW_CREDIT_CLASS) {
                        intent = new Intent(context, InforCreditClassActivity.class);
                        intent.putExtra("detailCreditClass", (ArrayList<DetailCreditClass>)data);
                        intent.putExtra("creditClass", creditClass);
                        context.startActivity(intent);
                    } else {
//                        intent = new Intent(context, EditStudentActivity.class);
//                        intent.putExtra("student", data);
//                        mUpdateSinhVienLauncher.launch(intent);
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
            public void onFailure(@NonNull Call<ResponseObject<List<DetailCreditClass>>> call, @NonNull Throwable t) {
                new CustomDialog.BuliderOKDialog(context)
                        .setMessage("Lỗi kết nối!" + t.getMessage())
                        .setSuccessful(false)
                        .build()
                        .show();
            }
        });
    }

    private class ViewHolder {
        TextView tvMaLTC;
        TextView tvTenGV;
        TextView tvTenHP;
        TextView tvDetail;
        ImageButton ibtSua;
        ImageButton ibtXoa;

        public ViewHolder(View view) {
            this.tvMaLTC = view.findViewById(R.id.tvMaLTC);
            this.tvTenHP = view.findViewById(R.id.tvTenHP);
            this.tvTenGV = view.findViewById(R.id.tvTenGV);
            this.ibtSua = view.findViewById(R.id.ibtSua);
            this.ibtXoa = view.findViewById(R.id.ibtXoa);
            this.tvDetail = view.findViewById(R.id.tvDetail);
        }
    }
}
