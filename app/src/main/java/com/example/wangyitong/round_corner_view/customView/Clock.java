package com.example.wangyitong.round_corner_view.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.example.wangyitong.round_corner_view.R;

import java.util.Calendar;

/**
 * Created by wangyitong on 2016/8/12.
 * <p>
 * clock : hour minute second needle; time scale; numbers of time scale
 * <p>
 * center: middle of view
 * mDialPlateRadius
 * refresh-interval
 */

public class Clock extends View {
    private static final int sDefaultSize = 100;
    private static final int TANS_TO_SECOND = 1000;
    private static final int HAND_TYPE_SECOND = 1;
    private static final int HAND_TYPE_MINITE = 2;
    private static final int HAND_TYPE_HOUR = 4;
    private int mWidth;
    private int mHeight;
    private int mCenterX;
    private int mCenterY;
    private int mDialPlateRadius;
    private Paint mPaint;

    public Clock(Context context) {
        this(context, null);
    }

    public Clock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            mWidth = width > sDefaultSize ? width : sDefaultSize;
        } else {
            mWidth = sDefaultSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            int height = MeasureSpec.getSize(heightMeasureSpec);
            mHeight = height > sDefaultSize ? height : sDefaultSize;
        } else {
            mHeight = sDefaultSize;
        }
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
        mDialPlateRadius = mWidth < mHeight ? mCenterX : mCenterY;
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(getResources().getColor(R.color.colorPrimary));

        drawDialPlate(canvas);

        drawWatchHand(canvas, HAND_TYPE_HOUR, R.color.lightGray);
        drawWatchHand(canvas, HAND_TYPE_MINITE, R.color.darkGray);
        drawWatchHand(canvas, HAND_TYPE_SECOND, R.color.white);

        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(mCenterX, mCenterY, 8, mPaint);

        postInvalidateDelayed(1000);
    }

    private static final int DOT_COUNT = 60;
    private static final int FLAG_HOUR = 5;
    private static final int SCALE_PADDING = 16;
    private static final int SCALE_GAP = 8;
    private static final int sLargeScaleLength = 32;
    private static final int sLargeScaleWidth = 2;
    private static final int sSmallScaleDot = 4;
    private static final int scaleTextSize = 32;

    private void drawDialPlate(Canvas canvas) {
        canvas.save();
        canvas.drawCircle(mCenterX, mCenterY, mDialPlateRadius, mPaint);

        mPaint.setColor(Color.WHITE);
        for (int i = 0; i < DOT_COUNT; i++) {
            if (i % FLAG_HOUR == 0) {
                canvas.drawRect(mCenterX - sLargeScaleWidth, SCALE_PADDING,
                        mCenterX + sLargeScaleWidth, sLargeScaleLength + SCALE_PADDING,
                        mPaint);
                mPaint.setTextSize(scaleTextSize);
                String scaleText = i / FLAG_HOUR == 0 ? 12 + "" : i / FLAG_HOUR + "";
                Rect textBounds = new Rect();
                mPaint.getTextBounds(scaleText, 0, scaleText.length(), textBounds);
                int textHeight = textBounds.bottom - textBounds.top;
                int textWidth = textBounds.right - textBounds.left;
                canvas.drawText(scaleText, mCenterX - textWidth / 2 ,
                        sLargeScaleLength +SCALE_PADDING + SCALE_GAP + textHeight, mPaint);
            } else {
                canvas.drawCircle(mCenterX, SCALE_PADDING + sSmallScaleDot, sSmallScaleDot, mPaint);
            }
            canvas.rotate(360.0f / DOT_COUNT, mCenterX, mCenterY);
        }
        canvas.restore();
    }

    private static final float HOUR_ANGLE_UNIT = 30.0f;
    private static final float SEC_MIN_ANGLE_UNIT = 6.0f;

    private void drawWatchHand(Canvas canvas, int handType, int paintColorRes) {
        canvas.save();
        mPaint.setColor(getResources().getColor(paintColorRes));
        float rotateAngle;
        if (handType == HAND_TYPE_HOUR) {
            rotateAngle = HOUR_ANGLE_UNIT * Calendar.getInstance().get(Calendar.HOUR);
        } else if (handType == HAND_TYPE_MINITE) {
            rotateAngle = SEC_MIN_ANGLE_UNIT * Calendar.getInstance().get(Calendar.MINUTE);
        } else {
            rotateAngle = SEC_MIN_ANGLE_UNIT * Calendar.getInstance().get(Calendar.SECOND);
        }
        canvas.rotate(rotateAngle, mCenterX, mCenterY);
        RectF watchHandRect = getRectF(handType);
        canvas.drawRoundRect(watchHandRect, handType, handType, mPaint);
        canvas.restore();
    }

    @NonNull
    private RectF getRectF(int handType) {
        return new RectF(mCenterX - handType,
                handType * 40,
                mCenterX + handType,
                mCenterY + 16 * handType);
    }
}
