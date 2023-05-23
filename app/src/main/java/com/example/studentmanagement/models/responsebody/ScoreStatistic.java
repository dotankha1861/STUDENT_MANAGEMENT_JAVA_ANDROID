package com.example.studentmanagement.models.responsebody;

//
public class ScoreStatistic {
    private String type;
    private int soLuong;

    public ScoreStatistic() {
    }

    public ScoreStatistic(String type, int soLuong) {
        this.type = type;
        this.soLuong = soLuong;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }
}
