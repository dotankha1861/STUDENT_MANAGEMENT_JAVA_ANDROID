package com.example.studentmanagement.models.view;

import java.io.Serializable;

public class ActivityItem implements Serializable {
    private String tenMh;
    private int tiet;
    private int soTiet;
    private String tenGv;
    private String phong;
    private String maLopTc;
    public ActivityItem() {
    }

    public String getTenMh() {
        return tenMh;
    }

    public void setTenMh(String tenMh) {
        this.tenMh = tenMh;
    }

    public int getTiet() {
        return tiet;
    }

    public void setTiet(int tiet) {
        this.tiet = tiet;
    }

    public int getSoTiet() {
        return soTiet;
    }

    public void setSoTiet(int soTiet) {
        this.soTiet = soTiet;
    }

    public String getTenGv() {
        return tenGv;
    }

    public void setTenGv(String tenGv) {
        this.tenGv = tenGv;
    }

    public String getPhong() {
        return phong;
    }

    public void setPhong(String phong) {
        this.phong = phong;
    }

    public String getMaLopTc() {
        return maLopTc;
    }

    public void setMaLopTc(String maLopTc) {
        this.maLopTc = maLopTc;
    }
}
