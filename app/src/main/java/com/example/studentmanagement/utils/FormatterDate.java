package com.example.studentmanagement.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class FormatterDate {
    public static String dd_slash_MM_slash_yyyy = "dd/MM/yyyy";

    public static String yyyy_dash_MM_dash_dd = "yyyy-MM-dd";
    public static String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    public static class Fomatter {
        String from;
        String to;
        String dateString;

        public Fomatter(String dateString){
            this.dateString = dateString;
        }

        public FormatterDate.Fomatter from(String from){
            this.from = from;
            return this;
        }

        public FormatterDate.Fomatter to(String to){
            this.to = to;
            return this;
        }

        @SuppressLint("SimpleDateFormat")
        public String format(){
            SimpleDateFormat formatter=new SimpleDateFormat(this.from);
            SimpleDateFormat formatter2 = new SimpleDateFormat(this.to);
            try {
                return formatter2.format(Objects.requireNonNull(formatter.parse(this.dateString)));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
