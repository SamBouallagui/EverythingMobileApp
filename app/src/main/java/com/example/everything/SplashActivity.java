package com.example.everything;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Connect to xml file
        setContentView(R.layout.activity_splash);
        //
        new Handler(Looper.getMainLooper()).postDelayed(() ->{
            Intent intent = new Intent(SplashActivity.this, AuthActivity.class);
            //Navigate to AuthActivity
            startActivity(intent);
            //Destroy splashActivity from memory
            finish();

        },2000);
    }
}