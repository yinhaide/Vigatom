package com.tencent.vigatom.utils;

import android.util.Log;

import com.tencent.vigatom.Vigatom;

/**
 * 统一封装的Log工具类
 */
public class ViLog {

    private static String TAG = Vigatom.class.getSimpleName();
    private static Type type = Type.ALL;

    /**
     * 开放指定类型的Log
     *
     * @param type Log类型
     */
    public static void openLogType(Type type) {
        ViLog.type = type;
    }

    /**
     * 关闭所有的Log
     */
    public static void stopALLLog() {
        ViLog.type = Type.STOP;
    }

    /**
     * 开放所有的Log
     */
    public static void openAllLog() {
        ViLog.type = Type.ALL;
    }

    /**
     * Log.v 长条提示
     *
     * @param msg msg
     */
    public static void v(String msg) {
        if (type == Type.ALL || type == Type.VERBOSE) {
            Log.v(TAG, getMsgWithThread(msg));
        }
    }

    /**
     * Log.v 长条提示
     *
     * @param tag tag
     * @param msg msg
     */
    public static void v(String tag, String msg) {
        if (type == Type.ALL || type == Type.VERBOSE) {
            Log.v(tag, getMsgWithThread(msg));
        }
    }

    /**
     * Log.d 调试提示
     *
     * @param msg msg
     */
    public static void d(String msg) {
        if (type == Type.ALL || type == Type.DEBUG) {
            Log.d(TAG, getMsgWithThread(msg));
        }
    }

    /**
     * Log.d 调试提示
     *
     * @param tag tag
     * @param msg msg
     */
    public static void d(String tag, String msg) {
        if (type == Type.ALL || type == Type.DEBUG) {
            Log.d(TAG, getMsgWithThread(msg));
        }
    }

    /**
     * Log.i 信息提示
     *
     * @param msg msg
     */
    public static void i(String msg) {
        if (type == Type.ALL || type == Type.INFO) {
            Log.i(TAG, getMsgWithThread(msg));
        }
    }

    /**
     * Log.i 信息提示
     *
     * @param tag tag
     * @param msg msg
     */
    public static void i(String tag, String msg) {
        if (type == Type.ALL || type == Type.INFO) {
            Log.i(tag, getMsgWithThread(msg));
        }
    }

    /**
     * Log.w 警告提示
     *
     * @param msg msg
     */
    public static void w(String msg) {
        if (type == Type.ALL || type == Type.WARN) {
            Log.w(TAG, getMsgWithThread(msg));
        }
    }

    /**
     * Log.w 警告提示
     *
     * @param tag tag
     * @param msg msg
     */
    public static void w(String tag, String msg) {
        if (type == Type.ALL || type == Type.WARN) {
            Log.w(tag, getMsgWithThread(msg));
        }
    }

    /**
     * Log.e 错误提示
     *
     * @param msg msg
     */
    public static void e(String msg) {
        if (type == Type.ALL || type == Type.ERROR) {
            Log.e(TAG, getMsgWithThread(msg));
        }
    }

    /**
     * Log.e 错误提示
     *
     * @param tag tag
     * @param msg msg
     */
    public static void e(String tag, String msg) {
        if (type == Type.ALL || type == Type.ERROR) {
            Log.e(tag, getMsgWithThread(msg));
        }
    }

    private static String getMsgWithThread(String msg) {
        return msg + " (" + Thread.currentThread().getName() + ".thread)";
    }

    /**
     * 开放Log类型枚举
     */
    public enum Type {
        VERBOSE, DEBUG, INFO, WARN, ERROR, ALL, STOP
    }
}