package com.example.studentmanagement.models.view;

import java.util.Objects;

public class CourseItem {
    private String id;
    private String maMh;
    private String tenMh;
    private int soTc;

    public CourseItem() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaMh() {
        return maMh;
    }

    public void setMaMh(String maMh) {
        this.maMh = maMh;
    }

    public String getTenMh() {
        return tenMh;
    }

    public void setTenMh(String tenMh) {
        this.tenMh = tenMh;
    }

    public int getSoTc() {
        return soTc;
    }

    public void setSoTc(int soTc) {
        this.soTc = soTc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseItem)) return false;
        CourseItem that = (CourseItem) o;
        return maMh.equals(that.maMh);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maMh);
    }
}
