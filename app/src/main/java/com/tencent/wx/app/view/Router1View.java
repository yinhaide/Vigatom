package com.tencent.wx.app.view;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.haideyin.app.R;
import com.tencent.vigatom.Vigatom;
import com.tencent.vigatom.ue.view.ViView;
import com.tencent.vigatom.utils.ScreenUtil;
import com.tencent.vigatom.utils.ViLog;
import com.tencent.vigatom.utils.ViViewUtil;

/**
 * 路由跳转的测试页面
 */
public class Router1View extends ViView {

    private TextView tvRouter;
    private Button jumpBtn;

    @Override
    public int onInflateLayout() {
        return ScreenUtil.isLanscape(Vigatom.getAppContext()) ? R.layout.view_router_1_l
                : R.layout.view_router_1_p;
    }

    @Override
    public void onCreate(View inflateView) {
        ViLog.d("Router1View新创建");
        initView(inflateView);
    }

    @Override
    public void onNexts(Object object) {
        ViLog.d("Router1View可执行业务操作");
        tvRouter.setText("当前页面层级：" + ViViewUtil.getRouter(rootView).toString());
    }

    /**
     * 页面初始化
     *
     * @param inflateView 页面信息
     */
    private void initView(View inflateView) {
        tvRouter = inflateView.findViewById(R.id.tv_router);
        jumpBtn = inflateView.findViewById(R.id.bt_go);
        jumpBtn.setOnClickListener(v -> startView(RouterView.class));
    }
}
