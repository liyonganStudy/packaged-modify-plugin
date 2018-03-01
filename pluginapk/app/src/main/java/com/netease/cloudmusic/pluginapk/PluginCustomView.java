package com.netease.cloudmusic.pluginapk;

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

public class PluginCustomView extends ImageView {
    private static final String TAG = "PluginCustomView";
    public PluginCustomView(Context context) {
        super(context);
    }

    public PluginCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PluginCustomView, 0, 0);
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.GRAY);
        super.onDraw(canvas);
    }
}
