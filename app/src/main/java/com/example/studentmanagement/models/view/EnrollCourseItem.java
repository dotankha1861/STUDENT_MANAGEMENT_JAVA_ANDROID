package com.example.studentmanagement.models.view;

import androidx.annotation.NonNull;

import com.example.studentmanagement.utils.StatusEnroll;

import java.io.Serializable;
import java.util.Objects;

public class EnrollCourseItem implements Serializable {
    private String id;
    private String maLopTc;
    private String tenMh;
    private String maLop;
    private String tenGv;
    private int soTc;
    private int soLuong;
    private int soLuongCon;
    private String maMh;
    private Boolean isVisibleCT;
    private StatusEnroll statusEnroll;
    Boolean checked;

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public StatusEnroll getStatusEnroll() {
        return statusEnroll;
    }

    public void setStatusEnroll(StatusEnroll statusEnroll) {
        this.statusEnroll = statusEnroll;
    }

    public Boolean getVisibleCT() {
        return isVisibleCT;
    }

    public void setVisibleCT(Boolean visibleCT) {
        isVisibleCT = visibleCT;
    }

    public int getSoTc() {
        return soTc;
    }

    public void setSoTc(int soTc) {
        this.soTc = soTc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaLopTc() {
        return maLopTc;
    }

    public void setMaLopTc(String maLopTc) {
        this.maLopTc = maLopTc;
    }

    public String getTenMh() {
        return tenMh;
    }

    public void setTenMh(String tenMh) {
        this.tenMh = tenMh;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getTenGv() {
        return tenGv;
    }

    public void setTenGv(String tenGv) {
        this.tenGv = tenGv;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public int getSoLuongCon() {
        return soLuongCon;
    }

    public void setSoLuongCon(int soLuongCon) {
        this.soLuongCon = soLuongCon;
    }

    public String getMaMh() {
        return maMh;
    }

    public void setMaMh(String maMh) {
        this.maMh = maMh;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EnrollCourseItem)) return false;
        EnrollCourseItem that = (EnrollCourseItem) o;
        return maLopTc.equals(that.maLopTc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maLopTc);
    }

    @NonNull
    @Override
    public String toString() {
        return this.maLopTc + " " + this.tenMh + " " + this.getTenGv() + " " + this.statusEnroll;
    }
}
