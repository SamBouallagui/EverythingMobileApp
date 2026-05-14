package com.example.everything;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Connect to xml file
        setContentView(R.layout.activity_splash_enhanced);

        // Initialize Particle View for background animation
        com.example.everything.views.ParticleView particleView = findViewById(R.id.particleView);
        if (particleView != null) {
            particleView.start();
        }

        // Animate the app name — fade in + scale
        TextView tvAppName = findViewById(R.id.tvAppName);
        TextView tvTagLine = findViewById(R.id.tvTagLine);

        tvAppName.setAlpha(0f);
        tvTagLine.setAlpha(0f);

        // App name: scale + fade
        AnimationSet nameAnim = new AnimationSet(true);
        nameAnim.setInterpolator(new DecelerateInterpolator());

        ScaleAnimation scale = new ScaleAnimation(
                0.8f, 1.0f, 0.8f, 1.0f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(800);

        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(800);

        nameAnim.addAnimation(scale);
        nameAnim.addAnimation(fadeIn);
        nameAnim.setFillAfter(true);

        tvAppName.startAnimation(nameAnim);
        tvAppName.setAlpha(1f);

        // Tagline: delayed fade in
        tvTagLine.postDelayed(() -> {
            AlphaAnimation tagFadeIn = new AlphaAnimation(0f, 1f);
            tagFadeIn.setDuration(600);
            tagFadeIn.setFillAfter(true);
            tvTagLine.startAnimation(tagFadeIn);
            tvTagLine.setAlpha(1f);
        }, 500);

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
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, 2000);
    }
}