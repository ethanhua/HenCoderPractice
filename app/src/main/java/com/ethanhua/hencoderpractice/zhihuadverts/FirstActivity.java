package com.ethanhua.hencoderpractice.zhihuadverts;

import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.ethanhua.hencoderpractice.R;

import java.util.ArrayList;
import java.util.List;

public class FirstActivity extends AppCompatActivity {
    private Matrix imgMatrix = new Matrix();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        List<NewsModel> newsModelList = new ArrayList<>();
        NewsAdapter newsAdapter = new NewsAdapter(newsModelList);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(newsAdapter);
        for (int i = 0; i < 14; i++) {
            NewsModel newsModel = new NewsModel();
            newsModel.type = NewsModel.TYPE_NEWS;
            if (i == 3) {
                newsModel.type = NewsModel.TYPE_ADVERTS;

            }
            newsModelList.add(newsModel);
        }
        newsAdapter.notifyDataSetChanged();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx1, int dy1) {
                super.onScrolled(recyclerView, dx1, dy1);
                ImageView imageView = recyclerView.findViewById(R.id.img_adverts);
                if (imageView != null) {
                    float scale;
                    float dx = 0, dy = 0;
                    float dwidth = imageView.getDrawable().getIntrinsicWidth();
                    float dheight = imageView.getDrawable().getIntrinsicHeight();

                    float vheight = getResources().getDisplayMetrics().heightPixels;
                    float vwidth = imageView.getWidth();
                    if (dwidth * vheight > vwidth * dheight) {
                        scale = (float) vheight / (float) dheight;
                    } else {
                        scale = (float) vwidth / (float) dwidth;
                    }
                    dx = Math.round((vwidth - dwidth * scale) * 0.5f);
                    imgMatrix.setScale(scale, scale);
                    View parentView = (View) imageView.getParent();
                    dy = parentView.getTop();
                    if (dy < 0) {
                        dy = 0;
                    }
                    if (dy > vheight - imageView.getHeight()) {
                        dy = vheight - imageView.getHeight();
                    }
                    imgMatrix.postTranslate(dx, -dy);
                    imageView.setImageMatrix(imgMatrix);
                }
            }
        });
    }
}
