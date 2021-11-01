package com.tencent.vigatom.ue.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import com.tencent.vigatom.R;
import com.tencent.vigatom.api.IViKeyboard;
import com.tencent.vigatom.api.IViView;
import com.tencent.vigatom.bean.DialogBean;
import com.tencent.vigatom.bean.DialogBean.MODE;
import com.tencent.vigatom.bean.ViewBean;
import com.tencent.vigatom.callback.CostomCallback;
import com.tencent.vigatom.cons.ViKey;
import com.tencent.vigatom.helper.DialogHelper;
import com.tencent.vigatom.helper.LeakHelper;
import com.tencent.vigatom.helper.LoadingHelper;
import com.tencent.vigatom.helper.ToastHelper;
import com.tencent.vigatom.impl.ViKeyboardImpl;
import com.tencent.vigatom.ue.widget.ViEditTextWidget;
import com.tencent.vigatom.utils.ViLog;
import com.tencent.vigatom.utils.ViViewUtil;
import java.io.Serializable;

public abstract class ViView implements IViView {

    private final static String TAG = ViView.class.getSimpleName();
    //根容器
    protected ViewGroup rootView;
    //当前的View
    protected View view;
    //页面通信
    private Bundle arguments;
    //IkeyBoard
    private Class iViKeyBoard;
    //Window属性
    private Window window;
    //状态栏
    private ViStatusBarView viStatusBarView;

    public ViView() {

    }

