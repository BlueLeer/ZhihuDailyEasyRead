package com.leer.ZhihuDailyEasyRead.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.leer.ZhihuDailyEasyRead.R;
import com.leer.ZhihuDailyEasyRead.fragments.LatestFragment;
import com.leer.ZhihuDailyEasyRead.fragments.ThemeFragment;
import com.leer.ZhihuDailyEasyRead.fragments.ThemePickFragment;
import com.leer.ZhihuDailyEasyRead.http.ZhihuThemeList;
import com.leer.ZhihuDailyEasyRead.utils.ResUtils;

import java.util.List;

/**
 * Created by Leer on 2017/6/30.
 */

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private Toolbar mToolbar;
    private FrameLayout mFl_container;
    //记录按下Back键的时间
    private long firstPre;
    private FloatingActionButton mFab;
    private FragmentManager mFm;
    private LatestFragment mLatestFragment;
    private ThemePickFragment mThemePickFragment;
    //当前默认的日报主题
    //默认是0,是今日热闻的内容
    private int mCurrentTheme = 0;
    private ThemeFragment mThemeFragment;
    private SwipeRefreshLayout mSwipe_refresh_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFm = getSupportFragmentManager();
        initView();

        //默认加载"最新的日报内容"
        initLastFragment();
    }

    private void initLastFragment() {
        mLatestFragment = new LatestFragment();
        mFm.beginTransaction().replace(R.id.fl_container, mLatestFragment).commit();

        mThemePickFragment = ThemePickFragment.getInstance(1);

        mThemePickFragment.setOnItemClickListener(new ThemePickFragment.OnItemClickListener() {
            @Override
            public void onItemClick(ZhihuThemeList.Theme theme) {
                if (theme == null) {
                    mFm.beginTransaction().replace(R.id.fl_container, mLatestFragment).commit();
                    mCurrentTheme = 0;
                    setToolBarTitle("今日热闻");
                } else {
                    int i = Integer.parseInt(theme.id);
                    if (i != mCurrentTheme) {
                        mThemeFragment = ThemeFragment.getInstance(theme.id);
                        mFm.beginTransaction().replace(R.id.fl_container, mThemeFragment).commit();
                        mCurrentTheme = Integer.parseInt(theme.id);
                        setToolBarTitle(theme.name);
                    }
                }
            }
        });
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("今日热闻");
        setSupportActionBar(mToolbar);
        mFl_container = (FrameLayout) findViewById(R.id.fl_container);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThemePickFragment.show(mFm, "test");
            }
        });

        //下拉刷新控件
        mSwipe_refresh_layout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        //设置小圈圈的颜色,转动一圈就变一个颜色
        mSwipe_refresh_layout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipe_refresh_layout.setOnRefreshListener(this);
        updateTheme();
    }


    private void showSnackBar(View view, String s) {
        Snackbar snackbar = Snackbar.make(view, s, Snackbar.LENGTH_SHORT).setAction("Action", null);
        snackbar.getView().setBackgroundColor(ResUtils.getColorRes(R.color.colorGray));
        snackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.read_mode) {
            isDay = !isDay;
            if (isDay) {
                item.setIcon(R.drawable.ic_night);
            } else {
                item.setIcon(R.drawable.ic_day);
            }
            updateTheme();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void updateTheme() {
        super.updateTheme();
        mToolbar.setBackgroundColor(isDay ? mDayBackGroundTitleColor : mNightBackgroundColor);
        mToolbar.setTitleTextColor(isDay ? mDayTextColor : mNightTextColor);

        mFl_container.setBackgroundColor(isDay ? mDayBackgroundColor : mNightBackgroundColor);

        mFab.setBackgroundTintList(getResources().getColorStateList(isDay ? R.color.selector_fab_day : R.color.selector_fab_night));

        mFab.setBackgroundResource(isDay ? R.color.selector_fab_day : R.color.selector_fab_night);
        if (mLatestFragment != null) {
            mLatestFragment.updateTheme();
        }

        if (mThemeFragment != null) {
            mThemeFragment.updateTheme();
        }
    }

    public void setToolBarTitle(String title) {
        mToolbar.setTitle(title);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstPre < 2000) {
            finish();
        } else {
            firstPre = System.currentTimeMillis();
            showSnackBar(mFl_container, "再按一次退出");
        }
    }

    @Override
    public void onRefresh() {
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
        }

    }

    //设置刷新完成
    public void onRefreshCompleted() {
        if (mSwipe_refresh_layout != null && mSwipe_refresh_layout.isRefreshing()) {
            mSwipe_refresh_layout.setRefreshing(false);
        }

    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public OnRefreshListener mOnRefreshListener;

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mOnRefreshListener = listener;
    }
}
