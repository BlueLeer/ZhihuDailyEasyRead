package com.leer.ZhihuDailyEasyRead.utils;

import android.widget.Toast;

/**
 * Created by Leer on 2017/7/1.
 */

public class ToastUtil {
    public static Toast mToast;

    public static void show(String s) {
        if (mToast == null) {
            mToast = Toast.makeText(UIUtils.getContext(), s, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(s);
        }

        mToast.show();

    }
}
