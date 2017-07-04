package com.leer.ZhihuDailyEasyRead.fragments;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.leer.ZhihuDailyEasyRead.db.MySqliteHelper;
import com.leer.ZhihuDailyEasyRead.db.ZhihuDao;
import com.leer.ZhihuDailyEasyRead.http.HttpConstant;
import com.leer.ZhihuDailyEasyRead.http.Story;
import com.leer.ZhihuDailyEasyRead.http.ZhihuBefore;
import com.leer.ZhihuDailyEasyRead.utils.HttpUtils;
import com.leer.ZhihuDailyEasyRead.http.MyRetrofit;
import com.leer.ZhihuDailyEasyRead.http.ZhihuLatest;
import com.leer.ZhihuDailyEasyRead.utils.ResUtils;
import com.leer.ZhihuDailyEasyRead.utils.ToastUtil;
import com.leer.ZhihuDailyEasyRead.utils.UIUtils;
import com.leer.ZhihuDailyEasyRead.widgets.Kanner;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Leer on 2017/6/30.
 */

public class LatestFragment extends BaseFragment {

    private RecyclerView mRecycler_view;
    private static final int MSG_SET_DATA = 1;
    private static final int MSG_LOAD_BEFORE = 2;
    //最新新闻列表
    private ArrayList<Story> mStories = new ArrayList<>();

    //最新新闻的头部新闻
    private ArrayList<ZhihuLatest.TopStory> mTopStories = new ArrayList<>();

    private MyAdapter mRecyclerViewAdapter;

//    private MyViewPagerAdapter mMyViewPagerAdapter;

    private boolean isLoadingData = false;

    private ZhihuDao mZhihuDao;

    private Kanner mKanner;


    //利用这个日期可以查询前一天的新闻,例如20170701可以查询到到20170630的新闻
    private String mDate;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SET_DATA:
                    if (mRecyclerViewAdapter == null) {
                        mRecyclerViewAdapter = new MyAdapter();
                    }
                    mRecycler_view.setAdapter(mRecyclerViewAdapter);

                    if (mKanner != null) {
                        mKanner.setData(mTopStories);
                    }

                    mActivity.onRefreshCompleted();
                    isLoadingData = false;

