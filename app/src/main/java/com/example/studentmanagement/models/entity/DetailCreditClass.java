package com.example.studentmanagement.models.entity;

import java.io.Serializable;

public class DetailCreditClass implements Serializable {
    private String id;
    private int tiet;
    private String thu;
    private int soTiet;
    private String phong;
    private String timeBd;
    private String timeKt;
    public DetailCreditClass() {
    }

    public DetailCreditClass(String id, int tiet, String thu, int soTiet, String phong) {
        this.id = id;
        this.tiet = tiet;
        this.thu = thu;
        this.soTiet = soTiet;
        this.phong = phong;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTiet() {
        return tiet;
    }

    public void setTiet(int tiet) {
        this.tiet = tiet;
    }

    public String getThu() {
        return thu;
    }

    public void setThu(String thu) {
        this.thu = thu;
    }

    public int getSoTiet() {
        return soTiet;
    }

    public void setSoTiet(int soTiet) {
        this.soTiet = soTiet;
    }

    public String getPhong() {
        return phong;
    }

    public void setPhong(String phong) {
        this.phong = phong;
    }

    public String getTimeBd() {
        return timeBd;
    }

    public void setTimeBd(String timeBd) {
        this.timeBd = timeBd;
    }

    public String getTimeKt() {
        return timeKt;
    }

    public void setTimeKt(String timeKt) {
        this.timeKt = timeKt;
    }
}
