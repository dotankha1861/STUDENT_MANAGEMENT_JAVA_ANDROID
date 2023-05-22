package com.example.studentmanagement.models.entity;

import java.io.Serializable;
import java.util.Objects;

public class Faculty implements Serializable {
    private String id;
    private String maKhoa;
    private String tenKhoa;
    private String sdt;
    private String email;

    public Faculty() {
    }

    public Faculty(String maKhoa, String tenKhoa, String sdt, String email, String id) {
        this.maKhoa = maKhoa;
        this.tenKhoa = tenKhoa;
        this.sdt = sdt;
        this.email = email;
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

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Faculty)) return false;
        Faculty faculty = (Faculty) o;
        return maKhoa.equals(faculty.maKhoa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKhoa);
    }

}
