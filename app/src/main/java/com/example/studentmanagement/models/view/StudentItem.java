package com.example.studentmanagement.models.view;

import java.io.Serializable;
import java.util.Objects;

public class StudentItem implements Serializable {
    private String id;
    private String maSv;
    private String ho;
    private String ten;

    private String phai;
    private String hinhAnh;

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public StudentItem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaSv() {
        return maSv;
    }

    public void setMaSv(String maSv) {
        this.maSv = maSv;
    }

    public String getHo() {
        return ho;
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getPhai() {
        return phai;
    }

    public void setPhai(String phai) {
        this.phai = phai;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentItem)) return false;
        StudentItem that = (StudentItem) o;
        return maSv.equals(that.maSv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSv);
    }
}
