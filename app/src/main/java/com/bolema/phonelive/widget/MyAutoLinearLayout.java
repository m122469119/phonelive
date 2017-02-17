package com.bolema.phonelive.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.zhy.autolayout.AutoLinearLayout;

/**
 * Created by yuanshuo on 2017/2/16.
 */

public class MyAutoLinearLayout extends AutoLinearLayout {
    public MyAutoLinearLayout(Context context) {
        super(context);
    }

    public MyAutoLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyAutoLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyAutoLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
