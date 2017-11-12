package com.ethanhua.hencoderpractice.zhihuadverts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ethanhua.hencoderpractice.R;

public class SecondaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplitAnimationUtil.prepareAnimation(this);
        setContentView(R.layout.activity_secondary);
        SplitAnimationUtil.animate(this, 1000);
    }

    @Override
    public void onBackPressed() {
        SplitAnimationUtil.prepareBackAnimation(this);
        finish();
        SplitAnimationUtil.backAnimate(1000);
        overridePendingTransition(0, 0);
    }

}
