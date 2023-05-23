package com.example.studentmanagement.utils;

import java.util.HashMap;
import java.util.Map;

public class StatusStudent {
    public static Map<Integer, String> status;

    static {
        status = new HashMap<>();
        status.put(0, "Còn học");
        status.put(1, "Đã nghỉ");
    }
}
