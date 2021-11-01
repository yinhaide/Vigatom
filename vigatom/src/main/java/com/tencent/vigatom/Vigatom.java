package com.tencent.vigatom;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.tencent.vigatom.helper.CrashHelper;
import com.tencent.vigatom.helper.FluentHelper;
import com.tencent.vigatom.helper.FluentHelper.OnFluentListener;
import com.tencent.vigatom.helper.LeakHelper;
import com.tencent.vigatom.helper.LeakHelper.OnLeaksListener;
import com.tencent.vigatom.ue.layout.PercentLayoutHelper;

/**
 * 本类作用是暴露内部接口供外部使用
 */
public class Vigatom {

    private static final String TAG = Vigatom.class.getSimpleName();
    //全局的Application
    private static Application application;
    //主线程的消息队列，用于执行异步操作之后更新UI
    private final Handler handler;
    //单例
    private static volatile Vigatom instance;

    public static Vigatom getInstance() {
        if (instance == null) {
            synchronized (Vigatom.class) {
                if (instance == null) {
                    instance = new Vigatom();
                }
            }
        }
        return instance;
    }

    public Vigatom() {
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * applicatipn的初始化，建议在onCreate中调用
     *
     * @param app 进程
     */
    public Vigatom init(Application app) {
        //存储当前的Application
        application = app;
        return instance;
    }

    /**
     * 是否允许内存泄漏的检测，耗时很小，只会在UI线程空闲的时候才会检测
     *
     * @param enable 是否开启
     * @param delayTime 对象销毁后多久开始检测内存
     * @param leaksListener 回调监听
     * @return 单例
     */
    public Vigatom enableLeak(boolean enable, int delayTime, OnLeaksListener leaksListener) {
        //内存泄漏相关
        LeakHelper.getInstance().setDelayTime(delayTime).setOnLeaksListener(leaksListener).setEnable(enable);
        return instance;
    }

    /**
     * 是否允许卡顿的检测，不耗时，无侵入
     *
     * @param enable 是否开启
     * @param monitorTime UI帧率间隔多久算是卡顿
     * @param fluentListener 回调监听
     * @return 单例
     */
    public Vigatom enableFluent(boolean enable, int monitorTime, OnFluentListener fluentListener) {
        //卡顿检测
        FluentHelper.getInstance().setMonitorTime(monitorTime).setEnable(enable).setOnFluentListener(fluentListener);
        return instance;
    }

    /**
     * 是否允许由框架内部自动处理未捕获的异常
     *
     * @param enable 是否允许，true的话内部自动捕获异常，自动处理，false的话由开发者处理
     * @param thread 当前发生异常的线程，如果enable是true的话传null
     * @param throwable 发生的异常，如果enable是true的话传null
     * @return 单例
     */
    public Vigatom enableUncaughtException(boolean enable, Thread thread, Throwable throwable) {
        if (enable) {
            Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
                CrashHelper.getInstance().uncaughtException(application, t, e);
            });//捕获所有线程抛出的异常
        } else {
            CrashHelper.getInstance().uncaughtException(application, thread, throwable);
        }
        return instance;
    }

    /**
     * 获取全局的上下文
     *
     * @return 上下文
     */
    public static Context getAppContext(){
        return application;
    }

    /**
     * 主线程运行，用户异步结果之后更新UI
     *
     * @param runnable 接口
     */
    public void runUIThread(Runnable runnable) {
        handler.post(runnable);
    }

    /**
     * 百分比的布局方向
     *
     * @param isHorizontal 预览方向是否是横向的，否则是竖向
     */
    public Vigatom setPreviewDirection(boolean isHorizontal) {
        PercentLayoutHelper.isHorizontal = isHorizontal;
        return instance;
    }
}
