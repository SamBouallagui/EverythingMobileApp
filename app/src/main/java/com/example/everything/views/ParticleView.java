package com.example.everything.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleView extends View {
    private List<Particle> particles = new ArrayList<>();
    private Random random = new Random();
    private Paint paint = new Paint();
    private boolean isRunning = true;
    
    private static class Particle {
        float x, y;
        float size;
        float speedY;
        float speedX;
        float alpha;
        int color;
        
        Particle(float x, float y, float size, float speedX, float speedY, int color) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.speedX = speedX;
            this.speedY = speedY;
            this.alpha = 255;
            this.color = color;
        }
    }
    
    public ParticleView(Context context) {
        super(context);
        init();
    }
    
    public ParticleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public ParticleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createParticles(w, h);
    }
    
    private void createParticles(int width, int height) {
        particles.clear();
        int particleCount = 50;
        
        for (int i = 0; i < particleCount; i++) {
            particles.add(new Particle(
                random.nextFloat() * width,
                random.nextFloat() * height,
                2 + random.nextFloat() * 4,
                (random.nextFloat() - 0.5f) * 0.5f,
                (random.nextFloat() - 0.5f) * 0.5f,
                0xFF8B5CF6 + random.nextInt(0xFFFFFF)
            ));
        }
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        for (Particle particle : particles) {
            particle.x += particle.speedX;
            particle.y += particle.speedY;
            
            if (particle.x < 0) particle.x = getWidth();
            if (particle.x > getWidth()) particle.x = 0;
            if (particle.y < 0) particle.y = getHeight();
            if (particle.y > getHeight()) particle.y = 0;
            
            paint.setColor(particle.color);
            paint.setAlpha((int) (100 + random.nextFloat() * 155));
            canvas.drawCircle(particle.x, particle.y, particle.size, paint);
        }
        
        if (isRunning) {
            invalidate();
        }
    }
    
    public void stop() {
        isRunning = false;
    }
    
    public void start() {
        isRunning = true;
        invalidate();
    }
}
