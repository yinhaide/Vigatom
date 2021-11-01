package com.tencent.vigatom.helper;

import android.os.Build.VERSION_CODES;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.tencent.vigatom.R;
import com.tencent.vigatom.api.IViKeyboard;
import com.tencent.vigatom.api.IViView;
import com.tencent.vigatom.bean.VigatomBean;
import com.tencent.vigatom.ue.view.ViStatusBarView;
import com.tencent.vigatom.ue.view.ViView;
import com.tencent.vigatom.utils.ViViewUtil;

/**
 * 每一个页面的容器，可以用于Activity、Fragment、Dialog等页面窗体
 */
public abstract class VigatomHelper implements IViView {

    private static final String TAG = VigatomHelper.class.getSimpleName();
    //容器
    private ViewGroup rootView;

    /**
     * 构造函数
     */
    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    public VigatomHelper() {
        try {
            //检查参数是否合法
            if (getVigatomParam().getRootView() == null) {
                throw new RuntimeException("container is empty");
            }
            if (getVigatomParam().getViViewClas() == null) {
                throw new RuntimeException("mainview is empty");
            }
            if (getVigatomParam().getRootView() == null) {
                throw new RuntimeException("rootView is empty");
            }
            this.rootView = getVigatomParam().getRootView();
            //window处理
            Window window = getVigatomParam().getWindow();
            if (window != null) {
                //缓存window对象
                this.rootView.setTag(R.id.vigatom_window, window);
                ViStatusBarView viStatusBarView = new ViStatusBarView(window);
                this.rootView.setTag(R.id.vigatom_statusbar, viStatusBarView);
                if (getVigatomParam().isVigatomStatusBus()) {
                    //自定义状态栏：沉浸、全屏，自定义图标
                    viStatusBarView.setVigatomStyle();
                }
            }
            //检查参数是否合法
            if (!ViView.class.isAssignableFrom(getVigatomParam().getViViewClas())) {
                throw new RuntimeException("注册的ViView非法：" + getVigatomParam().getViViewClas().getSimpleName());
            }
            //检查参数是否合法
            if (getVigatomParam().getViKeyboardClass() != null) {
                if (!IViKeyboard.class.isAssignableFrom(getVigatomParam().getViKeyboardClass())) {
                    throw new RuntimeException("注册的ViKeyboard非法："+ getVigatomParam().getViKeyboardClass().getSimpleName());
                }else{
                    //存下相关的自定义能力
                    rootView.setTag(R.id.vigatom_keyboard_class,getVigatomParam().getViKeyboardClass());
                }
            }
            //必须要获取焦点才能拿到按键事件
            this.rootView.setFocusable(true);
            //按键事件处理
            this.rootView.setOnKeyListener((v, keyCode, event) -> {
                ViView topViView = ViViewUtil.getTopViView(rootView);
                //给最顶端的View分发按键事件
                if(topViView != null){
                    return topViView.handleKeyEvent(keyCode,event);
                }
                //只是内部消费，不阻断事件回传
                return false;
            });
            //初始化参数
            ViView mainView = (ViView)(getVigatomParam().getViViewClas().newInstance());
            //创建对象后需要执行绑定操作
            mainView.bind(rootView);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }



    /* ************************************************************* */
    /*                           生命周期传递
    /* ************************************************************* */

    @Override
    public boolean onBackPressed() {//返回虚拟按键
        //允许最后退到空白页面，需要自己处理最后个页面的回退逻辑
        if (rootView != null && rootView.getChildCount() > 0) {
            ViView topViView = ViViewUtil.getTopViView(rootView);
            if (topViView != null) {
                //内部不处理的话就统一外部退出处理
                if (!topViView.onBackPressed()) {
                    topViView.finish();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onCreate(View inflateView) {

    }

    @Override
    public void onNexts(Object object) {

    }

    @Override
    public boolean onKey(IViKeyboard iViKeyboard) {
        return false;
    }

    @Override
    public void onDestroy() {
        //释放吐司相关的资源
        ToastHelper.getInstance().dismiss(rootView);
    }

    /* ************************  param  ************************ */

    /**
     * param
     * @return param
     */
    public abstract VigatomBean getVigatomParam();
}
