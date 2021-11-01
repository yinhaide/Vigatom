package com.tencent.wx.app.view;

import android.view.View;
import android.widget.TextView;
import com.haideyin.app.R;
import com.tencent.vigatom.Vigatom;
import com.tencent.vigatom.ue.view.ViView;
import com.tencent.vigatom.utils.ScreenUtil;
import com.tencent.vigatom.utils.ViLog;

/**
 * 主功能演示页面
 */
public class MainView extends ViView {

    @Override
    public int onInflateLayout() {
        return ScreenUtil.isLanscape(Vigatom.getAppContext()) ? R.layout.view_main_l
                : R.layout.view_main_p;
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
        inflateView.findViewById(R.id.bt_toast).setOnClickListener(v -> startView(ToastView.class));
        inflateView.findViewById(R.id.bt_loading).setOnClickListener(v -> startView(LoadingView.class));
        inflateView.findViewById(R.id.bt_dialog).setOnClickListener(v -> startView(DialogView.class));
        inflateView.findViewById(R.id.bt_statusbar).setOnClickListener(v -> startView(StatusBarView.class));
        inflateView.findViewById(R.id.bt_fluent).setOnClickListener(v -> startView(FluentView.class));
        inflateView.findViewById(R.id.bt_leak).setOnClickListener(v -> startView(LeakView.class));
        inflateView.findViewById(R.id.bt_keyboard).setOnClickListener(v -> startView(KeyBoardView.class));
        inflateView.findViewById(R.id.bt_router).setOnClickListener(v -> startView(Router1View.class));
        inflateView.findViewById(R.id.bt_presentation).setOnClickListener(v -> startView(PresentationView.class));
        inflateView.findViewById(R.id.bt_precent).setOnClickListener(v -> startView(PercentView.class));
        inflateView.findViewById(R.id.bt_crash).setOnClickListener(v -> {
            TextView textView = null;
            textView.setText("我会崩溃");
        });
    }

    @Override
    public boolean onBackPressed() {
        ViLog.d("onBackPressed");
        //第一个页面不允许退出
        return true;
    }
}
