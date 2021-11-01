package com.tencent.vigatom.ue.view;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.tencent.vigatom.R;
import com.tencent.vigatom.Vigatom;
import com.tencent.vigatom.ue.layout.PercentLinearLayout;
import com.tencent.vigatom.utils.ScreenUtil;
import com.tencent.vigatom.utils.ViLog;
import com.tencent.vigatom.utils.ViewAnimUtil;

/**
 * 自定义吐司
 */
public class ViToastView extends ViView {

    private static final int FADE_DURATION = 500;//渐变显示与渐变隐藏的时间间隔
    //文字
    private TextView textView;
    //图标
    private ImageView imageView;
    //容器
    private PercentLinearLayout contentLL;

    @Override
    public int onInflateLayout() {
        // 将Xml中定义的布局解析出来。
        return ScreenUtil.isLanscape(Vigatom.getAppContext()) ? R.layout.vigatom_widget_toast_l
                : R.layout.vigatom_widget_toast_p;
    }

    /**
     * 渐变隐藏吐司
     */
    public void hideFade(ViewGroup rootView){
        View toastView = getToastView(rootView,false);
        if (toastView != null) {
            try {
                //渐变隐藏
                ViewAnimUtil.hideFade(toastView, 1, 0, FADE_DURATION, null);
                rootView.removeView(toastView);
            } catch (Exception e) {
                ViLog.e("删除出错：" + e.toString());
            }
        }
    }

    /**
     * 吐司渐变显示
     *
     * @param imgRes 吐司左边的图片
     * @param tips 提示内容
     */
    public void showFade(ViewGroup rootView, int imgRes, String tips) {
        View toastView = getToastView(rootView, true);
        if (toastView != null) {
            try {
                textView.setText(tips);
                if (imgRes > 0) {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageResource(imgRes);
                } else {
                    imageView.setVisibility(View.GONE);
                }
                toastView.bringToFront();
                //渐变显示
                ViewAnimUtil.showFade(toastView, 0, 1, FADE_DURATION, null);
            } catch (Exception e) {
                ViLog.e("显示出错：" + e.toString());
            }
        }
    }

    /**
     * 获取弹窗的页面
     *
     * @param rootView 跟页面
     * @param create 是否要创建和初始化
     * @return 返回页面
     */
    private View getToastView(ViewGroup rootView, boolean create) {
        //创建
        View toastView = rootView.findViewWithTag(getClass().getSimpleName());
        if (create) {
            toastView = bind(rootView);
            init(toastView);
        }
        return toastView;
    }

    /**
     * 每次都要做页面初始化操作
     * @param toastView 父类的View
     */
    private void init(View toastView) {
        textView = toastView.findViewById(R.id.text);
        imageView = toastView.findViewById(R.id.iv);
        contentLL = toastView.findViewById(R.id.content_ll);
        //支持点击滑动
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public PercentLinearLayout getContentLL() {
        return contentLL;
    }

    @Override
    public void finish() {
        hideFade(rootView);
    }
}
