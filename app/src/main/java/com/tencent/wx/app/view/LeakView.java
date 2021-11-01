package com.tencent.wx.app.view;

import android.view.View;
import com.haideyin.app.R;
import com.tencent.vigatom.Vigatom;
import com.tencent.vigatom.helper.LeakHelper.OnLeaksListener;
import com.tencent.vigatom.ue.view.ViView;
import com.tencent.vigatom.utils.ScreenUtil;

/**
 * 内存泄漏的页面
 */
public class LeakView extends ViView {

    //卡顿回调
    private OnLeaksListener onLeaksListener = (cosTime, hashMap) -> {
        //注意，因为吐司消失一样会触发发起内存泄漏检测，所以会无限弹出泄漏信息，可以杀掉应用
        if (hashMap.size() > 0) {
            String msg = "内存泄漏检测耗时：" + cosTime + "\n" + "泄露对象：" + hashMap.toString();
            toast(msg, 5000);
        }
    };

    @Override
    public int onInflateLayout() {
        return ScreenUtil.isLanscape(Vigatom.getAppContext()) ? R.layout.view_leak_l
                : R.layout.view_leak_p;
    }

    @Override
    public void onCreate(View inflateView) {
        Vigatom.getInstance().enableLeak(true, 5000, onLeaksListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //故意不释放资源，产生内存泄漏
        //onLeaksListener == null;
    }
}
