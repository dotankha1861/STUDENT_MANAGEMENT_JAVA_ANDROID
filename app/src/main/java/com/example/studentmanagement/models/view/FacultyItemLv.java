package com.example.studentmanagement.models.view;

import java.io.Serializable;
import java.util.Objects;

public class FacultyItemLv implements Serializable {
    private String id;
    private String maKhoa;
    private String tenKhoa;

    public FacultyItemLv() {}

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
        if (!(o instanceof FacultyItemLv)) return false;
        FacultyItemLv falcultyItemLv = (FacultyItemLv) o;
        return maKhoa.equals(falcultyItemLv.maKhoa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKhoa);
    }

}
