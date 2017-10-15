package com.ethanhua.hencoderpractice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import static android.graphics.BitmapFactory.decodeResource;

/**
 * Created by ethanhua on 2017/10/14.
 */

public class PraiseButton extends View {

    private static final int NUM_PART_FIXED = 0;
    private static final int NUM_PART_ROLL_OLD = 1;
    private static final int NUM_PART_ROLL_NEW = 2;

    private boolean mIsRolling = false;
    private int mSpace = 10;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);


    private int mNumber = 0;
    private int mTextColor = Color.BLACK;
    private int mRippleColor = Color.parseColor("#28FF4081");
    private float mTextSize = 30;
    private boolean mChecked = false;

    private final Matrix matrix = new Matrix();
    private final Bitmap unSelectBitmap = decodeResource(getResources(), R.drawable.ic_messages_like_unselected);
    private final Bitmap selectBitmap = decodeResource(getResources(), R.drawable.ic_messages_like_selected);
    private final Bitmap shiningBitmap = decodeResource(getResources(), R.drawable.ic_messages_like_selected_shining);

    private float praiseIconScale = 1;
    private float shiningIconScale = 1;
    private float rippleScale = 1;
    private float textOffYScale = 0;
    private final ObjectAnimator mTextRollAnim = ObjectAnimator.ofFloat(this, "textOffYScale", 0, 1f);
    private final ObjectAnimator mPraiseIconScaleAnim = ObjectAnimator.ofFloat(this, "praiseIconScale", 0.80f, 1.08f, 1f);
    private final ObjectAnimator mShiningIconScaleAnim = ObjectAnimator.ofFloat(this, "shiningIconScale", 0f, 1.1f, 1f);
    private final ObjectAnimator mRippleScaleAnim = ObjectAnimator.ofFloat(this, "rippleScale", 1f, 3f);

    private final String[] mNumPart = new String[3];
    private float mTextHeight;
    private float mTextBaseLineHeight;


    private float mSelectBmpX;
    private float mSelectBmpY;
    private float mUnSelectBmpX;
    private float mUnSelectBmpY;
    private float mShiningBmpX;
    private float mShiningBmpY;


    public PraiseButton(Context context) {
        this(context, null);
    }

    public PraiseButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PraiseButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PraiseButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PraiseButton);
        mNumber = a.getInt(R.styleable.PraiseButton_text, mNumber);
        mTextColor = a.getColor(R.styleable.PraiseButton_textColor, mTextColor);
        mTextSize = a.getDimension(R.styleable.PraiseButton_textSize, mTextSize);
        mRippleColor = a.getColor(R.styleable.PraiseButton_rippleColor, mRippleColor);
        mChecked = a.getBoolean(R.styleable.PraiseButton_checked, mChecked);
        a.recycle();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setStrokeWidth(4);


        mNumPart[NUM_PART_FIXED] = "";
        mNumPart[NUM_PART_ROLL_OLD] = String.valueOf(mNumber);
        mNumPart[NUM_PART_ROLL_NEW] = "";
        measureNumberText();
        measurePraiseBitmap();
    }

    private void measurePraiseBitmap() {
        mSelectBmpX = 0;
        mSelectBmpY = getDefaultHeight() / 2 - selectBitmap.getHeight() / 2;
        mUnSelectBmpX = 0;
        mUnSelectBmpY = getDefaultHeight() / 2 - unSelectBitmap.getHeight() / 2;
        mShiningBmpX = selectBitmap.getWidth() / 2 - shiningBitmap.getWidth() / 2;
        mShiningBmpY = mSelectBmpY - shiningBitmap.getHeight() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPraiseIcon(canvas);
        drawRippleCircle(canvas);
        drawNumber(canvas);
    }

    private void drawPraiseIcon(Canvas canvas) {
        //绘制点赞图标
        canvas.save();
        if (mChecked) {
            matrix.reset();
            matrix.setTranslate(mSelectBmpX, mSelectBmpY);
            matrix.postScale(praiseIconScale, praiseIconScale, selectBitmap.getWidth() / 2, selectBitmap.getHeight() / 2);
            canvas.drawBitmap(selectBitmap, matrix, mPaint);
            //绘制点赞图标上方的闪光icon
            matrix.reset();
            matrix.setTranslate(mShiningBmpX, mShiningBmpY);
            matrix.postScale(shiningIconScale, shiningIconScale, shiningBitmap.getWidth() / 2, shiningBitmap.getHeight() / 2);
            canvas.drawBitmap(shiningBitmap, matrix, mPaint);
        } else {
            matrix.reset();
            matrix.setTranslate(mUnSelectBmpX, mUnSelectBmpY);
            matrix.postScale(praiseIconScale, praiseIconScale, unSelectBitmap.getWidth() / 2, unSelectBitmap.getHeight() / 2);
            canvas.drawBitmap(unSelectBitmap, matrix, mPaint);
        }
        canvas.restore();
    }

    private void drawRippleCircle(Canvas canvas) {
        //绘制扩散圆圈部分
        if (mChecked && rippleScale < 2) {
            canvas.save();
            mPaint.setColor(mRippleColor);
            mPaint.setStrokeWidth(6);
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(selectBitmap.getWidth() / 2, getHeight() / 2, rippleScale * selectBitmap.getWidth() / 2 - 6, mPaint);
            mPaint.reset();
            canvas.restore();
        }
    }

    private void drawNumber(Canvas canvas) {
        canvas.save();
        canvas.clipRect(0, getDefaultHeight() / 2 - mTextHeight / 2, getDefaultWidth(), getDefaultHeight() / 2 + mTextHeight / 2);
        //绘制固定数字部分
        canvas.drawText(mNumPart[NUM_PART_FIXED], offX(true), getDefaultHeight() / 2 + mTextBaseLineHeight / 2, mTextPaint);
        //绘制动态数字部分
        int direction = mChecked ? -1 : 1;
        canvas.drawText(mNumPart[NUM_PART_ROLL_OLD], offX(false), getDefaultHeight() / 2 + mTextBaseLineHeight / 2 + direction * textOffYScale * mTextHeight, mTextPaint);
        canvas.drawText(mNumPart[NUM_PART_ROLL_NEW], offX(false), getDefaultHeight() / 2 + mTextBaseLineHeight / 2 + (direction * textOffYScale * mTextHeight - direction * mTextHeight), mTextPaint);
        canvas.restore();
    }

    private float offX(boolean isFix) {
        if (isFix) {
            return mSpace + selectBitmap.getWidth();
        }
        return mSpace + selectBitmap.getWidth() + mTextPaint.measureText(mNumPart[NUM_PART_FIXED]);
    }

    public void setChecked(boolean checked) {
        if (this.mChecked == checked || mIsRolling) {
            return;
        }
        this.mChecked = checked;
        textOffYScale = 0;
        mIsRolling = true;
        splitNumToFixedAndRollPart(mChecked ? 1 : -1);
        mPraiseIconScaleAnim.start();
        if (mChecked) {
            mShiningIconScaleAnim.start();
            mRippleScaleAnim.start();
        }
        mTextRollAnim.start();
        mTextRollAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mTextRollAnim.removeListener(this);
                mIsRolling = false;
            }
        });
    }

    public void setText(int number) {
        if (mNumber == number) {
            return;
        }
        mNumber = number;
        splitNumToFixedAndRollPart(mChecked ? 1 : -1);
        invalidate();
    }

    public void setTextOffYScale(float scale) {
        textOffYScale = scale;
        invalidate();
    }

    public void setPraiseIconScale(float praiseIconScale) {
        this.praiseIconScale = praiseIconScale;
        invalidate();
    }

    public void setShiningIconScale(float scale) {
        this.shiningIconScale = scale;
        invalidate();
    }

    public void setRippleScale(float scale) {
        this.rippleScale = scale;
        invalidate();
    }

    public boolean isChecked() {
        return mChecked;
    }

    private void measureNumberText() {
        Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
        mTextHeight = metrics.bottom - metrics.top;
        mTextBaseLineHeight = -metrics.ascent;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTextRollAnim.end();
        mPraiseIconScaleAnim.end();
        mShiningIconScaleAnim.end();
        mRippleScaleAnim.end();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(getDefaultWidth(), getDefaultHeight());
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(getDefaultWidth(), heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, getDefaultHeight());
        }
    }

    private int getDefaultWidth() {
        return (int) (mTextPaint.measureText(mNumber + "0") + 2 * mSpace + selectBitmap.getWidth());
    }

    private int getDefaultHeight() {
        return (int) (Math.max(selectBitmap.getHeight() + shiningBitmap.getHeight(), mTextHeight) + 2 * mSpace);
    }

    private void splitNumToFixedAndRollPart(int add) {
        String oldNumStr = String.valueOf(mNumber);
        String newNumStr = String.valueOf(mNumber + add);
        mNumber = mNumber + add;
        int oldNumLength = oldNumStr.length();
        int newNumLength = newNumStr.length();
        if (oldNumLength != newNumLength) {  //如果发生进位或者退位 则num全部是滚动部分
            mNumPart[0] = "";
            mNumPart[1] = oldNumStr;
            mNumPart[2] = newNumStr;
            return;
        }
        //如果变化前后数字长度相同，则从高位开始比较，到哪一位数字不同，则这一位前面的部分为固定部分，后面（包含自己）为滚动部分
        for (int i = 0; i < oldNumLength; i++) {
            if (newNumStr.charAt(i) != oldNumStr.charAt(i)) {
                mNumPart[0] = oldNumStr.substring(0, i);
                mNumPart[1] = oldNumStr.substring(i);
                mNumPart[2] = newNumStr.substring(i);
                break;
            }
        }
    }
}
