package com.tencent.wx.app.view;

import android.view.View;
import android.widget.TextView;
import com.haideyin.app.R;
import com.tencent.vigatom.Vigatom;
import com.tencent.vigatom.bean.ViewBean;
import com.tencent.vigatom.ue.view.ViView;
import com.tencent.vigatom.utils.ScreenUtil;
import com.tencent.vigatom.utils.ViViewUtil;
import com.tencent.wx.app.bean.RouterBean;

/**
 * 路由跳转的演示页面
 */
public class RouterView extends ViView {

    private TextView tvRouter;

    @Override
    public int onInflateLayout() {
        return ScreenUtil.isLanscape(Vigatom.getAppContext()) ? R.layout.view_router_l
                : R.layout.view_router_p;
    }

    @Override
    public void onCreate(View inflateView) {
        initView(inflateView);
    }

    @Override
    public void onNexts(Object object) {
        tvRouter.setText("当前页面层级：" + ViViewUtil.getRouter(rootView).toString());
    }

    /**
     * 页面初始化
     *
     * @param inflateView 页面信息
     */
    private void initView(View inflateView) {
        inflateView.findViewById(R.id.bt_kill_self).setOnClickListener(v -> {
            //页面跳转的参数集合
            ViewBean viewBean = new ViewBean();
            //页面跳转清除自身，返回会直接回到上个页面
            viewBean.setOriginDestroy(true);
            //开始跳转
            startView(Router2View.class, viewBean);
        });
        inflateView.findViewById(R.id.bt_save_self).setOnClickListener(v -> {
            //开始跳转
            startView(Router2View.class);
        });
        inflateView.findViewById(R.id.bt_refresh_target).setOnClickListener(v -> {
            //页面跳转的参数集合
            ViewBean viewBean = new ViewBean();
            //页面跳转清除目标页面再跳转
            viewBean.setTargetRefresh(true);
            //开始跳转，跳转到自身，先删掉再重新创建，测试onCreate是否执行
            startView(Router1View.class, viewBean);
        });
        inflateView.findViewById(R.id.bt_with_data).setOnClickListener(v -> {
            //页面跳转的参数集合
            ViewBean viewBean = new ViewBean();
            //页面跳转携带的数据，数据需要实现序列化
            viewBean.setObject(new RouterBean(getClass().getSimpleName(), "我要测试页面跳转携带数据"));
            //开始跳转
            startView(Router2View.class, viewBean);
        });
        inflateView.findViewById(R.id.bt_clear_top).setOnClickListener(v -> {
            //页面跳转的参数集合
            ViewBean viewBean = new ViewBean();
            //页面跳转清除目标页面上面所有的页面
            viewBean.setClearTop(true);
            //开始跳转，跳转到自身，先删掉再重新创建，测试onCreate是否执行
            startView(Router1View.class, viewBean);
        });
        tvRouter = inflateView.findViewById(R.id.tv_router);
    }
}
