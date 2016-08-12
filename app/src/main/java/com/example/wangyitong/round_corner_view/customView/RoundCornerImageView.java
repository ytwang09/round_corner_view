package com.example.wangyitong.round_corner_view.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.example.wangyitong.round_corner_view.R;

/**
 * Created by wangyitong on 2016/8/8.
 */

public class RoundCornerImageView extends View {
    private enum ImageScaleType {
        NONE, INNER, OUTER
    }

    private static final int default_radius = 20;
    private static final ScaleType[] sScaleTypes = {
            ScaleType.FIT_CENTER,
            ScaleType.FIT_START,
            ScaleType.FIT_XY,
            ScaleType.CENTER_CLIP,
            ScaleType.CENTER_INSIDE,
            ScaleType.CENTER,
            ScaleType.MATRIX
    };

    private Drawable mDrawable;
    private int mRadius;
    private ScaleType mScaleType;

    private int mViewWidth;
    private int mViewHeight;
    private int mDrawableWidth;
    private int mDrawableHeight;

    private int mDrawableFinalWidth;
    private int mDrawableFinalHeight;
    private int mRoundRectWidth;
    private int mRoundRectHeight;

    private Paint mPaint;

    public RoundCornerImageView(Context context) {
        this(context, null);
    }

    public RoundCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initialize();
    }

    private void initialize() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (mDrawable != null) {
            mDrawableWidth = mDrawable.getIntrinsicWidth();
            mDrawableHeight = mDrawable.getIntrinsicHeight();
        }
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RoundCornerImageView);
        mDrawable = typedArray.getDrawable(R.styleable.RoundCornerImageView_src);
        mRadius = typedArray.getDimensionPixelSize(R.styleable.RoundCornerImageView_radius, default_radius);
        int scaleTypeIndex = typedArray.getInt(R.styleable.RoundCornerImageView_scaleType, ScaleType.CENTER.mTypeIndex);
        mScaleType = sScaleTypes[scaleTypeIndex - 1];
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mDrawable == null) {
            return;
        }
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mViewWidth = widthSize;
        } else {
            mViewWidth = mDrawableWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mViewHeight = heightSize;
        } else {
            mViewHeight = mDrawableHeight;
        }
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawable == null || mViewWidth == 0 || mViewHeight == 0) {
            return;
        }
        calculateFinalSizeNPosition();
        canvas.drawBitmap(getRoundCornerBitmap(), mLeft, mTop, null);
    }

    private int mLeft = 0;
    private int mTop = 0;

    private void calculateFinalSizeNPosition() {
        switch (mScaleType) {
            case FIT_XY:
                updateDrawableSize(getRealViewWidth(), getRealViewHeight());
                break;
            case MATRIX:
                updateDrawableSize(mDrawableWidth, mDrawableHeight);
                break;
            case CENTER:
                updateDrawableSize(mDrawableWidth, mDrawableHeight);
                measureCenter();
                break;
            case FIT_START:
                updateDrawableSize(getRealViewWidth(), getRealViewHeight(), ImageScaleType.INNER);
                break;
            case FIT_CENTER:
                updateDrawableSize(getRealViewWidth(), getRealViewHeight(), ImageScaleType.INNER);
                measureCenter();
                break;
            case CENTER_CLIP:
                updateDrawableSize(getRealViewWidth(), getRealViewHeight(), ImageScaleType.OUTER);
                measureCenter();
                break;
            case CENTER_INSIDE:
                if (mDrawableWidth < getRealViewWidth() && mDrawableHeight < getRealViewHeight()) {
                    // mScaleType == CENTER
                    updateDrawableSize(mDrawableWidth, mDrawableHeight);
                    measureCenter();
                    break;
                }
                // mScaleType == FIT_CENTER
                updateDrawableSize(getRealViewWidth(), getRealViewHeight(), ImageScaleType.INNER);
                measureCenter();
                break;
        }
    }

    private void measureCenter() {
        mLeft = Math.round((getRealViewWidth() - mRoundRectWidth) * 0.5f) + getPaddingLeft();
        mTop = Math.round((getRealViewHeight() - mRoundRectHeight) * 0.5f) + getPaddingTop();
    }

    private int getRealViewHeight() {
        return mViewHeight - getPaddingTop() - getPaddingBottom();
    }

    private int getRealViewWidth() {
        return mViewWidth - getPaddingLeft() - getPaddingRight();
    }

    private void updateDrawableSize(int viewRealWidth, int viewRealHeight) {
        updateDrawableSize(viewRealWidth, viewRealHeight, ImageScaleType.NONE);
    }

    private void updateDrawableSize(int realW, int realH, ImageScaleType type) {
        mDrawableFinalHeight = realH;
        mDrawableFinalWidth = realW;
        float ratio = 1.0f;
        float ratioHeight = 1.0f * mDrawableFinalHeight / mDrawableHeight;
        float ratioWidth = 1.0f * mDrawableFinalWidth / mDrawableWidth;
        switch (type) {
            case NONE:
                break;
            case INNER:
                ratio = ratioHeight > ratioWidth ? ratioWidth : ratioHeight;
                mDrawableFinalHeight = (int) (mDrawableHeight * ratio);
                mDrawableFinalWidth = (int) (mDrawableWidth * ratio);
                break;
            case OUTER:
                ratio = ratioHeight < ratioWidth ? ratioWidth : ratioHeight;
                mDrawableFinalHeight = (int) (mDrawableHeight * ratio);
                mDrawableFinalWidth = (int) (mDrawableWidth * ratio);
                break;
        }
        mRoundRectWidth = getSmallerSize(mDrawableFinalWidth, getRealViewWidth());
        mRoundRectHeight = getSmallerSize(mDrawableFinalHeight, getRealViewHeight());
    }

    private int getSmallerSize(int sizeA, int sizeB) {
        return sizeA > sizeB ? sizeB : sizeA;
    }

    @NonNull
    private Bitmap getRoundCornerBitmap() {
        Bitmap roundCorner = Bitmap.createBitmap(mRoundRectWidth, mRoundRectHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundCorner);
        RectF rectF = new RectF(0, 0, mRoundRectWidth, mRoundRectHeight);
        canvas.drawRoundRect(rectF, mRadius, mRadius, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        int left = (mRoundRectWidth - mDrawableFinalWidth) / 2;
        int top = (mRoundRectHeight - mDrawableFinalHeight) / 2;
        canvas.drawBitmap(getBmFromDrawable(),
                mScaleType == ScaleType.MATRIX ? 0 : left,
                mScaleType == ScaleType.MATRIX ? 0 : top, mPaint);
        mPaint.setXfermode(null);
        return roundCorner;
    }

    @NonNull
    private Bitmap getBmFromDrawable() {
        Bitmap.Config bmConfig = mDrawable.getOpacity() != PixelFormat.OPAQUE ?
                Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(mDrawableFinalWidth, mDrawableFinalHeight, bmConfig);
        Canvas bmCanvas = new Canvas(bitmap);
        mDrawable.setBounds(0, 0, mDrawableFinalWidth, mDrawableFinalHeight);
        mDrawable.draw(bmCanvas);
        return bitmap;
    }

    private enum ScaleType {
        FIT_CENTER(1),
        FIT_START(2),
        FIT_XY(3),
        CENTER_CLIP(4),
        CENTER_INSIDE(5),
        CENTER(6),
        MATRIX(7);

        private int mTypeIndex;

        ScaleType(int index) {
            mTypeIndex = index;
        }
    }
}
