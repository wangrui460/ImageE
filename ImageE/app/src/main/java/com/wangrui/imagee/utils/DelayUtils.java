package com.wangrui.imagee.utils;

import android.app.Activity;

import androidx.annotation.NonNull;

import java.util.Timer;
import java.util.TimerTask;

/**
 * created by WR
 * 时间：2019-09-05 10:34
 */
public class DelayUtils {

    public static void doSomethingInDelay(int delay, @NonNull OnDelayListener listener) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                listener.onDelay();
            }
        }, delay);
    }

    public static void doSomethingInDelayOnUiThread(@NonNull Activity activity, int delay, @NonNull OnDelayListener listener) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onDelay();
                    }
                });
            }
        }, delay);
    }

    public interface OnDelayListener {
        public void onDelay();
    }
}
