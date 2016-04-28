package com.goxod.freedom.view.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Levey on 16/3/23.
 */
public class IconView extends TextView {
    public IconView(Context context) {
        super(context);
        initFont(context);
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFont(context);
    }

    public IconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFont(context);
    }

    private void initFont(Context context){
        setTypeface(Typeface.createFromAsset(context.getAssets(), "fontawesome.ttf"));
    }
}
