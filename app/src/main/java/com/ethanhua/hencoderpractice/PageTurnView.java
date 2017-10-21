package com.ethanhua.hencoderpractice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by ethanhua on 2017/10/14.
 *
 * 整个动画分3部分
 *
 * 1 将坐标系沿Y轴3D旋转-45的动画
 * 2 将坐标系沿着 3D轴线Y 2D旋转后形成的新轴线 3D旋转-45投影的动画
 * 3 将坐标系沿Y轴3D旋转45的动画
 */

public class PageTurnView extends View {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.maps);
    private Camera camera = new Camera();
    private int degree3DY1 = 0;
    private int degreeZ = 0;
    private int degree3DY2 = 0;
    private ObjectAnimator animStep1 = ObjectAnimator.ofInt(this, "degree3DY1", 0, -45);
    private ObjectAnimator animStep2 = ObjectAnimator.ofInt(this, "degreeZ", 0, 270);
    private ObjectAnimator animStep3 = ObjectAnimator.ofInt(this, "degree3DY2", 0, 45);
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

    {
        animStep1.setDuration(1500);
        animStep2.setDuration(1500);
        animStep3.setDuration(1500);
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

    public void setDegree3DY1(int degreeY) {
        this.degree3DY1 = degreeY;
        invalidate();
    }

    public void setDegree3DY2(int degreeY) {
        this.degree3DY2 = degreeY;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int bmpWidth = bitmap.getWidth();
        int bmpHeight = bitmap.getHeight();

        //将画布坐标系平移至View中心
        canvas.translate(getWidth() / 2, getHeight() / 2);


        //绘制初始位置右半边


        canvas.save();
        //1 对坐标系先2D旋转
        canvas.rotate(-degreeZ);
        //2 对坐标系进行3D绕Y轴旋转
        camera.save();
        camera.rotateY(degree3DY1);
        camera.applyToCanvas(canvas);
        //3 裁切
        canvas.clipRect(0, -bmpHeight, bmpWidth, bmpHeight);
        //4 恢复2D旋转
        canvas.rotate(degreeZ);
        //5 绘制图形
        canvas.drawBitmap(bitmap, -bmpWidth / 2, -bmpHeight / 2, paint);
        camera.restore();
        canvas.restore();

        //绘制初始位置左半边

        canvas.save();
        //1 对坐标系先2D旋转
        canvas.rotate(-degreeZ);
        //2 对坐标系进行3D绕Y轴旋转
        camera.save();
        camera.rotateY(degree3DY2);
        camera.applyToCanvas(canvas);
        //3 裁切
        canvas.clipRect(-bmpWidth, -bmpHeight, 0, bmpHeight);
        //4 恢复2D旋转
        canvas.rotate(degreeZ);
        //5 绘制图形
        canvas.drawBitmap(bitmap, -bmpWidth / 2, -bmpHeight / 2, paint);
        camera.restore();
        canvas.restore();

    }

    private void reset() {
        degree3DY1 = 0;
        degreeZ = 0;
        degree3DY2 = 0;
    }
}
