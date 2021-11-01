package com.tencent.vigatom.helper;

import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.tencent.vigatom.Vigatom;
import com.tencent.vigatom.ue.view.ViToastView;

/**
 * 吐司的工具类
 */
public class ToastHelper {

    private static final String TAG = ToastHelper.class.getSimpleName();
    public static int DEFAULT_MILTIME = 3000;//默认提示时间
    private static volatile ToastHelper instance;
    //延迟小时的任务
    private Runnable dismissRunnable;

    private ToastHelper() {

    }

    public static ToastHelper getInstance() {
        if (instance == null) {
            synchronized (ToastHelper.class) {
                if (instance == null) {
                    instance = new ToastHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 渐变显示
     *
     * @param tips 提示语
     * @param duration 显示时间间隔，-1为永不消失
     * @param isCenter 是否居中显示
     */
    public void show(ViewGroup rootView, String tips, int imgRes, int duration, boolean isCenter) {
        ViToastView viToastView = new ViToastView();
        //先清掉上次的延迟任务
        if (dismissRunnable != null) {
            TimeoutHelper.getInstance().removeTask(dismissRunnable);
            dismissRunnable = null;
        }
        dismissRunnable = () -> Vigatom.getInstance().runUIThread(() -> {
            dismiss(rootView);
        });
        viToastView.showFade(rootView, imgRes, tips);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viToastView.getContentLL().getLayoutParams();
        params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.removeRule(RelativeLayout.CENTER_IN_PARENT);
        //是否居中
        if (isCenter) {
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        viToastView.getContentLL().setLayoutParams(params);
        if (duration >= 0) {
            TimeoutHelper.getInstance().putTask(dismissRunnable, duration);
        }
    }

    /**
     * 供给外部立刻释放吐司和定时器
     */
    public void dismiss(ViewGroup rootView){
        ViToastView viToastView = new ViToastView();
        if(dismissRunnable != null){
            TimeoutHelper.getInstance().removeTask(dismissRunnable);
            dismissRunnable = null;
        }
        viToastView.hideFade(rootView);
    }
}
