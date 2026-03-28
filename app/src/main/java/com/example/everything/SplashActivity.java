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
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager session = new SessionManager(SplashActivity.this);

            Intent intent;
            if (session.isLoggedIn()) {
                // user already logged in, skip auth
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                //navigate to auth
                intent = new Intent(SplashActivity.this, AuthActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2000);
    }
}