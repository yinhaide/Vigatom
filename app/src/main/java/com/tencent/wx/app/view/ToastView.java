package com.tencent.wx.app.view;

import android.view.View;
import com.haideyin.app.R;
import com.tencent.vigatom.Vigatom;
import com.tencent.vigatom.ue.view.ViView;
import com.tencent.vigatom.utils.ScreenUtil;

/**
 * 吐司相关的演示页面
 */
public class ToastView extends ViView {

    @Override
    public int onInflateLayout() {
        return ScreenUtil.isLanscape(Vigatom.getAppContext()) ? R.layout.view_toast_l
                : R.layout.view_toast_p;
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
        inflateView.findViewById(R.id.bt_toast_1_second).setOnClickListener(v -> {
            toast("吐司1秒", 1000);
        });
        inflateView.findViewById(R.id.bt_toast_3_second).setOnClickListener(v -> {
            toast(getContext().getString(R.string.toast_3_second));
        });
        inflateView.findViewById(R.id.bt_toast_5_second).setOnClickListener(v -> {
            toast("吐司5秒", 5000);
        });
        inflateView.findViewById(R.id.bt_toast_warning_img).setOnClickListener(v -> {
            toast("吐司告警图片", R.drawable.warning, 5000);
        });
        inflateView.findViewById(R.id.bt_toast_success_img).setOnClickListener(v -> {
            toast("吐司成功图片", R.drawable.success, 5000);
        });
        inflateView.findViewById(R.id.bt_toast_pop).setOnClickListener(v -> {
            toastPop("吐司标题" + "\n" + "CPU卡类型：AAA卡商" + "\n" + "M1卡类型：BBB卡商" + "\n" + "用户数量：100万");
        });
    }
}
