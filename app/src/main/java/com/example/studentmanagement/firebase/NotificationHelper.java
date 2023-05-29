package com.example.studentmanagement.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.studentmanagement.MyApplication;
import com.example.studentmanagement.R;
import com.example.studentmanagement.activities.home.HomeStudentActivity;
import com.example.studentmanagement.activities.notification.ViewNotificationActivity;

import java.util.Date;

public class NotificationHelper {

    public static void displayNotification(Context context, String title, String subTitle,String body) {
        Intent intent = new Intent(context, ViewNotificationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MyApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.khoa_icon)
                .setContentTitle(title)
                .setSubText(subTitle)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Hiển thị thông báo
        notificationManager.notify(getId(), builder.build());
    }

    private static int getId() {
        return (int) new Date().getTime();
    }
}
