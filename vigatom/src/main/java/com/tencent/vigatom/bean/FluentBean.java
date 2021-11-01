package com.tencent.vigatom.bean;

/**
 * 卡顿的信息
 */
public class FluentBean extends ViBean {

    //开始检查的时间
    private long beginTime;
    //读取到栈的时间
    private long getStackTime;
    //栈信息
    private String stackMsg;

    public FluentBean(long beginTime, long getStackTime, String stackMsg) {
        this.beginTime = beginTime;
        this.getStackTime = getStackTime;
        this.stackMsg = stackMsg;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getGetStackTime() {
        return getStackTime;
    }

    public void setGetStackTime(long getStackTime) {
        this.getStackTime = getStackTime;
    }

    public String getStackMsg() {
        return stackMsg;
    }

    public void setStackMsg(String stackMsg) {
        this.stackMsg = stackMsg;
    }

    @Override
    public String toString() {
        return "FluentBean{" +
                "beginTime=" + beginTime +
                ", getStackTime=" + getStackTime +
                ", stackmsg='" + stackMsg + '\'' +
                '}';
    }
}
