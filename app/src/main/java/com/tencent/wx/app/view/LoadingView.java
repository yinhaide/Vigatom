package com.tencent.wx.app.view;

import android.view.View;
import com.haideyin.app.R;
import com.tencent.vigatom.Vigatom;
import com.tencent.vigatom.ue.view.ViView;
import com.tencent.vigatom.utils.ScreenUtil;

/**
 * 进度加载相关的演示页面
 */
public class LoadingView extends ViView {

    @Override
    public int onInflateLayout() {
        return ScreenUtil.isLanscape(Vigatom.getAppContext()) ? R.layout.view_loading_l
                : R.layout.view_loading_p;
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
        inflateView.findViewById(R.id.bt_loading_begin).setOnClickListener(v -> {
            showLoading("加载中...");
        });
        inflateView.findViewById(R.id.bt_loading_30).setOnClickListener(v -> {
            showLoadingPercent("完成30%", 30);
        });
        inflateView.findViewById(R.id.bt_loading_60).setOnClickListener(v -> {
            showLoadingPercent("完成60%", 60);
        });
        inflateView.findViewById(R.id.bt_loading_100).setOnClickListener(v -> {
            showLoadingPercent("完成100%", 100);
        });
        inflateView.findViewById(R.id.bt_loading_end).setOnClickListener(v -> {
            hideLoading();
        });
    }
}
