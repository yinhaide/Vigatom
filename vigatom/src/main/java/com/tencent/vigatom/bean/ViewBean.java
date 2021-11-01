package com.tencent.vigatom.bean;

/**
 * 页面跳转的参数集合
 */
public class ViewBean extends ViBean{

    //跳转是否要销毁源页面
    private boolean originDestroy;
    //跳转的时候是否要刷新目标：存在先销毁重建
    private boolean targetRefresh;
    //是否清除目标View顶端的View，类似singleTop
    private boolean clearTop;
    //跳转数据携带
    private Object object;

    public ViewBean() {
    }

    public ViewBean(boolean originDestroy) {
        this.originDestroy = originDestroy;
    }

    public ViewBean(boolean originDestroy, boolean targetRefresh) {
        this.originDestroy = originDestroy;
        this.targetRefresh = targetRefresh;
    }

    public ViewBean(boolean originDestroy, boolean targetRefresh, boolean clearTop) {
        this.originDestroy = originDestroy;
        this.targetRefresh = targetRefresh;
        this.clearTop = clearTop;
    }

    public ViewBean(boolean originDestroy, boolean targetRefresh, boolean clearTop, Object object) {
        this.originDestroy = originDestroy;
        this.targetRefresh = targetRefresh;
        this.clearTop = clearTop;
        this.object = object;
    }

    public boolean isOriginDestroy() {
        return originDestroy;
    }

    public void setOriginDestroy(boolean originDestroy) {
        this.originDestroy = originDestroy;
    }

    public boolean isTargetRefresh() {
        return targetRefresh;
    }

    public void setTargetRefresh(boolean targetRefresh) {
        this.targetRefresh = targetRefresh;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public boolean isClearTop() {
        return clearTop;
    }

    public void setClearTop(boolean clearTop) {
        this.clearTop = clearTop;
    }

    @Override
    public String toString() {
        return "ViViewBean{" +
                "originDestroy=" + originDestroy +
                ", targetRefresh=" + targetRefresh +
                ", clearTop=" + clearTop +
                ", object=" + object +
                '}';
    }
}
