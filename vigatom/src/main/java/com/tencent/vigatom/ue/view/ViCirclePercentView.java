package com.tencent.vigatom.ue.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.tencent.vigatom.R;

/**
 * 圆形的百分比View
 */
public class ViCirclePercentView extends View {

    private int svp_color;
    private Paint paint;
    private RectF oval;
    private float startAngle = -90;
    private float sweepAngle;
    private int svp_percent;


    public ViCirclePercentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CirclePercent, 0, 0);
        try {
            svp_color = a.getColor(R.styleable.CirclePercent_circle_color, 60000000);
            svp_percent = a.getInteger(R.styleable.CirclePercent_circle_percent, 0);
        } finally {
            a.recycle();
        }
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(svp_color);
        updateArgs(svp_percent * 3.6f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float xp = (float) (getPaddingLeft() + getPaddingRight());
        float yp = (float) (getPaddingBottom() + getPaddingTop());
        float wwd = (float) w - xp;
        float hhd = (float) h - yp;
        oval = new RectF(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + wwd, getPaddingTop() + hhd);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(oval, startAngle, sweepAngle, true, paint);
    }

    /**
     * 设置百分比
     *
     * @param percent 百分比
     */
    public void setPercent(int percent) {
        final float degree = percent * 3.6f;
        post(() -> {
            updateArgs(degree);
            refreshTheLayout();
        });
    }

    /**
     * 刷新
     */
    private void refreshTheLayout() {
        invalidate();
        requestLayout();
    }

    /**
     * 刷新百分比角度
     *
     * @param degree 角度
     */
    private void updateArgs(float degree) {
        sweepAngle = degree;
    }

}
