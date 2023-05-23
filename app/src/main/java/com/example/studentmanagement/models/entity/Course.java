package com.example.studentmanagement.models.entity;

import java.io.Serializable;
import java.util.Objects;

public class Course implements Serializable {
    private String id;
    private String maMh;
    private String tenMh;
    private int percentCc;
    private int  percentGk;
    private int percentCk;
    private int  soTietLt;
    private int  soTietTh;
    private int soTc;
    private String maKhoa;

    public Course() {
    }

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

    public int getPercentCc() {
        return percentCc;
    }

    public void setPercentCc(int percentCc) {
        this.percentCc = percentCc;
    }

    public int getPercentGk() {
        return percentGk;
    }

    public void setPercentGk(int percentGk) {
        this.percentGk = percentGk;
    }

    public int getPercentCk() {
        return percentCk;
    }

    public void setPercentCk(int percentCk) {
        this.percentCk = percentCk;
    }

    public int getSoTietLt() {
        return soTietLt;
    }

    public void setSoTietLt(int soTietLt) {
        this.soTietLt = soTietLt;
    }

    public int getSoTietTh() {
        return soTietTh;
    }

    public void setSoTietTh(int soTietTh) {
        this.soTietTh = soTietTh;
    }

    public int getSoTc() {
        return soTc;
    }

    public void setSoTc(int soTc) {
        this.soTc = soTc;
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
        if (!(o instanceof Course)) return false;
        Course course = (Course) o;
        return maMh.equals(course.maMh);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maMh);
    }
}
