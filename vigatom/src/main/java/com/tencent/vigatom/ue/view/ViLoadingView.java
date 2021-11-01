package com.tencent.vigatom.ue.view;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.tencent.vigatom.R;
import com.tencent.vigatom.Vigatom;
import com.tencent.vigatom.utils.ScreenUtil;
import com.tencent.vigatom.utils.ViewAnimUtil;

/**
 * 自定义加载中
 */
public class ViLoadingView extends ViView {

    private static final int FADE_DURATION = 300;//渐变显示与渐变隐藏的时间间隔
    private TextView textView;
    private ViCirclePercentView circlePercentView;
    private ProgressBar progressBar;

    @Override
    public int onInflateLayout() {
        // 将Xml中定义的布局解析出来。
        return ScreenUtil.isLanscape(Vigatom.getAppContext()) ? R.layout.vigatom_widget_loading_l
                : R.layout.vigatom_widget_loading_p;
    }

    /**
     * 渐变隐藏对话框
     */
    public void hideFade(ViewGroup rootView){
        View loadingView = getLoadingView(rootView,false);
        if (loadingView != null) {
            //渐变隐藏
            ViewAnimUtil.hideFade(loadingView, 1, 0, FADE_DURATION, null);
            rootView.removeView(loadingView);
        }
    }

    /**
     * 吐司渐变显示
     *
     * @param tips 提示内容
     * @param percent 百分比 0-100
     */
    public void showFade(ViewGroup rootView, String tips, int percent) {
        View loadingView = getLoadingView(rootView, true);
        if (loadingView != null) {
            textView.setText(tips);
            loadingView.bringToFront();
            if (percent > 0) {
                circlePercentView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                circlePercentView.setPercent(percent);
                //持续显示进度的不需要渐变
            } else {
                progressBar.setVisibility(View.VISIBLE);
                circlePercentView.setVisibility(View.GONE);
                //渐变显示
                ViewAnimUtil.showFade(loadingView, 0, 1, FADE_DURATION, null);
            }
        }
    }

    /**
     * 获取弹窗的页面
     * @param rootView 跟页面
     * @param create 是否要创建和初始化
     * @return 返回页面
     */
    private View getLoadingView(ViewGroup rootView,boolean create){
        //创建
        View loadingView = rootView.findViewWithTag(getClass().getSimpleName());
        if(create) {
            loadingView = bind(rootView);
            init(loadingView);
        }
        return loadingView;
    }

    /**
     * 每次都要做页面初始化操作
     *
     * @param loadingView 父类的View
     */
    private void init(View loadingView) {
        textView = loadingView.findViewById(R.id.text);
        //支持点击滑动
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        //百分比的View
        circlePercentView = loadingView.findViewById(R.id.circle_view);
        //转圈圈
        progressBar = loadingView.findViewById(R.id.loading_progress);
    }

    @Override
    public void finish() {
        hideFade(rootView);
    }
}
