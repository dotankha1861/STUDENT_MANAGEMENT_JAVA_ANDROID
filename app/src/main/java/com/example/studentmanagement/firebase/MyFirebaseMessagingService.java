package com.example.studentmanagement.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;
import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessaging";
//    private static final String DATABASE_PATH = "https://console.firebase.google.com/u/1/project/noti-e29f2/database/noti-e29f2-default-rtdb/data/~2F";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "đã nhận thông báo");
        // Xử lý khi nhận được thông báo từ giảng viên
        remoteMessage.getData();
        Map<String, String> stringMap = remoteMessage.getData();
        String title = stringMap.get("title");
        String subtitle = stringMap.get("subtitle");
        String body = stringMap.get("body");

        Log.d(TAG, title + " " + subtitle + " " + " " + body);
        // Hiển thị thông báo
        NotificationHelper.displayNotification(getApplicationContext(), title, subtitle, body);

//            // Lưu trữ thông báo vào Firebase Realtime Database
//            saveNotificationToDatabase(title, body);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Lưu trữ token của sinh viên vào Firebase Realtime Database
         Log.d(TAG, "New Token: " + token);
    }

    public static void saveTokenToDatabase(String key, String token) {
        // Lấy tham chiếu đến Firebase Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Lưu trữ token vào đường dẫn "tokens/student_token"
        String studentTokenPath = "tokens/" + key;
        databaseRef.child(studentTokenPath).setValue(token);
    }
    public static void removeTokenToDatabase(String key) {
        // Lấy tham chiếu đến Firebase Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Lưu trữ token vào đường dẫn "tokens/student_token"
        String studentTokenPath = "tokens/" + key;
        databaseRef.child(studentTokenPath).removeValue();
    }

    public static void saveNotificationToDatabase(String creditClassCode, String lecturerName, NotificationData notificationData) {
        // Lấy tham chiếu đến Firebase Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("creditClasses/" + creditClassCode);
        databaseRef.child("priority").setValue(notificationData.getPrioritySort());
        databaseRef.child("updatedTimeStamp").setValue(notificationData.getTimeStamp());
        databaseRef.child("lecturer").setValue(lecturerName);
        String notificationKey = databaseRef.push().getKey();
        if(notificationKey!=null){
            databaseRef.child("notifications/" + notificationKey).setValue(notificationData);
        }
    }
}