package com.example.studentmanagement.models.responsebody;

import java.io.Serializable;

public class ScoreCreditClass implements Serializable {
    private String id;
    private String maSv;
    private String tenSv;
    private float cc;
    private float gk;
    private float ck;
    private float tb;
    private String xepLoai;

    public ScoreCreditClass() {
    }

    public ScoreCreditClass(String id, String maSv, String tenSv, float cc, float gk, float ck, float tb, String xepLoai) {
        this.id = id;
        this.maSv = maSv;
        this.tenSv = tenSv;
        this.cc = cc;
        this.gk = gk;
        this.ck = ck;
        this.tb = tb;
        this.xepLoai = xepLoai;
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

    public String getTenSv() {
        return tenSv;
    }

    public void setTenSv(String tenSv) {
        this.tenSv = tenSv;
    }

    public float getCc() {
        return cc;
    }

    public void setCc(float cc) {
        this.cc = cc;
    }

    public float getGk() {
        return gk;
    }

    public void setGk(float gk) {
        this.gk = gk;
    }

    public float getCk() {
        return ck;
    }

    public void setCk(float ck) {
        this.ck = ck;
    }

    public float getTb() {
        return tb;
    }

    public void setTb(float tb) {
        this.tb = tb;
    }

    public String getXepLoai() {
        return xepLoai;
    }

    public void setXepLoai(String xepLoai) {
        this.xepLoai = xepLoai;
    }
}
