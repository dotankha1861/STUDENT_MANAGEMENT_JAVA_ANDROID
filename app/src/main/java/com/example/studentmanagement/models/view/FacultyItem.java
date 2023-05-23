package com.example.studentmanagement.models.view;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class FacultyItem implements Serializable {
    private String id;
    private String maKhoa;
    private String tenKhoa;

    public FacultyItem() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaKhoa() {
        return maKhoa;
    }

    public void setMaKhoa(String maKhoa) {
        this.maKhoa = maKhoa;
    }

    public String getTenKhoa() {
        return tenKhoa;
    }

    public void setTenKhoa(String tenKhoa) {
        this.tenKhoa = tenKhoa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FacultyItem)) return false;
        FacultyItem falcultyItemLv = (FacultyItem) o;
        return maKhoa.equals(falcultyItemLv.maKhoa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKhoa);
    }

    @NonNull
    @Override
    public String toString() {
        return this.maKhoa + " - " + this.tenKhoa;
    }
}
