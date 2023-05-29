package com.example.studentmanagement.utils;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class FormatterDate {
    public static long MILLIS_OF_WEEK = 604_800_000L;
    public static long MILLIS_OF_DAY = 86_400_000L;
    public static String dd_slash_MM_slash_yyyy = "dd/MM/yyyy";

    public static String TIME_DATE = "hh:mm:ss dd/MM/yyyy";

    public static String yyyy_dash_MM_dash_dd = "yyyy-MM-dd";
    public static String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static String convertDate2String(Date date, String pattern){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter= new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    public static int getWeek(Date begin, Date current){
        return (int)((current.getTime() - begin.getTime())/MILLIS_OF_WEEK + 1);
    }

    public static int getDateOfWeek(Date date){
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        return  calendar.get(Calendar.DAY_OF_WEEK);
    }

    @NonNull
    public static Date getFirstDayOfWeek(Date begin, int week){
        return new Date(begin.getTime() + MILLIS_OF_WEEK * (week- 1));
    }
    public static Date getEndDayOfWeek(Date begin, int week){
        return new Date(getFirstDayOfWeek(begin, week).getTime() + MILLIS_OF_DAY * 6);
    }
    public static Date getAnyDayOfWeek(Date begin, int week, int nth){
        return new Date(getFirstDayOfWeek(begin, week).getTime() + MILLIS_OF_DAY * (nth-1));
    }
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
            SimpleDateFormat formatter=  new SimpleDateFormat(this.from);
            SimpleDateFormat formatter2 = new SimpleDateFormat(this.to);
            try {
                return formatter2.format(Objects.requireNonNull(formatter.parse(this.dateString)));
            } catch (ParseException e) {
                throw new RuntimeException();
            }
        }
    }
}
