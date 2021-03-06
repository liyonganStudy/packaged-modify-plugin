package com.netease.cloudmusic.commonlibrary;

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

public class CommonLibraryCustomView extends ImageView {
    private static final String TAG = "CommonLibraryCustomView";
    private int bgColor;

    public CommonLibraryCustomView(Context context) {
        super(context);
    }

    public CommonLibraryCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CommonLibraryCustomView, 0, 0);

        int count = attrs.getAttributeCount();
        for (int i = 0; i < count; i++) {
            String attrName = attrs.getAttributeName(i);
            String attrVal = attrs.getAttributeValue(i);
            Log.e(TAG, "attrName = " + attrName + " , attrVal = " + attrVal);

            Log.d(TAG, "attrId: " + Integer.toHexString(attrs.getAttributeNameResource(i)));
        }

        bgColor = a.getColor(R.styleable.CommonLibraryCustomView_bgColor, Color.BLUE);

        for (int i : R.styleable.CommonLibraryCustomView) {
            Log.i(TAG, "CommonLibraryCustomView: " + Integer.toHexString(i));
        }

        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(bgColor);
        super.onDraw(canvas);
    }
}
