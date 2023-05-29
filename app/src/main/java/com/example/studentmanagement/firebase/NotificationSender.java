package com.example.studentmanagement.firebase;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class NotificationSender {
    private static final String SERVER_KEY = "AAAAd-UoqtY:APA91bFQvxsM-STjZjuxyX0Lv8TxFJU0AL8m5UrvuVtdfsWbH1TsA99Fu8djLbo-cKYy8uThhNAIjWVhYQKtRVZsh1oUAKY--I6hmJYxyAKXTPHunYuXMGEFW5uajlMKW5xB5gvD70xX";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void sendNotification(final String creditClassCode, final  String lecturerName, final NotificationData notificationData, final List<String> studentTokens) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject();
            JSONObject dataJson = new JSONObject();
            try {
                dataJson.put("title", notificationData.getTitle());
                dataJson.put("subtitle", "GV:" + lecturerName + "-LopTC:" + creditClassCode);
                dataJson.put("body", notificationData.getBody());
                json.put("data", dataJson);
                json.put("registration_ids", new JSONArray(studentTokens));
                RequestBody requestBody = RequestBody.create(JSON, json.toString());

                Request request = new Request.Builder()
                        .url("https://fcm.googleapis.com/fcm/send")
                        .post(requestBody)
                        .addHeader("Authorization", "key=" + SERVER_KEY)
                        .addHeader("Content-Type", "application/json")
                        .build();

                client.newCall(request).execute();

                MyFirebaseMessagingService.saveNotificationToDatabase(creditClassCode, lecturerName, notificationData);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
