package com.tencent.wx.app;

import com.tencent.vigatom.Vigatom;
import com.tencent.vigatom.app.ViApplication;
import com.tencent.vigatom.utils.ViLog;

/**
 * 自己的Application
 */
public class WXApplication extends ViApplication {

    private static final String TAG = WXApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化配置
        Vigatom.getInstance()
                //初始化
                .init(this)
                //卡顿检测设置
                .enableFluent(true, 400, (cosTime, fluentBean) -> {
                    ViLog.e(TAG, fluentBean.toString());
                })
                //内存泄漏检测
                .enableLeak(true, 5000, (cosTime, hashMap) -> {
                    String msg = " 内存泄漏检测性能消耗:" + cosTime + " 泄漏内容:" + hashMap.toString();
                    ViLog.e(TAG, msg);
                })
                //崩溃处理
                .enableUncaughtException(true, null, null)
                //设置百分比布局预览方向
                .setPreviewDirection(false);
    }
}
