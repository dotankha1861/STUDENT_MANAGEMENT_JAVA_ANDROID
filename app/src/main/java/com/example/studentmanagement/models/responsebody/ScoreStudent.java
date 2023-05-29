package com.example.studentmanagement.models.responsebody;

import java.io.Serializable;

public class ScoreStudent implements Serializable {
    private String maMh;
    private String tenMh;
    private float cc;
    private float gk;
    private float ck;
    private float tb;
    private String xepLoai;
    private float percentCc;
    private float percentGk;
    private float percentCk;
    private String maLopTc;

    public ScoreStudent() {
    }

    public ScoreStudent(String maMh, String tenMh, float cc, float gk, float ck, float tb, String xepLoai, float percentCc, float percentGk, float percentCk) {
        this.maMh = maMh;
        this.tenMh = tenMh;
        this.cc = cc;
        this.gk = gk;
        this.ck = ck;
        this.tb = tb;
        this.xepLoai = xepLoai;
        this.percentCc = percentCc;
        this.percentGk = percentGk;
        this.percentCk = percentCk;
    }

    public String getMaLopTc() {
        return maLopTc;
    }

    public void setMaLopTc(String maLopTc) {
        this.maLopTc = maLopTc;
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

    public float getPercentCc() {
        return percentCc;
    }

    public void setPercentCc(float percentCc) {
        this.percentCc = percentCc;
    }

    public float getPercentGk() {
        return percentGk;
    }

    public void setPercentGk(float percentGk) {
        this.percentGk = percentGk;
    }

    public float getPercentCk() {
        return percentCk;
    }

    public void setPercentCk(float percentCk) {
        this.percentCk = percentCk;
    }
}
