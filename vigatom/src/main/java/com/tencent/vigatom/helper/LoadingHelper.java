package com.tencent.vigatom.helper;

import android.view.ViewGroup;
import com.tencent.vigatom.ue.view.ViLoadingView;

/**
 * Loading的工具类
 */
public class LoadingHelper {

    private static final String TAG = LoadingHelper.class.getSimpleName();
    private static volatile LoadingHelper instance;

    private LoadingHelper() {

    }

    public static LoadingHelper getInstance() {
        if (instance == null) {
            synchronized (LoadingHelper.class) {
                if (instance == null) {
                    instance = new LoadingHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 渐变显示加载中
     *
     * @param tips 提示语
     */
    public void showLoading(ViewGroup rootView, String tips) {
        ViLoadingView viLoadingView = new ViLoadingView();
        viLoadingView.showFade(rootView, tips, -1);
    }

    /**
     * 渐变显示圆形进度
     *
     * @param percent 进度
     */
    public void showLoadingPercent(ViewGroup rootView, String tips, int percent) {
        ViLoadingView viLoadingView = new ViLoadingView();
        viLoadingView.showFade(rootView, tips, percent);
    }

    /**
     * 渐变隐藏
     */
    public void dismiss(ViewGroup rootView) {
        ViLoadingView viLoadingView = new ViLoadingView();
        viLoadingView.hideFade(rootView);
    }
}
