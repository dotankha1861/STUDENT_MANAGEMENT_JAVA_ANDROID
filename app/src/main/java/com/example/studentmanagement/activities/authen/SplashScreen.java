package com.example.studentmanagement.activities.authen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanagement.R;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    private final int sleepTime = 5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash_screen);
        Thread thread = new Thread(){
            @Override
            public void run(){
                try{
                    sleep(5000);
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}