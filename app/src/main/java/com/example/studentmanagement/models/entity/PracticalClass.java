package com.example.studentmanagement.models.entity;

import java.io.Serializable;
import java.util.Objects;

public class PracticalClass implements Serializable {
    private String id;
    private String maLop;
    private String tenLop;
    private String maKhoa;

    public PracticalClass() {
    }

    public PracticalClass(String id, String maLop, String tenLop, String maKhoa) {
        this.id = id;
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.maKhoa = maKhoa;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getTenLop() {
        return tenLop;
    }

    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }

    public String getMaKhoa() {
        return maKhoa;
    }

    public void setMaKhoa(String maKhoa) {
        this.maKhoa = maKhoa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PracticalClass)) return false;
        PracticalClass that = (PracticalClass) o;
        return maLop.equals(that.maLop);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maLop);
    }
}
