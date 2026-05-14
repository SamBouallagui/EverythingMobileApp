package com.example.everything.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import androidx.recyclerview.widget.RecyclerView;
import com.example.everything.R;

public class AnimationUtils {
    
    // Card press animation
    public static void animateCardPress(View view, boolean isPressed) {
        float scale = isPressed ? 0.98f : 1.0f;
        float elevation = isPressed ? 2f : 8f;
        
        view.animate()
            .scaleX(scale)
            .scaleY(scale)
            .setDuration(150)
            .setInterpolator(new DecelerateInterpolator())
            .start();
            
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            view.animate()
                .translationZ(isPressed ? 0f : 8f)
                .setDuration(150)
                .start();
        }
    }
    
    // Like button animation
    public static void animateLike(View view) {
        AnimatorSet set = new AnimatorSet();
        
        ObjectAnimator scaleUp = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 1.4f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 1.4f);
        ObjectAnimator scaleDown = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.4f, 1f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.4f, 1f);
        
        scaleUp.setDuration(150);
        scaleUpY.setDuration(150);
        scaleDown.setDuration(200);
        scaleDownY.setDuration(200);
        scaleDown.setStartDelay(150);
        scaleDownY.setStartDelay(150);
        
        set.playTogether(scaleUp, scaleUpY, scaleDown, scaleDownY);
        set.setInterpolator(new OvershootInterpolator());
        set.start();
    }
    
    // Button click animation
    public static void animateButtonClick(View view) {
        AnimatorSet set = new AnimatorSet();
        
        ObjectAnimator scaleDown = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 0.95f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 0.95f);
        ObjectAnimator scaleUp = ObjectAnimator.ofFloat(view, View.SCALE_X, 0.95f, 1f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.95f, 1f);
        
        scaleDown.setDuration(100);
        scaleDownY.setDuration(100);
        scaleUp.setDuration(150);
        scaleUpY.setDuration(150);
        scaleUp.setStartDelay(100);
        scaleUpY.setStartDelay(100);
        
        set.playTogether(scaleDown, scaleDownY, scaleUp, scaleUpY);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }
    
    // RecyclerView item enter animation
    public static void animateItemEnter(View view, int position) {
        view.setAlpha(0f);
        view.setTranslationY(50f);
        
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(300)
            .setStartDelay(position * 50L)
            .setInterpolator(new DecelerateInterpolator())
            .start();
    }
    
    // FAB animation
    public static void animateFabAppear(View view) {
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.setAlpha(0f);
        
        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(400)
            .setInterpolator(new AnticipateOvershootInterpolator())
            .start();
    }
    
    // Pulse animation for badges
    public static void animatePulse(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 1.1f, 1f);
        
        scaleX.setDuration(600);
        scaleY.setDuration(600);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.start();
    }
    
    // Shimmer effect for loading
    public static void startShimmer(View view) {
        ObjectAnimator shimmer = ObjectAnimator.ofFloat(view, View.ALPHA, 0.3f, 1f, 0.3f);
        shimmer.setDuration(1500);
        shimmer.setRepeatCount(ValueAnimator.INFINITE);
        shimmer.start();
    }
    
    // Success confetti effect
    public static void animateSuccess(View view) {
        view.setScaleX(0.5f);
        view.setScaleY(0.5f);
        
        view.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(200)
            .setInterpolator(new OvershootInterpolator())
            .withEndAction(() -> {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .start();
            })
            .start();
    }
    
    // Fade in from bottom
    public static void fadeInFromBottom(View view) {
        view.setAlpha(0f);
        view.setTranslationY(100f);
        
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setInterpolator(new DecelerateInterpolator())
            .start();
    }
    
    // Cross fade between views
    public static void crossFade(View fromView, View toView, int duration) {
        toView.setAlpha(0f);
        toView.setVisibility(View.VISIBLE);
        
        toView.animate()
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(new DecelerateInterpolator())
            .start();
            
        fromView.animate()
            .alpha(0f)
            .setDuration(duration)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    fromView.setVisibility(View.GONE);
                }
            })
            .start();
    }
    
    // Rotate animation
    public static void rotate(View view, float degrees, long duration) {
        view.animate()
            .rotationBy(degrees)
            .setDuration(duration)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();
    }
    
    // Bounce animation
    public static void bounce(View view) {
        view.setTranslationY(-30f);
        view.animate()
            .translationY(0f)
            .setDuration(600)
            .setInterpolator(new BounceInterpolator())
            .start();
    }

    // Touch-based bounce effect (scale down on touch)
    @android.annotation.SuppressLint("ClickableViewAccessibility")
    public static void addBounceEffect(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    break;
            }
            return false;
        });
    }
}
