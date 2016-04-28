package com.goxod.freedom.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.utils.AutoLayoutHelper;

/**
 * Created by Levey on 2016/3/6.
 * Author: Zhy
 * Site: https://github.com/hongyangAndroid/AndroidAutoLayout
 */
public class AutoSpinner extends Spinner {

    private final AutoLayoutHelper mHelper = new AutoLayoutHelper(this);

    public AutoSpinner(Context context) {
        super(context);
    }
    public AutoSpinner(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AutoSpinner(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public AutoFrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new AutoFrameLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if (!isInEditMode())
        {
            mHelper.adjustChildren();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
