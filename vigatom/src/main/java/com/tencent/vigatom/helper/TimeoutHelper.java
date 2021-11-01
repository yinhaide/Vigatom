package com.tencent.vigatom.helper;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * 超时机制管理类，用于超时、倒计时、执行一些不耗时的倒计时任务
 */
public class TimeoutHelper {

    private static final String TAG = TimeoutHelper.class.getSimpleName();
    private final Handler handler;

    private TimeoutHelper() {
        HandlerThread thread = new HandlerThread("timeout");
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    public static TimeoutHelper getInstance() {
        return Inner.INSTANCE;
    }

    /**
     * 加入倒计时任务队列：切记任务不能做耗时操作
     *
     * @param runnable 任务：切记任务不能做耗时操作
     * @param delay 倒计时时长，单位毫秒
     */
    public void putTask(Runnable runnable, long delay) {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, delay);
    }

    /**
     * 从任务列表剔除任务
     *
     * @param runnable 要剔除的任务
     */
    public void removeTask(Runnable runnable) {
        handler.removeCallbacks(runnable);
    }

    private static class Inner {
        private static final TimeoutHelper INSTANCE = new TimeoutHelper();
    }
}
