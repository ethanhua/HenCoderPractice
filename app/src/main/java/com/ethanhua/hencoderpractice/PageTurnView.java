package com.ethanhua.hencoderpractice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by ethanhua on 2017/10/14.
 */

public class PageTurnView extends View {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.maps);
    private Camera camera = new Camera();
    private int degreeY1 = 0;
    private int degreeZ = 0;
    private int degreeY2 = 0;
    private ObjectAnimator animStep1 = ObjectAnimator.ofInt(this, "degreeY1", 0, -45);
    private ObjectAnimator animStep2 = ObjectAnimator.ofInt(this, "degreeZ", 0, 270);
    private ObjectAnimator animStep3 = ObjectAnimator.ofInt(this, "degreeY2", 0, 45);
    private AnimatorSet animatorSet = new AnimatorSet();

    public PageTurnView(Context context) {
        super(context);
    }

    public PageTurnView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PageTurnView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PageTurnView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        animStep1.setDuration(2500);
        animStep2.setDuration(2500);
        animStep3.setDuration(2500);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        animatorSet.playSequentially(animStep1, animStep2, animStep3);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reset();
                        animatorSet.start();
                    }
                }, 500);
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        animatorSet.end();
    }

    public void setDegreeZ(int degreeZ) {
        this.degreeZ = degreeZ;
        invalidate();
    }

    public void setDegreeY1(int degreeY) {
        this.degreeY1 = degreeY;
        invalidate();
    }

    public void setDegreeY2(int degreeY) {
        this.degreeY2 = degreeY;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int x = centerX - bitmapWidth / 2;
        int y = centerY - bitmapHeight / 2;


        // 第一部分绘制：初始状态时 左半边部分（第三段动画时有3D旋转）
        canvas.save();
        canvas.rotate(-degreeZ, centerX, centerY); //最后旋转回来

        camera.save();
        camera.rotateY(degreeY2);       //再3D变换
        canvas.translate(centerX, centerY);
        camera.applyToCanvas(canvas);
        canvas.translate(-centerX, -centerY);
        camera.restore();

        canvas.clipRect(0, 0, x + bitmapWidth / 2, centerY * 2); //再裁切
        canvas.rotate(degreeZ, centerX, centerY); //先旋转
        canvas.drawBitmap(bitmap, x, y, paint);
        canvas.restore();


        // 第二部分绘制：初始状态时 右半边部分 (第一段动画时有3D旋转并保持3D旋转角度到最后)
        canvas.save();
        canvas.rotate(-degreeZ, centerX, centerY); //最后旋转回来

        camera.save();
        camera.rotateY(degreeY1);       //再3D变换
        canvas.translate(centerX, centerY);
        camera.applyToCanvas(canvas);
        canvas.translate(-centerX, -centerY);
        camera.restore();

        canvas.clipRect(x + bitmapWidth / 2, 0, centerX * 2, centerY * 2); //再裁切
        canvas.rotate(degreeZ, centerX, centerY); //先旋转

        canvas.drawBitmap(bitmap, x, y, paint);
        canvas.restore();
    }

    private void reset() {
        degreeY1 = 0;
        degreeZ = 0;
        degreeY2 = 0;
    }
}
