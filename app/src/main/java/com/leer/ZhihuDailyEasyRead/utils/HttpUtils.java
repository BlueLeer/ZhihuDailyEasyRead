package com.leer.ZhihuDailyEasyRead.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.leer.ZhihuDailyEasyRead.utils.UIUtils;

/**
 * Created by Leer on 2017/7/1.
 */

public class HttpUtils {

    public static boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) UIUtils.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            return activeNetworkInfo.isAvailable();
        }

        return false;
    }
}
