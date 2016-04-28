package com.goxod.freedom.fresco;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.goxod.freedom.R;


/**
 * Created by Levey on 2016/3/2.
 */
public class Drawables {
    public static void init(final Resources resources) {
        if (sPlaceholderDrawable == null) {
            sPlaceholderDrawable = resources.getDrawable(R.drawable.loading);
        }
        if (sErrorDrawable == null) {
            sErrorDrawable = resources.getDrawable(R.drawable.error);
        }
    }
    public static Drawable sPlaceholderDrawable;
    public static Drawable sErrorDrawable;
    private Drawables() {
    }
}
