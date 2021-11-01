package com.tencent.wx.app.dialog;

import android.app.Presentation;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import com.haideyin.app.R;
import com.tencent.vigatom.bean.VigatomBean;
import com.tencent.vigatom.helper.VigatomHelper;
import com.tencent.wx.app.utils.SSDAKeyboard;
import com.tencent.wx.app.view.Router1View;

/**
 * 副屏相关
 */
public class ViPresentation extends Presentation {

    public ViPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getWindow() != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        setContentView(R.layout.activity_main);
        //一个窗口对应一个页面切换逻辑容器，不局限activity，可以用于fragment、viewgroup，dialog，presentation等容器
        new VigatomHelper() {
            @Override
            public VigatomBean getVigatomParam() {
                VigatomBean vigatomBean = new VigatomBean();
                vigatomBean.setRootView(findViewById(R.id.viView));     //必须，根容器
                vigatomBean.setViViewClas(Router1View.class);           //必须，第一个显示的页面，需要继承ViView抽象类
                vigatomBean.setViKeyboardClass(SSDAKeyboard.class);     //可选，对键盘的支持，可定制，需要继承ViKeyboardImpl
                vigatomBean.setWindow(getWindow());                     //可选，传入页面的窗体
                vigatomBean.setVigatomStatusBus(true);                  //可选，是否设置自定义状态栏，需要存在window才支持
                return vigatomBean;
            }
        };
    }
}
