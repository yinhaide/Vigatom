package com.tencent.wx.app.view;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import com.haideyin.app.R;
import com.tencent.vigatom.Vigatom;
import com.tencent.vigatom.bean.FluentBean;
import com.tencent.vigatom.helper.FluentHelper.OnFluentListener;
import com.tencent.vigatom.ue.view.ViView;
import com.tencent.vigatom.utils.ScreenUtil;

/**
 * 卡顿的页面
 */
public class FluentView extends ViView {

    private TextView contentTv;
    //卡顿回调
    private OnFluentListener onFluentListener = new OnFluentListener() {
        @Override
        public void onFluent(long cosTime, FluentBean fluentBean) {
            String msg = "开始检测时间：" + fluentBean.getBeginTime() + " 栈读取时间：" + fluentBean.getGetStackTime()
                    + " UI主线程耗时:" + cosTime + " 栈轨迹:"
                    + fluentBean.getStackMsg();
            contentTv.setText(msg);
        }
    };

    @Override
    public int onInflateLayout() {
        return ScreenUtil.isLanscape(Vigatom.getAppContext()) ? R.layout.view_fluent_l
                : R.layout.view_fluent_p;
    }

    @Override
    public void onCreate(View inflateView) {
        contentTv = inflateView.findViewById(R.id.content_tv);
        //支持点击滑动
        contentTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        Vigatom.getInstance().enableFluent(true, 400, onFluentListener);
        //开始产生卡顿
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onFluentListener = null;
    }
}
