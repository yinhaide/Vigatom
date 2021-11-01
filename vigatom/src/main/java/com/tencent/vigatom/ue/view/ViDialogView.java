package com.tencent.vigatom.ue.view;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tencent.vigatom.R;
import com.tencent.vigatom.Vigatom;
import com.tencent.vigatom.api.IViKeyboard;
import com.tencent.vigatom.callback.CostomCallback;
import com.tencent.vigatom.ue.layout.PercentFrameLayout;
import com.tencent.vigatom.ue.layout.PercentLinearLayout;
import com.tencent.vigatom.ue.widget.ViEditTextWidget;
import com.tencent.vigatom.utils.ScreenUtil;
import com.tencent.vigatom.utils.ViViewUtil;
import com.tencent.vigatom.utils.ViewAnimUtil;

/**
 * 自定义对话框，支持灵活扩展
 */
public class ViDialogView extends ViView {

    private static final int FADE_DURATION = 300;//渐变显示与渐变隐藏的时间间隔
    //最大宽度百分比
    public static float MAX_WIDTH_PERCENT = 0.92f;
    //最小宽度百分比
    public static float MIN_WIDTH_PERCENT = 0.4f;
    //记录自定义View的TAG
    public static final String CUSTOM_TAG = "CUSTOM";
    //子控件
    private TextView titleTv, contentTv, cancelTv, confirmTv, minMaxLimit;
    //编辑框
    private ViEditTextWidget viEditTextWidget;
    //自定义内容容器
    private PercentFrameLayout contentFl;
    //点击事件
    private CostomCallback<View> onCancleClickListener;
    private CostomCallback<View> onConfirmClickListener;
    //当前回调出去的View
    private View modeView;
    //按钮之间的分割线
    private View lineView;
    //容器
    private PercentLinearLayout contentLL;

    public ViDialogView() {

    }

    @Override
    public int onInflateLayout() {
        // 将Xml中定义的布局解析出来。
        return ScreenUtil.isLanscape(Vigatom.getAppContext()) ? R.layout.vigatom_widget_dialog_l
                : R.layout.vigatom_widget_dialog_p;
    }

    /**
     * 渐变隐藏对话框
     */
    public void hideFade(ViewGroup rootView){
        View dialogView = getDialogView(rootView,false);
        if (dialogView != null) {
            //渐变隐藏
            ViewAnimUtil.hideFade(dialogView, 1, 0, FADE_DURATION, null);
            PercentFrameLayout contentFl = dialogView.findViewById(R.id.contentFl);
            //先清除以前的自定义View，不然会造成内存泄漏
            View customView = contentFl.findViewWithTag(ViDialogView.CUSTOM_TAG);
            if (customView != null) {
                contentFl.removeView(customView);
            }
            rootView.removeView(dialogView);
        }
    }

    /**
     * 吐司渐变显示
     */
    public void showFade(ViewGroup rootView){
        View dialogView = getDialogView(rootView,true);
        if(dialogView != null){
            dialogView.bringToFront();
            //渐变显示
            ViewAnimUtil.showFade(dialogView, 0, 1, FADE_DURATION,null);
        }
    }

    public TextView getTitleTv() {
        return titleTv;
    }

    public TextView getContentTv() {
        return contentTv;
    }

    public TextView getCancelTv() {
        return cancelTv;
    }

    public TextView getConfirmTv() {
        return confirmTv;
    }

    public PercentFrameLayout getContentFl() {
        return contentFl;
    }

    public ViEditTextWidget getViEditTextWidget() {
        return viEditTextWidget;
    }

    public void setOnCancel(CostomCallback onCancel) {
        this.onCancleClickListener = onCancel;
    }

    public void setOnConfirm(CostomCallback onConfirm) {
        this.onConfirmClickListener = onConfirm;
    }

    public void setModeView(View modeView) {
        this.modeView = modeView;
    }

    public View getLineView() {
        return lineView;
    }

    public TextView getMinMaxLimit() {
        return minMaxLimit;
    }

    public PercentLinearLayout getContentLL() {
        return contentLL;
    }

    /**
     * 获取弹窗的页面
     *
     * @param rootView 跟页面
     * @param create 是否要创建和初始化
     * @return 返回页面
     */
    private View getDialogView(ViewGroup rootView, boolean create) {
        //创建
        View dialogView = ViViewUtil.getView(rootView, getClass());
        if (create) {
            dialogView = bind(rootView);
            init(dialogView);
        }
        return dialogView;
    }

    /**
     * 每次都要做页面初始化操作
     *
     * @param dialogView 父类的View
     */
    private void init(View dialogView) {
        contentTv = dialogView.findViewById(R.id.text);
        titleTv = dialogView.findViewById(R.id.title);
        cancelTv = dialogView.findViewById(R.id.cancel);
        confirmTv = dialogView.findViewById(R.id.confirm);
        contentFl = dialogView.findViewById(R.id.contentFl);
        viEditTextWidget = dialogView.findViewById(R.id.edit);
        minMaxLimit = dialogView.findViewById(R.id.min_max_limit);
        contentLL = dialogView.findViewById(R.id.content_ll);
        viEditTextWidget.setOnChangeListener(content -> {
            minMaxLimit.setText(content);
        });
        lineView = dialogView.findViewById(R.id.sep_line);
        confirmTv.setOnClickListener(v -> {
            finish();
            if (onConfirmClickListener != null) {
                onConfirmClickListener.onResult(modeView);
            }
        });
        cancelTv.setOnClickListener(v -> {
            finish();
            if (onCancleClickListener != null) {
                onCancleClickListener.onResult(modeView);
            }
        });
        //支持点击滑动
        contentTv.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    public boolean onKey(IViKeyboard iViKeyBoard) {
        if(iViKeyBoard.isKeyUp()){
            if(iViKeyBoard.isCancel() && cancelTv != null){
                cancelTv.performClick();
            }
            if(iViKeyBoard.isEnter() && confirmTv != null){
                confirmTv.performClick();
            }
            viEditTextWidget.keyUp(iViKeyBoard);
        }
        return super.onKey(iViKeyBoard);
    }

    @Override
    public void finish() {
        hideFade(rootView);
    }
}
