package com.ethanhua.hencoderpractice.zhihuadverts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;

/**
 * Created by ethanhua on 2017/11/12.
 */
public class SplitAnimationUtil {

    public static Bitmap mBitmap = null;
    private static int[] mLoc1;
    private static int[] mLoc2;
    private static ImageView mTopImage;
    private static ImageView mBottomImage;
    private static AnimatorSet mSetAnim;
    private static WeakReference<Activity> firstActivity;
    private static ImageView mBackHolderImage;

    public static void startActivity(Activity currActivity,
                                     Intent intent,
                                     int splitYTop,
                                     int splitYBottom) {

        // Preparing the bitmaps that we need to show
        firstActivity = new WeakReference<Activity>(currActivity);
        prepare(currActivity, splitYTop, splitYBottom);
        currActivity.startActivity(intent);
        currActivity.overridePendingTransition(0, 0);
    }

    public static void prepareAnimation(final Activity destActivity) {
        mTopImage = createImageView(destActivity, mBitmap, mLoc1);
        mBottomImage = createImageView(destActivity, mBitmap, mLoc2);
    }

    public static void prepareBackAnimation(Activity destActivity) {
        if (firstActivity.get() == null) {
            return;
        }
        Bitmap bmp = screenShot(destActivity, false);
        int[] loc = new int[]{0, bmp.getHeight()};
        mBackHolderImage = createImageView(firstActivity.get(), bmp, loc);
        mTopImage = createImageView(firstActivity.get(), mBitmap, mLoc1);
        mBottomImage = createImageView(firstActivity.get(), mBitmap, mLoc2);

    }

    public static void animate(final Activity destActivity,
                               final int duration,
                               final TimeInterpolator interpolator) {

        new Handler().post(new Runnable() {

            @Override
            public void run() {
                mSetAnim = new AnimatorSet();
                mTopImage.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                mBottomImage.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                mSetAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        clean(destActivity);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        clean(destActivity);
                    }
                });

                // Animating the 2 parts away from each other
                Animator anim1 = ObjectAnimator.ofFloat(mTopImage,
                        "translationY",
                        mTopImage.getHeight() * -1);
                Animator anim2 = ObjectAnimator.ofFloat(mBottomImage,
                        "translationY",
                        mBottomImage.getHeight());

                if (interpolator != null) {
                    anim1.setInterpolator(interpolator);
                    anim2.setInterpolator(interpolator);
                }

                mSetAnim.setDuration(duration);
                mSetAnim.playTogether(anim1, anim2);
                mSetAnim.start();
            }
        });
    }

    public static void backAnimate(final int duration,
                                   final TimeInterpolator interpolator) {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                mSetAnim = new AnimatorSet();
                mTopImage.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                mBottomImage.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                mSetAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        clean(firstActivity.get());
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        clean(firstActivity.get());
                        firstActivity.clear();
                    }
                });
                // Animating the 2 parts away from each other
                Animator anim1 = ObjectAnimator.ofFloat(mTopImage,
                        "translationY",
                        -mTopImage.getHeight(),
                        0);
                Animator anim2 = ObjectAnimator.ofFloat(mBottomImage,
                        "translationY",
                        mBottomImage.getHeight(), 0);

                if (interpolator != null) {
                    anim1.setInterpolator(interpolator);
                    anim2.setInterpolator(interpolator);
                }

                mSetAnim.setDuration(duration);
                mSetAnim.playTogether(anim1, anim2);
                mSetAnim.start();
            }
        });
    }

    public static void backAnimate(final int duration) {
        backAnimate(duration, new DecelerateInterpolator());

    }

    public static void animate(final Activity destActivity,
                               final int duration) {
        animate(destActivity, duration, new DecelerateInterpolator());
    }

    public static void cancel() {
        if (mSetAnim != null) {
            mSetAnim.cancel();
        }
    }

    private static void clean(Activity activity) {
        if (mBackHolderImage != null) {
            mBackHolderImage.setLayerType(View.LAYER_TYPE_NONE, null);
            try {
                activity.getWindowManager().removeViewImmediate(mBackHolderImage);
            } catch (Exception ignored) {
            }
        }
        if (mTopImage != null) {
            mTopImage.setLayerType(View.LAYER_TYPE_NONE, null);
            try {
                // If we use the regular removeView() we'll get a small UI glitch
                activity.getWindowManager().removeViewImmediate(mBottomImage);
            } catch (Exception ignored) {
            }
        }
        if (mBottomImage != null) {
            mBottomImage.setLayerType(View.LAYER_TYPE_NONE, null);
            try {
                activity.getWindowManager().removeViewImmediate(mTopImage);
            } catch (Exception ignored) {
            }
        }
    }

    private static void prepare(Activity currActivity, int splitYTop, int splitYBottom) {

        mBitmap = screenShot(currActivity, false);

        if (splitYTop < 0) {
            splitYTop = 0;
        }
        if (splitYBottom > mBitmap.getHeight()) {
            splitYBottom = mBitmap.getHeight();
        }

        if (splitYTop > splitYBottom) {
            throw new IllegalArgumentException("params is invalid");
        }


        // Set the location to put the 2 bitmaps on the destination activity
        mLoc1 = new int[]{0, splitYTop};
        mLoc2 = new int[]{splitYBottom, mBitmap.getHeight()};
    }

    private static ImageView createImageView(Activity destActivity, Bitmap bmp, int loc[]) {
        MyImageView imageView = new MyImageView(destActivity);
        imageView.setImageBitmap(bmp);
        imageView.setImageOffsets(bmp.getWidth(), loc[0], loc[1]);

        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.TOP;
        windowParams.x = 0;
        windowParams.y = loc[0];
        windowParams.height = loc[1] - loc[0];
        windowParams.width = bmp.getWidth();
        windowParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | FLAG_FULLSCREEN;
        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;
        destActivity.getWindowManager().addView(imageView, windowParams);

        return imageView;
    }

    private static class MyImageView extends android.support.v7.widget.AppCompatImageView {
        private Rect mSrcRect;
        private Rect mDstRect;
        private Paint mPaint;

        public MyImageView(Context context) {
            super(context);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }

        public void setImageOffsets(int width, int startY, int endY) {
            mSrcRect = new Rect(0, startY, width, endY);
            mDstRect = new Rect(0, 0, width, endY - startY);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Bitmap bm = null;
            Drawable drawable = getDrawable();
            if (null != drawable && drawable instanceof BitmapDrawable) {
                bm = ((BitmapDrawable) drawable).getBitmap();
            }

            if (null == bm) {
                super.onDraw(canvas);
            } else {
                canvas.drawBitmap(bm, mSrcRect, mDstRect, mPaint);
            }
        }
    }

    public static Bitmap screenShot(@NonNull final Activity activity, boolean isDeleteStatusBar) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);
        decorView.buildDrawingCache();
        Bitmap bmp = decorView.getDrawingCache();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Bitmap ret;
        if (isDeleteStatusBar) {
            Resources resources = activity.getResources();
            int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
            int statusBarHeight = resources.getDimensionPixelSize(resourceId);
            ret = Bitmap.createBitmap(bmp, 0, statusBarHeight, dm.widthPixels, dm.heightPixels - statusBarHeight);
        } else {
            ret = Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels);
        }
        decorView.destroyDrawingCache();
        return ret;
    }
}