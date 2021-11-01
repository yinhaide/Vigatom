package com.tencent.wx.app.bean;

import com.tencent.vigatom.bean.ViBean;

/**
 * 页面跳转携带数据
 */
public class RouterBean extends ViBean {

    private String fromClass;
    private String msg;

    public RouterBean(String fromClass, String msg) {
        this.fromClass = fromClass;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "RouterBean{" +
                "fromClass='" + fromClass + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
