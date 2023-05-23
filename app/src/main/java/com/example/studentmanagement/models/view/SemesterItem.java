package com.example.studentmanagement.models.view;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;


public class SemesterItem implements Serializable {
    private String maKeHoach;
    private int ky;
    private int nam;
    private Date timeStudyBegin;
    private Date timeStudyEnd;

    public SemesterItem() {
    }

    public SemesterItem(String maKeHoach, int ky, int nam, Date timeStudyBegin, Date timeStudyEnd) {
        this.maKeHoach = maKeHoach;
        this.ky = ky;
        this.nam = nam;
        this.timeStudyBegin = timeStudyBegin;
        this.timeStudyEnd = timeStudyEnd;
    }

    public String getMaKeHoach() {
        return maKeHoach;
    }

    public void setMaKeHoach(String maKeHoach) {
        this.maKeHoach = maKeHoach;
    }

    public int getKy() {
        return ky;
    }

    public void setKy(int ky) {
        this.ky = ky;
    }

    public int getNam() {
        return nam;
    }

    public void setNam(int nam) {
        this.nam = nam;
    }

    public Date getTimeStudyBegin() {
        return timeStudyBegin;
    }

    public void setTimeStudyBegin(Date timeStudyBegin) {
        this.timeStudyBegin = timeStudyBegin;
    }

    public Date getTimeStudyEnd() {
        return timeStudyEnd;
    }

    public void setTimeStudyEnd(Date timeStudyEnd) {
        this.timeStudyEnd = timeStudyEnd;
    }

    @NonNull
    @Override
    public String toString(){
        return "Kỳ " + this.ky + " - Năm: " + this.nam + " - " + (this.nam + 1);
    }
}
