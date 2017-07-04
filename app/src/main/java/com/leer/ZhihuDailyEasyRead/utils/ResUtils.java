package com.leer.ZhihuDailyEasyRead.utils;

import com.leer.ZhihuDailyEasyRead.global.MyApplication;

/**
 * Created by Leer on 2017/6/30.
 */

public class ResUtils {
    public static int getColorRes(int resId) {
        int color = MyApplication.getContext().getResources().getColor(resId);
        return color;
    }
}
