package com.example.studentmanagement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.studentmanagement.R;
import com.example.studentmanagement.api.ApiManager;
import com.example.studentmanagement.api.ResponseObject;
import com.example.studentmanagement.models.entity.DetailCreditClass;
import com.example.studentmanagement.models.view.EnrollCourseItem;
import com.example.studentmanagement.ui.CustomDialog;
import com.example.studentmanagement.utils.FormatterDate;
import com.example.studentmanagement.utils.MyPrefs;
import com.example.studentmanagement.utils.StatusEnroll;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
@SuppressLint("SetTextI18n")
public class CreditClassForChoseCourseAdapter extends ArrayAdapter implements Filterable {
    Context context;
    int resource;
    private String searchQuery;
    ArrayList<EnrollCourseItem> data_org;
    ArrayList<EnrollCourseItem> data_view;

    public CreditClassForChoseCourseAdapter(Context context, int resource) {
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
        data_org.add(index, (EnrollCourseItem) object);
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return data_org.indexOf(item);
    }

    public void setItem(@Nullable EnrollCourseItem object, int index){
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
                List<EnrollCourseItem> filteredList;

                String search_constraint = constraint.toString();
                if (search_constraint.isEmpty()) {
                    filteredList = new ArrayList<>(data_org);
                } else {
                    filteredList = new ArrayList<>();
                    filteredList = data_org.stream().filter((creditClass) ->
                            creditClass.toString().toLowerCase().contains(constraint.toString().toLowerCase())).collect(Collectors.toList());
                }
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data_view = (ArrayList<EnrollCourseItem>) results.values;
                CreditClassForChoseCourseAdapter.super.notifyDataSetChanged();
            }
        };
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else viewHolder = (ViewHolder) convertView.getTag();

        EnrollCourseItem enrollCourseItem =  data_view.get(position);
        viewHolder.tvTenHp.setText(enrollCourseItem.getTenMh());
        viewHolder.tvMaLTC.setText("Mã lớp TC: " + enrollCourseItem.getMaLopTc());
        viewHolder.tvTenGv.setText("Giảng viên: " + enrollCourseItem .getTenGv());
        viewHolder.tvMaHp.setText("Mã HP: " + enrollCourseItem.getMaMh());
        viewHolder.tvSoTc.setText("Số TC: "+ enrollCourseItem.getSoTc());
        viewHolder.tvMaLop.setText("Mã lớp: "+ enrollCourseItem.getMaLop());
        viewHolder.tvSiSo.setText("Sỉ số: " + enrollCourseItem.getSoLuong());
        viewHolder.tvConLai.setText("Còn lại: " + enrollCourseItem.getSoLuongCon());
        viewHolder.tvTrangThai.setText(enrollCourseItem.getStatusEnroll() == StatusEnroll.DALUU? "Đã lưu" :"Chưa lưu");

        if(enrollCourseItem.getVisibleCT()) {
            viewHolder.lnlLichHoc.setVisibility(View.VISIBLE);
            viewHolder.tvLichHoc.setText("Rút gọn");
        }
        else{
            viewHolder.lnlLichHoc.setVisibility(View.GONE);
            viewHolder.tvLichHoc.setText("Xem lịch học ...");
        }

        viewHolder.cbDangKy.setChecked(enrollCourseItem.getChecked());

        viewHolder.lnlLichHoc.setVisibility(enrollCourseItem.getVisibleCT()? View.VISIBLE:View.GONE);

        CreditClassForChoseCourseAdapter.ViewHolder finalViewHolder = viewHolder;

        viewHolder.tvLichHoc.setOnClickListener(view-> {
            enrollCourseItem.setVisibleCT(!enrollCourseItem.getVisibleCT());
            if(enrollCourseItem.getVisibleCT()){
                MyPrefs myPrefs = MyPrefs.getInstance();
                String jwt = myPrefs.getString(context, "jwt", "");
                ApiManager apiManager = ApiManager.getInstance();
                Call<ResponseObject<List<DetailCreditClass>>> call = apiManager.getApiService().getDetailByCreditClassCode(jwt, enrollCourseItem.getMaLopTc());
                call.enqueue(new Callback<ResponseObject<List<DetailCreditClass>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseObject<List<DetailCreditClass>>> call, @NonNull Response<ResponseObject<List<DetailCreditClass>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ResponseObject<List<DetailCreditClass>> jwtResponse = response.body();
                            List<DetailCreditClass> data = jwtResponse.getRetObj();
                            finalViewHolder.lnlLichHoc.removeAllViews();
                            IntStream.range(0, data.size())
                                    .forEach(index -> addView(finalViewHolder, data.get(index), index));
                            finalViewHolder.tvLichHoc.setText("Rút gọn");
                            finalViewHolder.lnlLichHoc.setVisibility(View.VISIBLE);
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
            else{
                finalViewHolder.tvLichHoc.setText("Xem lịch học ...");
                finalViewHolder.lnlLichHoc.setVisibility(View.GONE);
            }
        });
        viewHolder.cbDangKy.setOnClickListener((view) -> enrollCourseItem.setChecked(!enrollCourseItem.getChecked()));

        return convertView;
    }

    private void addView(ViewHolder finalViewHolder, DetailCreditClass detailCreditClass, int index) {
        @SuppressLint("InflateParams") View lichHoc = LayoutInflater.from(context).inflate(R.layout.item_detail_credit_class, null);
        ((TextView) lichHoc.findViewById(R.id.tvTitle)).setText("Lịch học " + (index+1));
        ((TextView) lichHoc.findViewById(R.id.tvPhonghoc)).setText("Phòng: " +detailCreditClass.getPhong());
        ((TextView) lichHoc.findViewById(R.id.tvThu)).setText("Thứ: "+ detailCreditClass.getThu());
        ((TextView) lichHoc.findViewById(R.id.tvSotiet)).setText("Số tiết: "+ detailCreditClass.getSoTiet());
        ((TextView) lichHoc.findViewById(R.id.tvTietBD)).setText("Tiết BĐ: "+ detailCreditClass.getTiet());
        ((TextView) lichHoc.findViewById(R.id.tvNgayBD)).setText("Ngày BĐ: " +
                new FormatterDate.Fomatter(detailCreditClass.getTimeBd())
                        .from(FormatterDate.yyyy_dash_MM_dash_dd)
                        .to(FormatterDate.dd_slash_MM_slash_yyyy)
                        .format()
        );
        ((TextView) lichHoc.findViewById(R.id.tvNgayKT)).setText( "Ngày KT: " +
                new FormatterDate.Fomatter(detailCreditClass.getTimeKt())
                        .from(FormatterDate.yyyy_dash_MM_dash_dd)
                        .to(FormatterDate.dd_slash_MM_slash_yyyy)
                        .format()
        );
        finalViewHolder.lnlLichHoc.addView(lichHoc);
    }

    private static class ViewHolder {
        TextView tvTenHp;
        TextView tvMaLTC;
        TextView tvTenGv;
        TextView tvMaHp;
        TextView tvSoTc;
        TextView tvMaLop;
        TextView tvSiSo;
        TextView tvConLai;
        TextView tvLichHoc;
        TextView tvTrangThai;
        LinearLayout lnlLichHoc;

        CheckBox cbDangKy;

        //
        public ViewHolder(View view) {
            this.tvMaHp = view.findViewById(R.id.txtMaHP);
            this.tvMaLTC = view.findViewById(R.id.txtMaLTC);
            this.tvTenHp = view.findViewById(R.id.txtTenHP);
            this.tvTenGv = view.findViewById(R.id.txtMaGV);
            this.tvSoTc = view.findViewById(R.id.txtSoTC);
            this.tvMaLop = view.findViewById(R.id.txtMaLop);
            this.tvSiSo = view.findViewById(R.id.txtSiso);
            this.cbDangKy = view.findViewById(R.id.cbDangKy);
            this.tvConLai = view.findViewById(R.id.txtSoluongconlai);
            this.tvLichHoc = view.findViewById(R.id.txtThoigian);
            this.lnlLichHoc = view.findViewById(R.id.lnlLichHoc);
            this.tvTrangThai = view.findViewById(R.id.tvTrangThai);
        }
    }
}
