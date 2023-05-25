package com.example.studentmanagement.firebase;


import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class UpLoadImage {
    public static final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images");
    public static UploadTask saveImageToDatabase(String code, Uri uri){
        return storageRef.child(code).putFile(uri);
    }
}
