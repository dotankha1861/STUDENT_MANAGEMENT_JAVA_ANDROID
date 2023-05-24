package com.example.studentmanagement.models.requestbody;

import java.util.List;

public class RequestBodyEnroll {
    private String maSv;
    private List<String> maLopTcList;

    public String getMaSv() {
        return maSv;
    }

    public void setMaSv(String maSv) {
        this.maSv = maSv;
    }

    public List<String> getMaLopTcList() {
        return maLopTcList;
    }

    public void setMaLopTcList(List<String> maLopTcList) {
        this.maLopTcList = maLopTcList;
    }
}
