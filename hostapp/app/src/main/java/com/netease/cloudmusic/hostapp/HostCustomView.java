package com.netease.cloudmusic.hostapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by hzliyongan on 2018/3/1.
 */

public class HostCustomView extends ImageView {
    private static final String TAG = "HostCustomView";
    private int bgColor;

    public HostCustomView(Context context) {
        super(context);
    }

    public HostCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        int count = attrs.getAttributeCount();
        for (int i = 0; i < count; i++) {
            String attrName = attrs.getAttributeName(i);
            String attrVal = attrs.getAttributeValue(i);
            Log.e(TAG, "attrName = " + attrName + " , attrVal = " + attrVal);

            Log.e(TAG, "attrId: " + Integer.toHexString(attrs.getAttributeNameResource(i)));
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HostCustomView, 0, 0);
        bgColor = a.getColor(R.styleable.HostCustomView_hostBgColor, Color.BLUE);

        for (int i : R.styleable.HostCustomView) {
            Log.i(TAG, "HostCustomView: " + Integer.toHexString(i));
        }

        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(bgColor);
        super.onDraw(canvas);
    }
}
