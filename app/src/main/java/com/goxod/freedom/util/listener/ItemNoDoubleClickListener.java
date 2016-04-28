package com.goxod.freedom.util.listener;

import android.view.View;
import android.widget.AdapterView;

/**
 * Created by Levey on 2016/1/28.
 */
public abstract class ItemNoDoubleClickListener implements AdapterView.OnItemClickListener {

    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onItemNoDoubleClick(parent,view,position,id);
        }
    }

    public abstract void onItemNoDoubleClick(AdapterView<?> parent, View view, int position, long id);
}

