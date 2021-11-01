package com.tencent.vigatom.helper;

import android.content.Context;
import android.content.Intent;
import com.tencent.vigatom.ue.activity.CrashActivity;
import com.tencent.vigatom.utils.ViLog;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * 崩溃处理
 * 简单来说UncaughtExceptionHandler就是用于在线程中当一些系统没有捕获的异常发生的时候来处理这些异常的。
 * 你可以使用系统默认的处理方式，你也可以通过Thread.setDefaultUncaughtExceptionHandler()方法
 * 设置你自己定义的异常处理。
 */
public class CrashHelper{

    private static final String TAG = CrashHelper.class.getSimpleName();
    private static volatile CrashHelper instance;//单例

    /**
     * 单例
     */
    public static CrashHelper getInstance() {
        if (instance == null) {
            synchronized (CrashHelper.class) {
                if (instance == null) {
                    instance = new CrashHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 内部处理异常信息
     *
     * @param context 上下文
     * @param t 线程
     * @param e 异常
     */
    public void uncaughtException(Context context, Thread t, Throwable e) {
        try {
            // 当主线程或子线程抛出异常时都会调用，可能运行在非UI线程中。
            //异常处理内部建议手动try{}catch(Throwable e){} ，以防handlerException内部再次抛出异常，导致循环调用
            if (handleException(e)) {
                //App处理
                String errTrace = getExceptionTrace(context, e);
                //打印输出
                ViLog.e(TAG + "::uncaughtException-->" + errTrace);
                //记录日志信息
                LogHelper.writeCrashLog(context, errTrace);
                showCrashWindow(context, errTrace);
            } else {
                systemException(t, e);
            }
        } catch (Exception e1){
            e1.printStackTrace();
        }
    }

    /**
     * 交给系统内部处理异常
     */
    private void systemException(Thread t, Throwable e) {
        //如果系统提供了默认的异常处理器，则交给系统去结束程序，否则就自己结束自己
        if (Thread.getDefaultUncaughtExceptionHandler() != null) {
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(t, e);
        } else {
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    /**
     * 判断是否有数据需要APP处理，如果没可处理的数据就没必要弹窗了
     */
    private boolean handleException(Throwable ex){
        if (ex == null) {
            return false;
        }
        return ex.getLocalizedMessage() != null;
    }

    /**
     * 读取异常消息
     * @param ex 异常
     */
    private String getExceptionTrace(Context context,Throwable ex) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ex.printStackTrace(ps);
        String errTrace = new String(baos.toByteArray(), StandardCharsets.UTF_8);
        try {
            baos.close();
        } catch (IOException error) {
            //打印
            ViLog.e(TAG+"::getExceptionTrace-->"+error.toString());
            //记录日志信息
            LogHelper.writeCrashLog(context,TAG+"::getExceptionTrace-->"+error.toString());
        }
        return errTrace;
    }

    /**
     * 展示异常信息
     * @param des 异常信息
     */
    private void showCrashWindow(Context context, String des) {
        Intent intent = new Intent(context, CrashActivity.class);
        intent.putExtra(CrashActivity.ERROR_INTENT, des);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        //退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}