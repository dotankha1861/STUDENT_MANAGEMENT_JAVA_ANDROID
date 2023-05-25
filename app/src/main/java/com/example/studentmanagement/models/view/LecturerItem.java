package com.example.studentmanagement.models.view;

import java.io.Serializable;
import java.util.Objects;

public class LecturerItem implements Serializable {
    private String id;
    private String maGv;
    private String ho;
    private String ten;
    private String phai;
    private String hinhAnh;

    public LecturerItem() {
    }

    public LecturerItem(String id, String maGv, String ho, String ten, String phai) {
        this.id = id;
        this.maGv = maGv;
        this.ho = ho;
        this.ten = ten;
        this.phai = phai;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaGv() {
        return maGv;
    }

    public void setMaGv(String maGv) {
        this.maGv = maGv;
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
        if (!(o instanceof LecturerItem)) return false;
        LecturerItem lecturerItem = (LecturerItem) o;
        return maGv.equals(lecturerItem.maGv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maGv);
    }
}
