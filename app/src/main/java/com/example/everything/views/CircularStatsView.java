package com.example.everything.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.animation.ValueAnimator;

public class CircularStatsView extends View {
    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint textPaint;
    private Paint labelPaint;
    
    private float progress = 0f;
    private float targetProgress = 0f;
    private String label = "";
    private String value = "0";
    
    private RectF rectF;
    private float strokeWidth = 20f;
    
    public CircularStatsView(Context context) {
        super(context);
        init();
    }
    
    public CircularStatsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public CircularStatsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setColor(0x20C4B5FD); // Semi-transparent accent
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setColor(0xFFC4B5FD); // Accent color
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(0xFFF1F5F9); // Text primary
        textPaint.setTextSize(48f);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextAlign(Paint.Align.CENTER);
        
        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(0xFF94A3B8); // Text secondary
        labelPaint.setTextSize(24f);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        
        rectF = new RectF();
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float padding = strokeWidth / 2f;
        rectF.set(padding, padding, w - padding, h - padding);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = Math.min(centerX, centerY) - strokeWidth;
        
        // Draw background circle
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint);
        
        // Draw progress arc
        float sweepAngle = 360 * progress;
        canvas.drawArc(rectF, -90, sweepAngle, false, progressPaint);
        
        // Draw value text
        canvas.drawText(value, centerX, centerY - 10, textPaint);
        
        // Draw label text
        canvas.drawText(label, centerX, centerY + 35, labelPaint);
    }
    
    public void setProgress(float targetProgress, String value, String label) {
        this.targetProgress = Math.min(targetProgress, 1f);
        this.value = value;
        this.label = label;
        
        ValueAnimator animator = ValueAnimator.ofFloat(0f, this.targetProgress);
        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            progress = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }
    
    public void setProgressColor(int color) {
        progressPaint.setColor(color);
        invalidate();
    }
}
