package com.wangrui.imagee;

import android.app.Application;

import com.wangrui.imagee.utils.Utils;

/**
 * created by WR
 * 时间：2020-03-26 15:42
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Utils.init(this);
    }
}
