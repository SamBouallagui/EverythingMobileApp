package com.example.everything;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;

public class CommunityTheme {
    @DrawableRes
    private final int iconResId;
    @ColorRes
    private final int gradientStart;
    @ColorRes
    private final int gradientEnd;

    public CommunityTheme(int iconResId, int gradientStart, int gradientEnd) {
        this.iconResId = iconResId;
        this.gradientStart = gradientStart;
        this.gradientEnd = gradientEnd;
    }

    public int getIconResId() {
        return iconResId;
    }

    public int getGradientStart() {
        return gradientStart;
    }

    public int getGradientEnd() {
        return gradientEnd;
    }
}
