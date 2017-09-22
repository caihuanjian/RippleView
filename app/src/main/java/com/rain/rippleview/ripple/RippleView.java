package com.rain.rippleview.ripple;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by HwanJ.Choi on 2017-9-22.
 */

public class RippleView extends View {

    enum Status {
        Open, close
    }

    private static final int DEFUALT_COLOR = Color.GRAY;
    private static final int DEFUALT_DURATION = 300;

    private Point mOrinalPoint;

    private float mCurRadius;

    private float mMaxRadius;

    private float centerX;

    private float centerY;

    private boolean isAnim;

    private ObjectAnimator mAnimtor;

    private Paint mPaint;

    private Status mCurState = Status.close;

    public RippleView(Context context) {
        this(context, null);
    }

    public RippleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(DEFUALT_COLOR);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isAnim && mCurState == Status.Open) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        } else {
            canvas.drawCircle(centerX, centerY, mCurRadius, mPaint);
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeCap(Paint.Cap.ROUND);
            p.setColor(Color.RED);
            p.setStrokeWidth(5);
            canvas.drawPoint(centerX, centerY, p);
        }
    }

    @Override
    public void setBackgroundColor(@ColorInt int color) {
        mPaint.setColor(color);
        invalidate();
    }

    public void open(Point point) {
        if (point == null || isAnim)
            return;
        mCurState = Status.Open;
        isAnim = true;
        mOrinalPoint = point;
        int startX = point.x;
        int startY = point.y;
        PropertyValuesHolder xHolder = PropertyValuesHolder.ofFloat("centerX", startX, getWidth() / 2);
        PropertyValuesHolder YHolder = PropertyValuesHolder.ofFloat("centerY", startY, getHeight() / 2);
        PropertyValuesHolder radiusHolder = PropertyValuesHolder.ofFloat("radius", 0, mMaxRadius);
        mAnimtor = ObjectAnimator.ofPropertyValuesHolder(this, xHolder, YHolder, radiusHolder);
        mAnimtor.setDuration(DEFUALT_DURATION);
        mAnimtor.setInterpolator(new AccelerateInterpolator());
        mAnimtor.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnim = false;
                if (mListener != null) {
                    mListener.onOpen();
                }
            }
        });
        mAnimtor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
        mAnimtor.start();
    }

    public void close() {
        if (mOrinalPoint == null || isAnim) {
            return;
        }
        mCurState = Status.close;
        isAnim = true;
        mCurRadius = mMaxRadius;
        float startX = getWidth() / 2;
        float startY = getHeight() / 2;
        PropertyValuesHolder xHolder = PropertyValuesHolder.ofFloat("centerX", startX, mOrinalPoint.x);
        PropertyValuesHolder YHolder = PropertyValuesHolder.ofFloat("centerY", startY, mOrinalPoint.y);
        PropertyValuesHolder radiusHolder = PropertyValuesHolder.ofFloat("radius", mMaxRadius, 0);
        mAnimtor = ObjectAnimator.ofPropertyValuesHolder(this, xHolder, YHolder, radiusHolder);
        mAnimtor.setDuration(DEFUALT_DURATION);
        mAnimtor.setInterpolator(new AccelerateInterpolator());
        mAnimtor.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnim = false;
                if (mListener != null) {
                    mListener.onClose();
                }
            }
        });
        mAnimtor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
        mAnimtor.start();
    }

    private void setCenterX(float x) {
        centerX = x;
    }

    private void setCenterY(float y) {
        centerY = y;
    }

    private void setRadius(float radius) {
        this.mCurRadius = radius;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mMaxRadius = (float) Math.sqrt(w / 2 * w / 2 + h / 2 * h / 2);
    }

    private onStateChangedListener mListener;

    public void setStateChangedListener(onStateChangedListener l) {
        mListener = l;
    }

    public interface onStateChangedListener {
        void onOpen();

        void onClose();
    }
}
