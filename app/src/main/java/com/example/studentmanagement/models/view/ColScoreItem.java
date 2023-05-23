package com.example.studentmanagement.models.view;


// Khai báo và lấy thông tin.
public class ColScoreItem {
    private String tenCot;
    private String moTa;

    public ColScoreItem() {
    }

    public ColScoreItem(String tenCot, String moTa) {
        this.tenCot = tenCot;
        this.moTa = moTa;
    }

    public String getTenCot() {
        return tenCot;
    }

    public void setTenCot(String tenCot) {
        this.tenCot = tenCot;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    @Override
    public String toString() {
        return this.moTa;
    }
}
