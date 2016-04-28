package com.goxod.freedom.util.listener;

import android.view.View;

/**
 * Created by Levey on 2016/3/12.
 */
public abstract class NoDoubleClickListener implements View.OnClickListener{
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;
    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }

    }
    public abstract void onNoDoubleClick(View v);
}
