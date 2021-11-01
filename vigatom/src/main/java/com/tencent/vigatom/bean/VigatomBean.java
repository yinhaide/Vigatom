package com.tencent.vigatom.bean;

import android.view.ViewGroup;
import android.view.Window;

public class VigatomBean extends ViewBean{

    //根布局
    private ViewGroup rootView;
    //第一个显示的页面
    private Class viViewClas;
    //键盘的引擎
    private Class viKeyboardClass;
    //当前根布局依附的窗口
    private Window window;
    //是否使用框架内部的状态栏：沉浸式、全屏、自定义图标
    private boolean vigatomStatusBus;

    public ViewGroup getRootView() {
        return rootView;
    }

    public void setRootView(ViewGroup rootView) {
        this.rootView = rootView;
    }

    public Class getViViewClas() {
        return viViewClas;
    }

    public void setViViewClas(Class viViewClas) {
        this.viViewClas = viViewClas;
    }

    public Class getViKeyboardClass() {
        return viKeyboardClass;
    }

    public void setViKeyboardClass(Class viKeyboardClass) {
        this.viKeyboardClass = viKeyboardClass;
    }

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public boolean isVigatomStatusBus() {
        return vigatomStatusBus;
    }

    public void setVigatomStatusBus(boolean vigatomStatusBus) {
        this.vigatomStatusBus = vigatomStatusBus;
    }
}
