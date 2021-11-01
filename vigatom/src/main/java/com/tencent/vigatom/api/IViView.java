package com.tencent.vigatom.api;

import android.view.View;

/**
 * ViView的生命周期
 */
public interface IViView {

    /**
     * 页面创建完成
     * @param inflateView 初始化完成的View
     */
    void onCreate(View inflateView);

    /**
     * 开始处理业务逻辑,不管有没有数据都会回调(页面跳转携带数据),页面切换会多次调用，不能把初始化工作放在这里
     *
     * @param object 跳转传递的对象，需要实现Serializable序列化
     */
    void onNexts(Object object);

    /**
     * 页面销毁
     */
    void onDestroy();

    /**
     * Key event
     * @param iViKeyboard ikey
     * @return iner handle
     */
    boolean onKey(IViKeyboard iViKeyboard);

    /**
     * 是否要自己处理回退事件，会影响到包括导航和按键返回
     *
     * @return 是否要自己处理
     */
    boolean onBackPressed();
}
