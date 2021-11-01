package com.tencent.wx.app.activity;

import android.app.Activity;
import android.os.Bundle;
import com.haideyin.app.R;
import com.tencent.vigatom.bean.VigatomBean;
import com.tencent.vigatom.helper.VigatomHelper;
import com.tencent.wx.app.utils.SSDAKeyboard;
import com.tencent.wx.app.view.MainView;

public class MainActivity extends Activity {

    private static String TAG = MainActivity.class.getSimpleName();
    private VigatomHelper vigatomHelper;

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

    @Override
    public void onBackPressed() {
        //给容器内部传递回退事件
        if (!vigatomHelper.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //容器资源销毁
        vigatomHelper.onDestroy();
    }
}
