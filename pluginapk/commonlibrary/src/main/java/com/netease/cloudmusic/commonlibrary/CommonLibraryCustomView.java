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

        bgColor = a.getColor(R.styleable.CommonLibraryCustomView_bgColor, Color.BLUE);
        String desc = a.getString(R.styleable.CommonLibraryCustomView_bgDescrip);

        Log.i(TAG, "CommonLibraryCustomView R.styleable.CommonLibraryCustomView_bgColor: 0x" + Integer.toHexString(R.styleable.CommonLibraryCustomView_bgColor));
        Log.i(TAG, "CommonLibraryCustomView bgColor: 0x" + Integer.toHexString(R.styleable.CommonLibraryCustomView_bgDescrip));
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(bgColor);
        super.onDraw(canvas);
    }
}
