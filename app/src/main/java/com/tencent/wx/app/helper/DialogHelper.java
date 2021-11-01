package com.tencent.wx.app.helper;


import android.content.Context;
import com.tencent.wx.app.dialog.ViDialog;

/**
 * Dialog相关的单例
 */
public class DialogHelper {

    private static final String TAG = DialogHelper.class.getSimpleName();

    private static volatile DialogHelper instance;

    private DialogHelper() {
    }

    public static DialogHelper getInstance() {
        if (instance == null) {
            synchronized (DialogHelper.class) {
                if (instance == null) {
                    instance = new DialogHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 显示
     */
    public void show(Context context) {
//        AlertDialog.Builder viDialog = new AlertDialog.Builder(context);
//        View mView = View.inflate(context, R.layout.activity_main, null);
//        viDialog.setTitle("我是一个自定义Dialog");
//        viDialog.setView(mView);
//        viDialog.setPositiveButton("确定", (dialog, which) -> {
//
//        });
//        vigatomHelper = new VigatomHelper() {
//            @Override
//            public VigatomBean getVigatomParam() {
//                VigatomBean vigatomBean = new VigatomBean();
//                vigatomBean.setRootView(mView.findViewById(R.id.viView));     //必须，跟容器layoutid
//                vigatomBean.setViViewClas(MainView.class);              //必须，第一个显示的页面，需要继承ViView抽象类
//                vigatomBean.setViKeyboardClass(SSDAKeyboard.class);     //可选，对键盘的支持，可定制，需要继承ViKeyboardImpl
//                //vigatomBean.setWindow(getWindow());                     //可选，传入页面的窗体
//                vigatomBean.setVigatomStatusBus(true);                  //可选，是否设置自定义状态栏，需要存在window才支持
//                return vigatomBean;
//            }
//        };
//        viDialog.show();
        new ViDialog(context).show();
    }
}
