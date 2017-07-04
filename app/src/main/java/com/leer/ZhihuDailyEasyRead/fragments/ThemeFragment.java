package com.leer.ZhihuDailyEasyRead.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.leer.ZhihuDailyEasyRead.R;
import com.leer.ZhihuDailyEasyRead.activities.ArticleActivity;
import com.leer.ZhihuDailyEasyRead.activities.MainActivity;
import com.leer.ZhihuDailyEasyRead.http.HttpConstant;
import com.leer.ZhihuDailyEasyRead.http.MyRetrofit;
import com.leer.ZhihuDailyEasyRead.http.Story;
import com.leer.ZhihuDailyEasyRead.http.ThemeStory;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Leer on 2017/7/2.
 */

public class ThemeFragment extends BaseFragment {

    private static final int MSG_SET_DATA = 100;
    private LayoutInflater mLayoutInflater;
    private RecyclerView mRecyclerView;
    private static final String ARG_THEME = "theme";
    private MyAdapter mAdapter;


    private String currentThemeId;
    private ArrayList<Story> mStories = new ArrayList<>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SET_DATA:
                    if (mAdapter == null) {
                        mAdapter = new MyAdapter();
                        mRecyclerView.setAdapter(mAdapter);
                    } else {
                        mAdapter.notifyDataSetChanged();
                        mActivity.onRefreshCompleted();
                    }

                    break;
            }
        }
    };

    @Override
    public View initView() {
        currentThemeId = getArguments().getString(ARG_THEME);

        mLayoutInflater = LayoutInflater.from(getContext());
        View view = mLayoutInflater.inflate(R.layout.fragment_theme, null, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_theme);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

//        mActivity.setOnRefreshListener(new MainActivity.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                initData();
//            }
//        });

        return view;
    }

    @Override
    public void initData() {
        setData(currentThemeId);
    }

    private void setData(String themeId) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpConstant.BASE_URL).build();
        MyRetrofit myRetrofit = retrofit.create(MyRetrofit.class);
        Call<ResponseBody> zhihuThemeJson = myRetrofit.getZhihuThemeJson(themeId);
        zhihuThemeJson.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    parseJson(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void parseJson(String json) {
        Gson gson = new Gson();
        mStories = gson.fromJson(json, ThemeStory.class).stories;

        mHandler.sendEmptyMessage(MSG_SET_DATA);

    }

    public static ThemeFragment getInstance(String themeId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_THEME, themeId);

        ThemeFragment themeFragment = new ThemeFragment();
        themeFragment.setArguments(bundle);
        return themeFragment;
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.recycler_view_item_theme, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Story story = mStories.get(position);
            holder.bindView(story);
        }

        @Override
        public int getItemCount() {
            return mStories.size();
        }

    }

    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mIv_icon_theme;
        private final TextView mTv_title_theme;
        private final LinearLayout mLl_item;
        Story mStory;

        public MyViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            mIv_icon_theme = (ImageView) itemView.findViewById(R.id.iv_icon_theme);
            mTv_title_theme = (TextView) itemView.findViewById(R.id.tv_title_theme);
            mLl_item = (LinearLayout) itemView.findViewById(R.id.ll_item);
        }

        public void bindView(Story story) {
            if (story != null) {
                this.mStory = story;
                if (story.images != null && story.images.size() != 0 && story.images.get(0) != null) {
                    Picasso.with(getContext()).load(story.images.get(0)).into(mIv_icon_theme);
                } else {
                    mIv_icon_theme.setImageResource(R.drawable.ic_zhihu);
                }
                mTv_title_theme.setText(story.title);
            }

            mTv_title_theme.setTextColor(isDay ? mActivity.getDayTextColor() : mActivity.getNightTextColor());
            itemView.setBackgroundColor(isDay ? mActivity.getDayBackgroundColor() : mActivity.getNightBackgroundColor());
            mLl_item.setBackgroundResource(isDay ? R.drawable.selector_rv_item_day : R.drawable.selector_rv_item_night);
        }

        @Override
        public void onClick(View v) {
            String article_id = mStory.id + "";
            Intent intent = ArticleActivity.newIntent(mActivity, article_id, isDay);
            startActivity(intent);
            mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    public void updateTheme() {
        this.isDay = mActivity.isDay();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

}
