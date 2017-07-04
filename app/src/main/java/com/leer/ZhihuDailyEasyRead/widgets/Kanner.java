package com.leer.ZhihuDailyEasyRead.widgets;

import android.content.Context;
import android.os.Handler;
import android.os.Process;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leer.ZhihuDailyEasyRead.R;
import com.leer.ZhihuDailyEasyRead.http.ZhihuLatest;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * 今日热闻的头部轮播条
 */
public class Kanner extends FrameLayout implements OnClickListener {
    private List<ZhihuLatest.TopStory> mTopStories;
    private List<View> mPagers;
    private Context context;
    private ViewPager vp;
    private boolean isAutoPlay = true;
    //记录播放的位置
    private int mCurrentItem;

    //记录播放到views的第几个条目了
    private int mCurrentIndex;
    private LinearLayout ll_dots;
    private List<ImageView> iv_dots;
    private static Handler mHandler = new Handler();
    private OnItemClickListener mItemClickListener;
    private int mHeight;
    private MyPagerAdapter mAdapter;

    public Kanner(Context context) {
        this(context, null);
    }

    public Kanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Kanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.mTopStories = new ArrayList<>();
        initView();
    }

    private void initView() {
        //viewpager要显示的页面
        mPagers = new ArrayList<>();
        //页面下方的指示器(小点)
        iv_dots = new ArrayList<>();
    }


    public void setData(List<ZhihuLatest.TopStory> topEntities) {
        this.mTopStories = topEntities;
        reset();
    }

    private void reset() {
        //viewPager的初始位置
        //保证这个数能够被视图需要显示个数整除
        mCurrentItem = Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % mTopStories.size();
        mPagers.clear();
        initUI();
    }

    private void initUI() {
        View view = LayoutInflater.from(context).inflate(
                R.layout.kanner_layout, this, true);
        vp = (ViewPager) view.findViewById(R.id.vp);
        ll_dots = (LinearLayout) view.findViewById(R.id.ll_dots);

        iv_dots.clear();
        ll_dots.removeAllViews();

        int len = mTopStories.size();
        for (int i = 0; i < len; i++) {
            ImageView iv_dot = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 5;
            params.rightMargin = 5;

            //设置显示的页面的点高亮
            if (i == mCurrentItem % len) {
                iv_dot.setImageResource(R.drawable.dot_focus);
            } else {
                iv_dot.setImageResource(R.drawable.dot_blur);
            }
            iv_dots.add(iv_dot);

            ll_dots.addView(iv_dot, params);


        }

        mPagers.clear();
        for (int i = 0; i < len; i++) {
            ZhihuLatest.TopStory topStory = mTopStories.get(i);
            View fm = LayoutInflater.from(context).inflate(
                    R.layout.kanner_content_layout, null);
            ImageView iv = (ImageView) fm.findViewById(R.id.iv_title);
            TextView tv_title = (TextView) fm.findViewById(R.id.tv_title);
            iv.setScaleType(ScaleType.CENTER_CROP);
//            iv.setBackgroundResource(R.drawable.loading1);

            Picasso.with(getContext()).load(topStory.image).into(iv);
            tv_title.setText(topStory.title);
            fm.setOnClickListener(this);

            mPagers.add(fm);
        }

        //设置数据适配器
        if(mAdapter == null) {
            mAdapter = new MyPagerAdapter();
        }
        vp.setAdapter(new MyPagerAdapter());
        //设置能够获取到焦点
        vp.setFocusable(true);

        vp.setCurrentItem(mCurrentItem);

        vp.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int currentIndex = position % iv_dots.size();
                for (int i = 0; i < iv_dots.size(); i++) {
                    if (i == currentIndex) {
                        iv_dots.get(i).setImageResource(R.drawable.dot_focus);

                    } else {
                        iv_dots.get(i).setImageResource(R.drawable.dot_blur);
                    }
                }

                mCurrentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        startPlay();
    }


    public void startPlay() {
        isAutoPlay = true;
        //开启自动轮播之前,先清除掉所有的自动轮播任务
        mHandler.removeCallbacks(task);

        mHandler.postDelayed(task, 3000);
    }


    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (isAutoPlay) {
                mCurrentItem++;
                vp.setCurrentItem(mCurrentItem);
                mHandler.postDelayed(task, 3000);
                int i = Process.myTid();
            }
        }

    };

    class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int currentIndex = position % mTopStories.size();
            View itemView = mPagers.get(currentIndex);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }


    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void click(View v, ZhihuLatest.TopStory topStory);
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener != null) {
            ZhihuLatest.TopStory entity = mTopStories.get(vp.getCurrentItem() % mTopStories.size());
            mItemClickListener.click(v, entity);
        }
    }

//    @Override
//    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//        super.onScrollChanged(l, t, oldl, oldt);
//        if (t <= -mHeight) {
//            startPlay();
//        }
//    }
}
