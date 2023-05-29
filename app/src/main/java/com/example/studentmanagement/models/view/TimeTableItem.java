package com.example.studentmanagement.models.view;

import java.io.Serializable;
import java.util.List;

public class TimeTableItem implements Serializable {
    private String thu;
    private String ngay;
    private List<ActivityItem> tkbDtoList;

    public String getThu() {
        return thu;
    }

    public void setThu(String thu) {
        this.thu = thu;
    }

    public List<ActivityItem> getTkbDtoList() {
        return tkbDtoList;
    }

    public void setTkbDtoList(List<ActivityItem> tkbDtoList) {
        this.tkbDtoList = tkbDtoList;
    }

    public String getNgay() {
        return ngay;
    }

    public void setNgay(String ngay) {
        this.ngay = ngay;
    }
}
