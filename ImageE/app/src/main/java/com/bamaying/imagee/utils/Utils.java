package com.bamaying.imagee.utils;

import android.content.Context;

/**
 * created by WR
 * 时间：2019-08-05 12:01
 */
public class Utils {

    private static Context context = null;

    public static void init(Context context) {
        Utils.context = context;
        LogUtils.init(context);
        ToastUtils.init(context);
    }

    public static Context getAppContext() {
        if (context == null) {
            throw new RuntimeException("Utils未在 BmyApp 中初始化");
        }
        return context;
    }
}
