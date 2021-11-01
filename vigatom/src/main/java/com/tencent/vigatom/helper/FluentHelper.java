package com.tencent.vigatom.helper;

import android.os.Handler;
import android.os.Looper;
import android.util.Printer;
import com.tencent.vigatom.bean.FluentBean;
import com.tencent.vigatom.utils.ViLog;

/**
 * 流畅性监控，检测主线程耗时工具类，还有另外一种监控方式：Choreographer，但是不推荐
 */
public class FluentHelper {

    private static final String TAG = FluentHelper.class.getSimpleName();
    private static volatile FluentHelper singleton = null;
    //方法耗时的卡口,400毫秒，超过阈值就上报
    private long monitorTime = 400L;
    //当前的栈信息
    private String stackMsg;
    //记录获取栈的时间，避免是因为缓存了上次的栈信息
    private long stackMsgTime = System.currentTimeMillis();
    //当前开始时间
    private long nowTime = System.currentTimeMillis();
    //主线程的Handler
    private Handler mainHandler;
    //是否开启
    private boolean enable;

    /**
     * 获取单例
     * @return 单例
     */
    public static FluentHelper getInstance(){
        if(singleton == null){
            synchronized (FluentHelper.class){
                if(singleton == null){
                    singleton = new FluentHelper();
                }
            }
        }
        return singleton;
    }

    private FluentHelper() {
        //拿到主线程
        mainHandler = new Handler(Looper.getMainLooper());
        //立刻启动
        start();
    }

    /**
     * 获取监控时间阈值
     * @return 时间
     */
    public long getMonitorTime() {
        return monitorTime;
    }

    /**
     * 设置监控时间阈值
     * @param monitorTime 时间
     */
    public FluentHelper setMonitorTime(long monitorTime) {
        if(monitorTime < 20L){
            ViLog.e(TAG,"setMonitorTime时间不能太小:"+monitorTime);
            return singleton;
        }
        this.monitorTime = monitorTime;
        return singleton;
    }

    /**
     * 是否开启检测
     *
     * @return 结果
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * 设置是否要检测
     *
     * @param enable 是否检测
     */
    public FluentHelper setEnable(boolean enable) {
        this.enable = enable;
        return singleton;
    }

    /**
     * 启动监控
     */
    private void start() {
        Looper.getMainLooper().setMessageLogging(new Printer() {
            //分发和处理消息开始前的log
            private static final String START = ">>>>> Dispatching";
            //分发和处理消息结束后的log
            private static final String END = "<<<<< Finished";

            @Override
            public void println(String x) {
                if (!enable) {
                    return;
                }
                if (x.startsWith(START)) {
                    nowTime = System.currentTimeMillis();
                    //开始计时
                    removeMonitor();
                    startMonitor();
                }
                if (x.startsWith(END)) {
                    long cosTime = System.currentTimeMillis() - nowTime;
                    //结束计时，并计算出方法执行时间
                    removeMonitor();
                    //计算出耗时
                    if (cosTime > monitorTime) {
                        String msg = "开始检测时间：" + nowTime + " 栈读取时间：" + stackMsgTime + " UI主线程耗时:" + cosTime + " 栈轨迹:"
                                + stackMsg;
                        ViLog.e(TAG, msg);
                        onFluentNext(cosTime, new FluentBean(nowTime, stackMsgTime, stackMsg));
                    }
                }
            }
        });
    }

    /**
     * 异步获取当前栈信息
     */
    private Runnable mLogRunnable = () -> {
        stackMsg = getStackMsg();
        stackMsgTime = System.currentTimeMillis();
    };

    /**
     * 获取当前栈信息
     *
     * @return 站信息
     */
    private String getStackMsg() {
        //打印出执行的耗时方法的栈消息
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();
        for (StackTraceElement s : stackTrace) {
            sb.append(s.toString());
            sb.append("\n");
        }
        return sb.toString();
    }


    /**
     * 开始计时
     */
    private void startMonitor() {
        TimeoutHelper.getInstance().putTask(mLogRunnable, monitorTime);
    }

    /**
     * 停止计时
     */
    private void removeMonitor() {
        TimeoutHelper.getInstance().removeTask(mLogRunnable);
    }

    /************************ 卡顿回调 start *******************/

    //listener全局变量
    private OnFluentListener onFluentListener;

    //供外部调用的set方法
    public FluentHelper setOnFluentListener(OnFluentListener onFluentListener) {
        this.onFluentListener = onFluentListener;
        return singleton;
    }

    //内部调用传递信息到外部
    private void onFluentNext(long cosTime, FluentBean fluentBean) {
        if (this.onFluentListener != null && mainHandler != null) {
            mainHandler.post(() -> onFluentListener.onFluent(cosTime, fluentBean));
        }
    }

    //定义listener
    public interface OnFluentListener {

        void onFluent(long cosTime, FluentBean fluentBean);
    }

    /************************ 卡顿回调 end  *******************/
}
