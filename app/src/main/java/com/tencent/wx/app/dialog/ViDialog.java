package com.tencent.wx.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.haideyin.app.R;
import com.tencent.vigatom.bean.VigatomBean;
import com.tencent.vigatom.helper.VigatomHelper;
import com.tencent.wx.app.utils.SSDAKeyboard;
import com.tencent.wx.app.view.MainView;

/**
 * 演示Dialog
 */
public class ViDialog extends Dialog {

    private VigatomHelper vigatomHelper;

    public ViDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //一个窗口对应一个页面切换逻辑容器，不局限activity，可以用于fragment、viewgroup，dialog，presentation等容器
        vigatomHelper = new VigatomHelper() {
            @Override
            public VigatomBean getVigatomParam() {
                VigatomBean vigatomBean = new VigatomBean();
                vigatomBean.setRootView(findViewById(R.id.viView));     //必须，根容器
                vigatomBean.setViViewClas(MainView.class);              //必须，第一个显示的页面，需要继承ViView抽象类
                vigatomBean.setViKeyboardClass(SSDAKeyboard.class);     //可选，对键盘的支持，可定制，需要继承ViKeyboardImpl
                vigatomBean.setWindow(getWindow());                     //可选，传入页面的窗体
                vigatomBean.setVigatomStatusBus(true);                  //可选，是否设置自定义状态栏，需要存在window才支持
                return vigatomBean;
            }
        };
    }
}
