package com.tencent.wx.app.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.haideyin.app.R;
import com.tencent.vigatom.Vigatom;
import com.tencent.vigatom.bean.DialogBean;
import com.tencent.vigatom.callback.CostomCallback;
import com.tencent.vigatom.ue.view.ViView;
import com.tencent.vigatom.ue.widget.ViEditTextWidget;
import com.tencent.vigatom.utils.ScreenUtil;

/**
 * 对话框
 */
public class DialogView extends ViView {

    @Override
    public int onInflateLayout() {
        return ScreenUtil.isLanscape(Vigatom.getAppContext()) ? R.layout.view_dialog_l
                : R.layout.view_dialog_p;
    }

    @Override
    public void onCreate(View inflateView) {
        initView(inflateView);
    }

    /**
     * 页面初始化
     *
     * @param inflateView 页面信息
     */
    private void initView(View inflateView) {
        inflateView.findViewById(R.id.bt_dialog).setOnClickListener(v -> {
            showDialog("标准的Dialog显示");
        });
        inflateView.findViewById(R.id.bt_dialog_no_title).setOnClickListener(v -> {
            DialogBean dialogBean = new DialogBean();
            dialogBean.setHasTitle(false);
            showDialog("取消标题栏", dialogBean);
        });
        inflateView.findViewById(R.id.bt_dialog_no_cancel).setOnClickListener(v -> {
            DialogBean dialogBean = new DialogBean();
            dialogBean.setHasCancel(false);
            showDialog("隐藏取消按钮", dialogBean);
        });
        inflateView.findViewById(R.id.bt_dialog_content).setOnClickListener(v -> {
            DialogBean dialogBean = new DialogBean();
            dialogBean.setConfirm("是的");
            dialogBean.setCancel("不是");
            dialogBean.setTitle("语言调查");
            dialogBean.setCancelCallback(new CostomCallback<TextView>() {
                @Override
                public void onResult(TextView result) {
                    toast("你是在逗我么？");
                }
            });
            dialogBean.setConfirmCallback(new CostomCallback<TextView>() {
                @Override
                public void onResult(TextView result) {
                    toast("你还是智商在线！");
                }
            });
            showDialog("Java是世界上最好的语言吗?", dialogBean);
        });
        inflateView.findViewById(R.id.bt_dialog_edit).setOnClickListener(v -> {
            showEditDialog("请按键输入", new CostomCallback<ViEditTextWidget>() {
                @Override
                public void onResult(ViEditTextWidget result) {
                    toast("输入内容：" + result.getContent());
                }
            });
        });
        inflateView.findViewById(R.id.bt_dialog_picture).setOnClickListener(v -> {
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ScaleType.CENTER_CROP);
            imageView.setImageResource(R.drawable.dialog);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.topMargin = 20;
            params.bottomMargin = 20;
            imageView.setLayoutParams(params);
            DialogBean dialogBean = new DialogBean();
            showCustomDialog(imageView, dialogBean);
        });
        inflateView.findViewById(R.id.bt_dialog_picture_over).setOnClickListener(v -> {
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ScaleType.CENTER_CROP);
            imageView.setImageResource(R.drawable.dialog);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            //演示控件超过屏幕尺寸被自动等比例缩放
            params.width = 1600;//默认宽度是1080
            params.height = 960;
            params.topMargin = 20;
            params.bottomMargin = 20;
            imageView.setLayoutParams(params);
            DialogBean dialogBean = new DialogBean();
            showCustomDialog(imageView, dialogBean);
        });
    }
}
