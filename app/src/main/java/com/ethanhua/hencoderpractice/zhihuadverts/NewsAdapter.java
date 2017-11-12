package com.ethanhua.hencoderpractice.zhihuadverts;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ethanhua.hencoderpractice.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ethanhua on 2017/11/12.
 */

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_NEWS = 0;
    private static final int TYPE_ADVERTS = 1;
    private List<NewsModel> newsModelList;

    public NewsAdapter(List<NewsModel> newsModelList) {
        this.newsModelList = newsModelList == null ? new ArrayList<NewsModel>() : newsModelList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_NEWS == viewType) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_news, parent, false)) {
            };
        } else {
            return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_adverts, parent, false)) {
            };
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ADVERTS) {
            final ImageView imageView = holder.itemView.findViewById(R.id.img_adverts);
            imageView.setImageResource(R.drawable.timg);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SplitAnimationUtil.startActivity(
                            (Activity) holder.itemView.getContext(),
                            new Intent(holder.itemView.getContext(), SecondaryActivity.class)
                            , holder.itemView.getTop(), holder.itemView.getBottom());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return newsModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (newsModelList.get(position).type == NewsModel.TYPE_NEWS) {
            return TYPE_NEWS;
        } else {
            return TYPE_ADVERTS;
        }
    }
}
