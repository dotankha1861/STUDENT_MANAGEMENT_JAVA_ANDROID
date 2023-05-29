package com.example.studentmanagement.firebase;

import java.util.Date;

public class NotificationData{
    private Long timeStamp;
    private Long prioritySort;
    private String title;
    private String body;

    public NotificationData() {
    }

    public NotificationData(String title, String body, Long timeStamp) {
        this.title = title;
        this.body = body;
        this.timeStamp = timeStamp;
        this.prioritySort = Long.MAX_VALUE - timeStamp;
    }

    public Long getPrioritySort() {
        return prioritySort;
    }

    public void setPrioritySort(Long prioritySort) {
        this.prioritySort = prioritySort;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "NotificationData{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
