package com.leer.ZhihuDailyEasyRead.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;

import com.leer.ZhihuDailyEasyRead.global.MyApplication;

/**
 * Created by Leer on 2017/6/30.
 */

public class UIUtils {
    public static Context getContext(){
        return MyApplication.getContext();
    }
    public static Handler getHandler(){
        return MyApplication.getHandler();
    }
    public static int getMainThreadID(){
        return MyApplication.getMainThreadId();
    }

/*---------------------------------获取资源---------------------------*/
    /**
     * @param id 字符串资源的ID
     * @return 字符串对象
     */
    public static String getStringRes(int id){
        return getContext().getResources().getString(id);
    }

    /**
     * @param id 字符串数组资源的ID
     * @return 字符串数组
     */
    public static String[] getStringArrRes(int id){
        return getContext().getResources().getStringArray(id);
    }

    /**
     * @param id Drawable资源的ID
     * @return Drawable图片资源
     */
    public static Drawable getDrawableRes(int id){
        return getContext().getResources().getDrawable(id);
    }

    /**
     * @param id 颜色状态选择器资源的ID
     * @return 颜色资源选择器
     */
    public static ColorStateList getColorStateListRes(int id){
        return getContext().getResources().getColorStateList(id);
    }

    /**
     * @param id 颜色资源的ID
     * @return int值类型的资源
     */
    public static int getColorRes(int id){
        return getContext().getResources().getColor(id);
    }

    /**
     * @param id dimens文件夹下面的dimen的id
     * @return dimens文件中对应的尺寸的像素
     */
    public static int getDimensRes(int id){
        return getContext().getResources().getDimensionPixelSize(id);
    }

    /*---------------------------------dp和px相互转换---------------------------*/

    /**
     * @param dp 需要转换的dp值
     * @return dp在该设备上对应的像素值
     */
    public static int dip2px(float dp){
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp*density+0.5f);
    }

    /**
     * @param px 需要转换的px值
     * @return 返回px在该设备上对应的dp值
     */
    public static float px2dip(int px){
        float density = getContext().getResources().getDisplayMetrics().density;
        return px/density;
    }

    /**加载布局文件的工具类
     * @param id 布局文件对应的资源文件的ID
     * @return 返回id创建的View
     */
    /*---------------------------------加载布局文件---------------------------*/
    public static View inflateRes(int id){
        View view = View.inflate(getContext(),id,null);
        return view;
    }

    /** 判断是否运行在主线程,因为这个方法是在运行过程中调用的,
     * 相对应的就会返回运行时环境的线程ID
     * @return 当前运行的线程ID
     */
    /*---------------------------------判断是否运行在主线程及运行在主线程---------------------------*/
    public static boolean isRunOnMainThread(){
        int currentThreadID = android.os.Process.myTid();
        if(currentThreadID == getMainThreadID()){
            return true;
        }

        return false;
    }


    /**
     * @param r 需要在主线程中运行的runnable对象
     */
    public static void runOnMainThread(Runnable r){
        if(isRunOnMainThread()){
            r.run();
        }else{
            //将r对象post到主线程中去
            getHandler().post(r);
        }
    }
}
