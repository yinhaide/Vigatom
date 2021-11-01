package com.tencent.vigatom.helper;

import android.view.View;
import android.view.ViewGroup;
import com.tencent.vigatom.Vigatom;
import com.tencent.vigatom.bean.DialogBean;
import com.tencent.vigatom.bean.DialogBean.MODE;
import com.tencent.vigatom.callback.CostomCallback;
import com.tencent.vigatom.ue.view.ViDialogView;
import com.tencent.vigatom.ue.widget.ViEditTextWidget;
import com.tencent.vigatom.utils.ScreenUtil;

/**
 * Dialog的工具类
 */
public class DialogHelper {

    private static final String TAG = DialogHelper.class.getSimpleName();
    private static volatile DialogHelper instance;

    private DialogHelper() {

    }

    public static DialogHelper getInstance() {
        if (instance == null) {
            synchronized (DialogHelper.class) {
                if (instance == null) {
                    instance = new DialogHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 渐变显示
     *
     * @param hit 提示语
     */
    public void showEdit(ViewGroup rootView, String hit, CostomCallback<ViEditTextWidget> confirmCallback) {
        DialogBean dialogBean = new DialogBean();
        dialogBean.setMode(MODE.EDIT.setObj(hit));
        dialogBean.setConfirmCallback(confirmCallback);
        show(rootView, dialogBean);
    }

    /**
     * 统一的显示处理
     *
     * @param rootView 根布局
     * @param dialogBean 显示参数
     */
    public void show(ViewGroup rootView, DialogBean dialogBean) {
        if (dialogBean == null) {
            return;
        }
        ViDialogView viDialogView = new ViDialogView();
        viDialogView.showFade(rootView);
        //设置标题和确认取消俺就
        if (dialogBean.getTitle() != null) {
            viDialogView.getTitleTv().setText(dialogBean.getTitle());
        }
        if (dialogBean.getCancel() != null) {
            viDialogView.getCancelTv().setText(dialogBean.getCancel());
        }
        if (dialogBean.getConfirm() != null) {
            viDialogView.getConfirmTv().setText(dialogBean.getConfirm());
        }
        //设置显示和隐藏的情况
        if (dialogBean.isHasCancel()) {
            viDialogView.getCancelTv().setVisibility(View.VISIBLE);
        } else {
            viDialogView.getCancelTv().setVisibility(View.GONE);
            viDialogView.getLineView().setVisibility(View.GONE);
        }
        if (dialogBean.isHasConfirm()) {
            viDialogView.getConfirmTv().setVisibility(View.VISIBLE);
        } else {
            viDialogView.getConfirmTv().setVisibility(View.GONE);
            viDialogView.getLineView().setVisibility(View.GONE);
        }
        if (dialogBean.isHasTitle()) {
            viDialogView.getTitleTv().setVisibility(View.VISIBLE);
        } else {
            viDialogView.getTitleTv().setVisibility(View.GONE);
        }
        //先清除以前的自定义View
        View tagView = viDialogView.getContentFl().findViewWithTag(ViDialogView.CUSTOM_TAG);
        if (tagView != null) {
            viDialogView.getContentFl().removeView(tagView);
        }
        //设置不同的模式
        if (dialogBean.getMode() == MODE.TEXT && (dialogBean.getMode().getObj() instanceof String)) {
            viDialogView.getViEditTextWidget().setVisibility(View.GONE);
            viDialogView.getContentTv().setVisibility(View.VISIBLE);
            viDialogView.getContentTv().setText((String) dialogBean.getMode().getObj());
            viDialogView.setModeView(viDialogView.getContentTv());
            //设置辅助最小最大宽度的基线
            viDialogView.getMinMaxLimit().setText((String) dialogBean.getMode().getObj());
        } else if (dialogBean.getMode() == MODE.EDIT && (dialogBean.getMode().getObj() instanceof String)) {
            viDialogView.getViEditTextWidget().setVisibility(View.VISIBLE);
            viDialogView.getContentTv().setVisibility(View.GONE);
            viDialogView.getViEditTextWidget().setHint((String) dialogBean.getMode().getObj());
            viDialogView.setModeView(viDialogView.getViEditTextWidget());
            //设置辅助最小最大宽度的基线
            viDialogView.getMinMaxLimit().setText((String) dialogBean.getMode().getObj());
        } else if (dialogBean.getMode() == MODE.CUSTOM && (dialogBean.getMode().getObj() instanceof View)) {
            View view = (View) dialogBean.getMode().getObj();
            if (view.getLayoutParams() != null) {
                //长度超过屏幕之后开始等比例缩放
                float maxWith = ScreenUtil.getScreenWidth(Vigatom.getAppContext()) * ViDialogView.MAX_WIDTH_PERCENT;
                if (ScreenUtil.isLanscape(Vigatom.getAppContext())) {
                    maxWith = ScreenUtil.getHeightWidth(Vigatom.getAppContext()) * ViDialogView.MIN_WIDTH_PERCENT;
                }
                //长度超过屏幕最大宽度之后开始等比例缩放
                //float minWith = ScreenUtil.getScreenWidth(Vigatom.getAppContext()) * minWidthPercent;
                if (maxWith < view.getLayoutParams().width) {
                    view.getLayoutParams().height = (int) (view.getLayoutParams().height * (maxWith / view
                            .getLayoutParams().width));
                    view.getLayoutParams().width = (int) (maxWith);
                    viDialogView.getMinMaxLimit().getLayoutParams().width = view.getLayoutParams().width;
                }
            }
            viDialogView.getViEditTextWidget().setVisibility(View.GONE);
            viDialogView.getContentTv().setVisibility(View.GONE);
            view.setTag(ViDialogView.CUSTOM_TAG);
            viDialogView.getContentFl().addView(view);
            viDialogView.setModeView(view);
        }
        //设置回调
        viDialogView.setOnCancel(dialogBean.getCancelCallback());
        viDialogView.setOnConfirm(dialogBean.getConfirmCallback());
    }

    /**
     * 渐变隐藏
     */
    public void dismiss(ViewGroup rootView) {
        ViDialogView viDialogView = new ViDialogView();
        viDialogView.hideFade(rootView);
    }
}
