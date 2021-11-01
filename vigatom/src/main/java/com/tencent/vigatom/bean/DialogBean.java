package com.tencent.vigatom.bean;

import com.tencent.vigatom.callback.CostomCallback;

/**
 * Dialog相关属性
 */
public class DialogBean extends ViBean{

    //是否有取消按钮
    private boolean hasCancel = true;
    //是否有确认按钮
    private boolean hasConfirm = true;
    //是否有标题栏
    private boolean hasTitle = true;
    //当前对话框的显示模式
    private MODE mode = MODE.TEXT;
    //View回调，将相关的View返回
    private CostomCallback confirmCallback;
    //View回调，将相关的View返回
    private CostomCallback cancelCallback;
    //标题内容
    private String title;
    //确认框内容
    private String confirm;
    //取消框内容
    private String cancel;

    public enum MODE {
        TEXT("显示内容"),//显示一行字
        EDIT("请输入密码"),//显示Hint提示内容
        CUSTOM(null); //自定义View对象

        MODE(Object object){
            this.arg = object;
        }

        private Object arg;

        public Object getObj() {
            return this.arg;
        }

        public MODE setObj(Object obj) {
            this.arg = obj;
            return this;
        }
    }

    public DialogBean() {

    }

    public boolean isHasCancel() {
        return hasCancel;
    }

    public void setHasCancel(boolean hasCancel) {
        this.hasCancel = hasCancel;
    }

    public boolean isHasConfirm() {
        return hasConfirm;
    }

    public void setHasConfirm(boolean hasConfirm) {
        this.hasConfirm = hasConfirm;
    }

    public boolean isHasTitle() {
        return hasTitle;
    }

    public void setHasTitle(boolean hasTitle) {
        this.hasTitle = hasTitle;
    }

    public MODE getMode() {
        return mode;
    }

    public void setMode(MODE mode) {
        this.mode = mode;
    }

    public CostomCallback getConfirmCallback() {
        return confirmCallback;
    }

    public void setConfirmCallback(CostomCallback confirmCallback) {
        this.confirmCallback = confirmCallback;
    }

    public CostomCallback getCancelCallback() {
        return cancelCallback;
    }

    public void setCancelCallback(CostomCallback cancelCallback) {
        this.cancelCallback = cancelCallback;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public String getCancel() {
        return cancel;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

    @Override
    public String toString() {
        return "DialogBean{" +
                "hasCancel=" + hasCancel +
                ", hasConfirm=" + hasConfirm +
                ", hasTitle=" + hasTitle +
                ", mode=" + mode +
                ", confirmCallback=" + confirmCallback +
                ", cancelCallback=" + cancelCallback +
                ", title='" + title + '\'' +
                ", confirm='" + confirm + '\'' +
                ", cancel='" + cancel + '\'' +
                '}';
    }
}
