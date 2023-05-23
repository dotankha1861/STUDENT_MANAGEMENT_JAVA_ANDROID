package com.example.studentmanagement.utils;

import com.example.studentmanagement.models.view.ColScoreItem;

import java.util.ArrayList;
import java.util.List;

public class ColsScore {
    private static final List<ColScoreItem> listColScore;
    static {
        listColScore = new ArrayList<>();
        //add list điểm
        listColScore.add(new ColScoreItem("CC", "Điểm chuyên cần"));
        listColScore.add(new ColScoreItem("GK", "Điểm giữa kì"));
        listColScore.add(new ColScoreItem("CK", "Điểm cuối kỳ"));
        listColScore.add(new ColScoreItem("TB", "Điểm tổng kết"));
        listColScore.add(new ColScoreItem("XEPLOAI", "Điểm chữ"));
    }
    public static List<ColScoreItem> getListColScore() {
        return listColScore;
    }
}