                    break;
                case MSG_LOAD_BEFORE:
                    isLoadingData = false;
                    mRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    };


    @Override
    public View initView() {
        View view = UIUtils.inflateRes(R.layout.fragment_latest);
        mRecycler_view = (RecyclerView) view.findViewById(R.id.recycler_view);
//        mView_pager = (ViewPager) view.findViewById(R.id.view_pager);

        mKanner = new Kanner(getContext());
        mKanner.setOnItemClickListener(new Kanner.OnItemClickListener() {
            @Override
            public void click(View v, ZhihuLatest.TopStory topStory) {
                ToastUtil.show("当前点击的条目:" + topStory.id);
                jumpToArticleActivity(topStory.id + "");
            }
        });


        mRecycler_view.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        mRecycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) mRecycler_view.getLayoutManager();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                //滑动到底部自动加载更多
                if (lastVisibleItemPosition == mRecycler_view.getAdapter().getItemCount() - 1) {
                    if (!isLoadingData) {
                        loadMore();
                    }
                }
            }
        });

        mActivity.setOnRefreshListener(new MainActivity.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //加载最新的消息
                initData();
            }
        });


        return view;
    }


    @Override
    public void initData() {
        if (mZhihuDao == null) {
            mZhihuDao = ZhihuDao.getInstance();
        }

        if (isLoadingData) return;

        if (HttpUtils.isNetworkAvailable()) {
            //如果网络可用,先获取最新的内容
            //加载最新的数据
            initLatest();
        } else {
            //网路不可用,就从本地加载数据
            initLatestFromDB();
            ToastUtil.show("网络不可用~");
        }

    }

    private void initLatestFromDB() {
        String json = mZhihuDao.query(MySqliteHelper.LATEST_COLUMN);
        if (json != null) {
            parseLatestJson(json);
        } else {
            ToastUtil.show("本地没有数据~");
        }
    }

    private void initLatest() {
        isLoadingData = true;
        Retrofit.Builder builder = new Retrofit.Builder();
        //添加baseUrl
        builder.baseUrl(HttpConstant.BASE_URL);
        //添加Gson解析库
//        builder.addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        MyRetrofit myRetrofit = retrofit.create(MyRetrofit.class);
        Call<ResponseBody> zhihuLatest = myRetrofit.getZhihuLatestJson();

        zhihuLatest.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String result;
                try {
                    result = response.body().string();
                    if (result != null) {
                        parseLatestJson(result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void parseLatestJson(String json) {
        mStories.clear();
        Gson gson = new Gson();
        ZhihuLatest zhihuLatest = gson.fromJson(json, ZhihuLatest.class);
        mStories = zhihuLatest.stories;
        mTopStories = zhihuLatest.top_stories;

        for (Story story : mStories) {
            story.date = zhihuLatest.date;
        }
        mDate = zhihuLatest.date;

        mHandler.sendEmptyMessage(MSG_SET_DATA);

        mZhihuDao.replace(MySqliteHelper.LATEST_COLUMN, json);
    }

    private void loadMore() {
        //加载以前的日报内容
        ToastUtil.show("正在加载更多!");

        isLoadingData = true;

        //先从数据库中获取,如果没有就从网络获取
        String storaged_result = mZhihuDao.query(Integer.parseInt(mDate));

        if (!TextUtils.isEmpty(storaged_result)) {
            parseBeforeJson(storaged_result);

        } else {
            Retrofit.Builder builder = new Retrofit.Builder();
            //添加baseUrl
            builder.baseUrl(HttpConstant.BASE_URL);
            //添加Gson解析库
//        builder.addConverterFactory(GsonConverterFactory.create());

            Retrofit retrofit = builder.build();
            MyRetrofit myRetrofit = retrofit.create(MyRetrofit.class);
            Call<ResponseBody> zhihuBeforeJson = myRetrofit.getZhihuBeforeJson(mDate);
            zhihuBeforeJson.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String result = response.body().string();
                        //将获取的数据存入数据库
                        mZhihuDao.replace(Integer.parseInt(mDate), result);

                        parseBeforeJson(result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }

    }

    private void parseBeforeJson(String json) {
        Gson gson = new Gson();
        ZhihuBefore zhihuBefore = gson.fromJson(json, ZhihuBefore.class);

        ArrayList<Story> beforeStories = zhihuBefore.stories;
        for (Story beforeStory : beforeStories) {
            beforeStory.date = zhihuBefore.date;
        }

        mDate = zhihuBefore.date;
        mStories.addAll(beforeStories);

        mHandler.sendEmptyMessage(MSG_LOAD_BEFORE);
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        public static final int BODY = 0;
        public static final int TOP = 1;

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(getContext()).inflate(R.layout.recycler_view_item_latest, parent, false);
            MyViewHolder viewHolder;
            if (viewType == BODY) {
                viewHolder = new MyViewHolder(view, BODY);
            } else {

                viewHolder = new MyViewHolder(mKanner, TOP);
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            if (position == 0) {
                holder.bindView(null);
            } else {
                Story story = mStories.get(position - 1);
                holder.bindView(story);
            }
        }

        @Override
        public int getItemCount() {
            return mStories.size() + 1;//story列表和一个头部轮播条
        }

        @Override
        public int getItemViewType(int position) {
            int viewType = BODY;
            if (position == 0) {
                viewType = TOP;
            }
            return viewType;
        }

    }

    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTv_title;
        private ImageView mIv_icon;
        private View itemView;
        private LinearLayout mLl_item;
        private TextView mTv_date;
        private int viewType;
        private Kanner kanner;
        private Story mStory;

        public MyViewHolder(View itemView, int viewType) {
            super(itemView);

            itemView.setOnClickListener(this);
            this.viewType = viewType;
            if (viewType == MyAdapter.BODY) {
                this.itemView = itemView;
                mLl_item = (LinearLayout) itemView.findViewById(R.id.ll_item);

                mTv_title = (TextView) itemView.findViewById(R.id.tv_title);
                mIv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);

                mTv_date = (TextView) itemView.findViewById(R.id.tv_date);
            } else if (viewType == MyAdapter.TOP) {
                kanner = (Kanner) itemView;
            }
        }

        //notifyDataSetChanged()以后,针对每一个item,一定会执行此方法,进行更新每一个item的内容
        public void bindView(Story story) {
            mStory = story;

            if (viewType == MyAdapter.BODY) {
                mLl_item.setBackgroundResource(isDay ? R.drawable.selector_rv_item_day : R.drawable.selector_rv_item_night);
                mTv_title.setTextColor(isDay ? mActivity.getDayTextColor() : mActivity.getNightTextColor());
                itemView.setBackgroundColor(isDay ? mActivity.getDayBackgroundColor() : mActivity.getNightBackgroundColor());
                mTv_date.setTextColor(ResUtils.getColorRes(isDay ? R.color.colorGray : R.color.dayBackGround));

                if (story.date != null) {
                    mTv_date.setText(parseDate(story.date));
                }
                mTv_title.setText(story.title);
                Picasso.with(mActivity).load(story.images.get(0)).into(mIv_icon);
            } else if (viewType == MyAdapter.TOP) {
                //已经在mHandler的handleMessage()回调方法中设置好了,避免item销毁又会重建
                //从而导致反复调用
            }
        }


        @Override
        public void onClick(View v) {
//            ToastUtil.show("当前点击的条目:" + mStory.id);
            jumpToArticleActivity(mStory.id + "");
        }
    }


    public void updateTheme() {
        //重新获取是否夜间模式
        this.isDay = mActivity.isDay();

        if (mRecyclerViewAdapter != null) {
            mRecyclerViewAdapter.notifyDataSetChanged();
        }

    }

    public String parseDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat newSdf = new SimpleDateFormat("M月d日");
        Date currentDate = new Date();
        String changedDate = null;

        if (sdf.format(currentDate).equals(date)) {
            changedDate = "今日热闻";
        } else {
            try {
                Date parse = sdf.parse(date);
                changedDate = newSdf.format(parse);
            } catch (ParseException e) {

            }

        }

        return changedDate;
    }

    private void jumpToArticleActivity(String article_id) {
        Intent i = ArticleActivity.newIntent(mActivity, article_id, isDay);
        startActivity(i);

        //设置activiti进入退出时的动画
        mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