    /**
     * 开始绑定
     */
    @SuppressLint("ClickableViewAccessibility")
    public View bind(ViewGroup fatherView) {
        this.rootView = fatherView;
        //状态栏
        viStatusBarView = (ViStatusBarView) this.rootView.getTag(R.id.vigatom_statusbar);
        //Window
        window = (Window) this.rootView.getTag(R.id.vigatom_window);
        //IKeyboard
        Object object = fatherView.getTag(R.id.vigatom_keyboard_class);
        if (object != null) {
            iViKeyBoard = (Class) object;
        } else {
            iViKeyBoard = ViKeyboardImpl.class;
        }
        view = rootView.findViewWithTag(getClass().getSimpleName());
        //是否要添加view
        boolean needAddView = (view == null);
        if(view == null){
            view = LayoutInflater.from(fatherView.getContext()).inflate(onInflateLayout(), null);
        }
        //绑定Key
        view.setTag(getClass().getSimpleName());
        //绑定容器
        view.setTag(R.id.vigatom_viview,this);
        //添加生命周期
        view.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                onCreate(view);
                ViLog.v(TAG+"::onViewAttachedToWindow-v:"+v.getClass().getSimpleName());
                //页面通信数据
                handleArguments();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                ViLog.v(TAG + "::onViewDetachedFromWindow-v:" + v.getClass().getSimpleName());
                //内存泄漏检测
                LeakHelper.getInstance().monitorView(v);
                onDestroy();
            }
        });
        //防止页面重叠导致点击事件穿透，保证顶端的View才有焦点
        view.setOnTouchListener((v, event) -> true);
        //最后再添加，保证上面的回调正常执行
        if (needAddView) {
            this.rootView.addView(view);
        }
        return view;
    }

    /**
     * 处理按键事件
     * @param keyCode 按键值
     * @param keyEvent 按键事件
     */
    public boolean handleKeyEvent(int keyCode, KeyEvent keyEvent){
        ViLog.v(TAG+"::"+getClass().getSimpleName()+"-OnKeyListener-keyEvent:"+keyEvent.toString());
        try {
            IViKeyboard keyboard = (IViKeyboard) (iViKeyBoard.newInstance());
            keyboard.setKeyCode(keyCode);
            keyboard.setKeyEvent(keyEvent);
            return onKey(keyboard);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 开始处理业务逻辑
     */
    public void handleArguments(){
        Serializable object = null;
        if (arguments != null) {
            object = arguments.getSerializable(ViKey.ARGUMENT_OBJECT_KEY);
        }
        onNexts(object);
    }

    /* ************************************************************* */
    /*                     暴露到外面的方法
    /* ************************************************************* */

    /**
     * 获取当前的上下文
     *
     * @return 上下文
     */
    public Context getContext() {
        return rootView.getContext();
    }

    /**
     * 获取当前的View
     *
     * @return view
     */
    public View getView() {
        return view;
    }

    /**
     * 获取当前的View附着的Window，可以为null
     *
     * @return window
     */
    public Window getWindow() {
        return window;
    }

    /**
     * 获取当前的状态栏，可能为null
     *
     * @return window
     */
    public ViStatusBarView getViStatusBarView() {
        return viStatusBarView;
    }

    /**
     * 结束本次页面，自动回到上次的页面
     */
    public void finish() {
        //先删除栈顶的View
        ViViewUtil.removeView(rootView, getClass());
        //然后启动栈顶下面第二个页面
        ViView targetViView = ViViewUtil.getTopViView(rootView);
        if (targetViView != null) {
            startView(targetViView.getClass());
        } else {
            //允许最后退到空白页面，需要自己处理最后个页面的回退逻辑
        }
    }

    /**
     * 显示Dialog
     *
     * @param tips 内容
     */
    public void showDialog(String tips) {
        showDialog(tips, null);
    }

    /**
     * 显示Dialog
     *
     * @param dialogBean 定制参数
     */
    public void showDialog(String content, DialogBean dialogBean) {
        if (dialogBean == null) {
            dialogBean = new DialogBean();
        }
        dialogBean.setMode(MODE.TEXT.setObj(content));
        DialogHelper.getInstance().show(rootView, dialogBean);
    }

    /**
     * 显示Dialog
     *
     * @param hint 内容
     */
    public void showEditDialog(String hint, CostomCallback<ViEditTextWidget> customCallback) {
        DialogHelper.getInstance().showEdit(rootView, hint, customCallback);
    }

    /**
     * 高级自定义级别显示Dialog
     *
     * @param dialogBean 定制参数
     */
    public void showCustomDialog(View view, DialogBean dialogBean) {
        if (dialogBean == null) {
            dialogBean = new DialogBean();
        }
        dialogBean.setMode(MODE.CUSTOM.setObj(view));
        DialogHelper.getInstance().show(rootView, dialogBean);
    }

    /**
     * 隐藏Dialog
     */
    public void hideDialog() {
        DialogHelper.getInstance().dismiss(rootView);
    }

    /**
     * 显示Loading
     *
     * @param tips 内容
     */
    public void showLoading(String tips) {
        LoadingHelper.getInstance().showLoading(rootView, tips);
    }

    /**
     * 显示Loading
     *
     * @param tips 内容
     */
    public void showLoadingPercent(String tips, int percent) {
        LoadingHelper.getInstance().showLoadingPercent(rootView, tips, percent);
    }

    /**
     * 隐藏Loading
     */
    public void hideLoading() {
        LoadingHelper.getInstance().dismiss(rootView);
    }

    /**
     * 默认页面跳转，会清保留本页面，跳转到目标页面(如果目标页面存在直接拉到最前面显示)
     * @param target 跳转
     */
    public void startView(Class target){
        startView(target,new ViewBean());
    }

    /**
     * 页面跳转
     *
     * @param target 跳转
     * @param viewBean 跳转参数，其中的object需要序列化
     */
    public void startView(@NonNull Class target, @NonNull ViewBean viewBean) {
        ViLog.v(TAG + "::viewGroup.getChildCount():" + rootView.getChildCount());
        //清除栈顶
        if (viewBean.isClearTop()) {
            ViViewUtil.clearTopView(rootView, target);
        }
        //目标刷新
        if (viewBean.isTargetRefresh()) {
            ViViewUtil.removeView(rootView, target);
        }
        //销毁源页面
        if (viewBean.isOriginDestroy()) {
            ViViewUtil.removeView(rootView, getClass());
        }
        //目标目标跳转
        ViViewUtil.showViView(rootView, target, viewBean.getObject());
    }

    /* ************************************************************* */
    /*                     页面通信的数据
    /* ************************************************************* */

    /**
     * 获取页面通信数据
     *
     * @return 数据
     */
    public Bundle getArguments() {
        if (arguments == null) {
            return new Bundle();
        }
        return arguments;
    }

    /**
     * 设置页面通信数据
     *
     * @param mArguments 数据
     */
    public void setArguments(Bundle mArguments) {
        this.arguments = mArguments;
    }


    /* ************************************************************* */
    /*                          吐司相关
    /* ************************************************************* */

    /**
     * 吐司，默认3秒消失
     *
     * @param tip 吐司内容
     */
    public void toast(String tip) {
        toast(tip, ToastHelper.DEFAULT_MILTIME);
    }

    /**
     * 字符串加显示时间的吐司
     */
    public void toast(String tip, int duration) {
        toast(tip, -1, duration, false);
    }

    /**
     * 带字符串、图片、显示时间的吐司
     */
    public void toast(String tip, int imgRes, int duration) {
        toast(tip, imgRes, duration, false);
    }


    /**
     * 居中显示吐司，Pop风格，为永不消失
     */
    public void toastPop(String tip) {
        toast(tip, -1, -1, true);
    }

    /**
     * 字符串加显示时间的吐司
     */
    public void toast(String tip, int imgRes, int duration, boolean isCenter) {
        ToastHelper.getInstance().show(rootView, tip, imgRes, duration, isCenter);
    }

    /* ************************************************************* */
    /*                     供子类重写的的生命周期
    /* ************************************************************* */

    @Override
    public void onCreate(View inflateView) {

    }

    @Override
    public void onNexts(Object object) {

    }

    @Override
    public boolean onKey(IViKeyboard iViKeyBoard) {
        //支持取消按键返回
        if (iViKeyBoard.isKeyUp() && iViKeyBoard.isCancel()) {
            //是否允许导航或者按键退出
            if (!onBackPressed()) {
                finish();
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        //内存泄漏检测
        LeakHelper.getInstance().monitorObject(this);
    }

    public boolean onBackPressed() {
        return false;
    }

    /* ************************************************************* */
    /*                     供子类继承的抽象方法
    /* ************************************************************* */

    /**
     * 传递页面Layout
     *
     * @return the layoutID
     */
    public abstract int onInflateLayout();
}
