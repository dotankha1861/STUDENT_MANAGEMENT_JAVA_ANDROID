package com.example.studentmanagement.models.view;

import androidx.annotation.NonNull;

import java.io.Serializable;

// thông tin lớp tín chỉ
public class CreditClassItem implements Serializable {
    private String id;
    private String maLopTc;
    private String tenMh;
    private String maMh;
    private String tenGv;

    public CreditClassItem() {
    }

    public CreditClassItem(String id, String maLopTc, String tenMh, String maMh, String tenGv) {
        this.id = id;
        this.maLopTc = maLopTc;
        this.tenMh = tenMh;
        this.maMh = maMh;
        this.tenGv = tenGv;
    }

    public String getMaMh() {
        return maMh;
    }

    public void setMaMh(String maMh) {
        this.maMh = maMh;
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

    public String getTenGv() {
        return tenGv;
    }

    public void setTenGv(String tenGv) {
        this.tenGv = tenGv;
    }

    @NonNull
    @Override
    public String toString() {
        return this.tenMh + " " + this.getMaLopTc() + " " + this.tenGv;
    }

}
