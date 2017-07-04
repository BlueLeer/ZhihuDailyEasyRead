package com.leer.ZhihuDailyEasyRead.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.leer.ZhihuDailyEasyRead.R;
import com.leer.ZhihuDailyEasyRead.utils.ResUtils;

public class BaseActivity extends AppCompatActivity {
    //true:日间模式
    //false:夜间模式
    protected boolean isDay = true;
    protected int mDayBackGroundTitleColor;
    protected int mDayTextColor;
    protected int mNightBackgroundColor;
    protected int mNightTextColor;
    protected int mDayBackgroundColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initColor();
    }

    private void initColor() {
        mDayBackGroundTitleColor = ResUtils.getColorRes(R.color.dayBackgroundTitle);
        mDayTextColor = ResUtils.getColorRes(R.color.dayTextcolor);
        mNightBackgroundColor = ResUtils.getColorRes(R.color.nightBackGround);
        mNightTextColor = ResUtils.getColorRes(R.color.nightTextColor);
        mDayBackgroundColor = ResUtils.getColorRes(R.color.dayBackGround);
    }


    //子类选择性的继承此方法,当有新的显示元素加入的时候,应该给view元素设置两种主题:
    //日间模式 夜间模式
    protected void updateTheme() {
        updateStatusColor();
    }


    @TargetApi(21)
    protected void updateStatusColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            //设置沉浸式状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(isDay ? mDayBackGroundTitleColor : mNightBackgroundColor);
        }

    }

    public boolean isDay() {
        return isDay;
    }

    public void setDay(boolean day) {
        isDay = day;
    }

    public int getDayBackGroundTitleColor() {
        return mDayBackGroundTitleColor;
    }

    public int getDayTextColor() {
        return mDayTextColor;
    }

    public int getNightBackgroundColor() {
        return mNightBackgroundColor;
    }

    public int getNightTextColor() {
        return mNightTextColor;
    }

    public int getDayBackgroundColor() {
        return mDayBackgroundColor;
    }
}
