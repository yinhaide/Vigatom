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
import com.tencent.wx.app.bean.RouterBean;

/**
 * 路由跳转的测试页面
 */
public class Router2View extends ViView {

    private TextView tvRouter;
    private Button jumpBtn;

    @Override
    public int onInflateLayout() {
        return ScreenUtil.isLanscape(Vigatom.getAppContext()) ? R.layout.view_router_2_l
                : R.layout.view_router_2_p;
    }

    @Override
    public void onCreate(View inflateView) {
        initView(inflateView);
    }

    @Override
    public void onNexts(Object object) {
        tvRouter.setText("当前页面层级：" + ViViewUtil.getRouter(rootView).toString());
        if (object instanceof RouterBean) {
            RouterBean routerBean = (RouterBean) object;
            ViLog.d(routerBean.toString());
            toast("收到数据：" + routerBean.toString());
        }
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
