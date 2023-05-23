package com.example.studentmanagement.models.view;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class PracticalClassItem implements Serializable {
    private String id;
    private String maLop;
    private String tenLop;

    public PracticalClassItem() {}

    public PracticalClassItem(String maLop, String tenLop) {
        this.maLop = maLop;
        this.tenLop = tenLop;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getTenLop() {
        return tenLop;
    }

    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PracticalClassItem)) return false;
        PracticalClassItem that = (PracticalClassItem) o;
        return maLop.equals(that.maLop);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maLop);
    }

    @NonNull
    @Override
    public String toString() {
        return this.maLop + " - " + this.tenLop;
    }
}
