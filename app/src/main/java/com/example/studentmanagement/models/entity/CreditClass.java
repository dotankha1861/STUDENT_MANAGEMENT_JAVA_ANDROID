package com.example.studentmanagement.models.entity;

import java.io.Serializable;

public class CreditClass implements Serializable {
    private String id;
    private String maLopTc;
    private String soLuong;
    private String soLuongCon;
    private String maMh;
    private String maGv;
    private String maLop;

    public CreditClass() {
    }

    public CreditClass(String id, String maLopTc, String soLuong, String soLuongCon, String maMh, String maGv, String maLop) {
        this.id = id;
        this.maLopTc = maLopTc;
        this.soLuong = soLuong;
        this.soLuongCon = soLuongCon;
        this.maMh = maMh;
        this.maGv = maGv;
        this.maLop = maLop;
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

    public String getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(String soLuong) {
        this.soLuong = soLuong;
    }

    public String getSoLuongCon() {
        return soLuongCon;
    }

    public void setSoLuongCon(String soLuongCon) {
        this.soLuongCon = soLuongCon;
    }

    public String getMaMh() {
        return maMh;
    }

    public void setMaMh(String maMh) {
        this.maMh = maMh;
    }

    public String getMaGv() {
        return maGv;
    }

    public void setMaGv(String maGv) {
        this.maGv = maGv;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }
}
