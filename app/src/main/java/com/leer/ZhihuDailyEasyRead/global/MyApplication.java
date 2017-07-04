package com.leer.ZhihuDailyEasyRead.global;

import android.app.Application;
import android.content.Context;
import android.os.Handler;


/**
 * Created by Leer on 2017/6/30.
 */

public class MyApplication extends Application {
    private static Context sContext;
    private static Handler sHandler;
    private static int sMainThreadId;

    @Override
    public void onCreate() {
        sContext = getApplicationContext();
        sHandler = new Handler();
        sMainThreadId = android.os.Process.myTid();
        super.onCreate();
    }

    public static Context getContext() {
        return sContext;
    }

    public static int getMainThreadId() {
        return sMainThreadId;
    }

    public static Handler getHandler() {
        return sHandler;
    }
}
