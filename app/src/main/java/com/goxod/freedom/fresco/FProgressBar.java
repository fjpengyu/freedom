package com.goxod.freedom.fresco;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.facebook.drawee.drawable.DrawableUtils;

/**
 * Created by Levey on 16/3/1.
 */
public class FProgressBar extends Drawable {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mBackgroundColor = 0x80000000;
    private int mColor = 0x80008066;
    private int mPadding = 0;
    private int mBarWidth = 10;
    private int mLevel = 0;
    private boolean mHideWhenZero = false;

    public void setColor(int color) {
        if (mColor != color) {
            mColor = color;
            invalidateSelf();
        }
    }

    public int getColor() {
        return mColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        if (mBackgroundColor != backgroundColor) {
            mBackgroundColor = backgroundColor;
            invalidateSelf();
        }
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setPadding(int padding) {
        if (mPadding != padding) {
            mPadding = padding;
            invalidateSelf();
        }
    }

    @Override
    public boolean getPadding(Rect padding) {
        padding.set(mPadding, mPadding, mPadding, mPadding);
        return mPadding != 0;
    }

    public void setBarWidth(int barWidth) {
        if (mBarWidth != barWidth) {
            mBarWidth = barWidth;
            invalidateSelf();
        }
    }

    public int getBarWidth() {
        return mBarWidth;
    }

    public void setHideWhenZero(boolean hideWhenZero) {
        mHideWhenZero = hideWhenZero;
    }

    public boolean getHideWhenZero() {
        return mHideWhenZero;
    }

    @Override
    protected boolean onLevelChange(int level) {
        mLevel = level;
        invalidateSelf();
        return true;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return DrawableUtils.getOpacityFromColor(mPaint.getColor());
    }

    @Override
    public void draw(Canvas canvas) {
        if (mHideWhenZero && mLevel == 0) {
            return;
        }
        drawBar(canvas, 10000, mBackgroundColor);
        drawBar(canvas, mLevel, 0x8000E066);
    }

    private void drawBar(Canvas canvas, int level, int color) {
        Rect bounds = getBounds();
        int length = (bounds.width() - 2 * mPadding) * level / 10000;
        int xPos = bounds.left + mPadding;
        int yPos = bounds.bottom - mPadding - mBarWidth;
        mPaint.setColor(color);
        canvas.drawRect(xPos, yPos, xPos + length, yPos + mBarWidth, mPaint);
    }
}
