package com.tencent.vigatom.utils;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.tencent.vigatom.R;
import com.tencent.vigatom.cons.ViKey;
import com.tencent.vigatom.ue.view.ViView;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ViView的工具类
 */
public class ViViewUtil {

    private static final String TAG = ViViewUtil.class.getSimpleName();

    /**
     * 获取View对象
     *
     * @param rootView 根布局
     * @param targetClass 目标容器的Class
     * @return view
     */
    public static View getView(ViewGroup rootView, Class targetClass) {
        if (rootView != null && targetClass != null) {
            return rootView.findViewWithTag(targetClass.getSimpleName());
        }
        return null;
    }

    /**
     * 获取栈顶的ViView对象
     *
     * @param rootView 根布局
     * @return view对象容器
     */
    public static ViView getTopViView(ViewGroup rootView) {
        if (rootView != null && rootView.getChildCount() >= 1) {
            //从最顶端清除
            View targetView = rootView.getChildAt(rootView.getChildCount() - 1);
            if (targetView != null) {
                return getViView(rootView, targetView);
            }
        }
        return null;
    }

    /**
     * 获取指定Class的ViView对象
     *
     * @param rootView 根布局
     * @param targetViClass 相关的ViViewClass
     * @return view对象容器
     */
    public static ViView getViView(ViewGroup rootView, Class targetViClass) {
        if (rootView != null && targetViClass != null) {
            View targetView = rootView.findViewWithTag(targetViClass.getSimpleName());
            if (targetView != null) {
                return getViView(rootView, targetView);
            }
        }
        return null;
    }

    /**
     * 获取指定View的ViView对象
     *
     * @param rootView 根布局
     * @param targetView 相关的targetView
     * @return view对象容器
     */
    public static ViView getViView(ViewGroup rootView, View targetView) {
        if (rootView != null && targetView != null) {
            return (ViView) targetView.getTag(R.id.vigatom_viview);
        }
        return null;
    }

    /**
     * 删除ViView对应的View
     *
     * @param rootView 根布局
     * @param targetViClass 目标ViViewClass
     */
    public static void removeView(ViewGroup rootView, Class targetViClass) {
        if (rootView != null && targetViClass != null) {
            View targetView = rootView.findViewWithTag(targetViClass.getSimpleName());
            if (targetView != null) {
                rootView.removeView(targetView);
            }
        }
    }

    /**
     * 删除ViView对应的View顶端所有的View
     *
     * @param rootView 根布局
     * @param targetViClass 目标ViViewClass
     */
    public static void clearTopView(ViewGroup rootView, Class targetViClass) {
        if (rootView != null && targetViClass != null) {
            View targetView = rootView.findViewWithTag(targetViClass.getSimpleName());
            if (targetView != null) {
                int index = rootView.indexOfChild(targetView);//总共四个，他在1，要移除2\3
                ViLog.v(TAG + "::indexOfChild:" + index);
                while (rootView.getChildCount() - 1 > index) {
                    ViLog.v(TAG + "::remove:" + (rootView.getChildCount() - 1));
                    rootView.removeViewAt(rootView.getChildCount() - 1);
                }
            }
        }
    }

    /**
     * 用于显示目标ViView的逻辑
     *
     * @param rootView 根布局
     * @param targetViClass 目标ViViewClass
     * @param objet 页面传参
     */
    public static void showViView(ViewGroup rootView, Class targetViClass, Object objet) {
        try {
            //目标View的容器处理
            ViView targetViView = ViViewUtil.getViView(rootView, targetViClass);
            View targetView = ViViewUtil.getView(rootView, targetViClass);
            if (targetView == null || targetViView == null) {
                try {
                    targetViView = (ViView) (targetViClass.newInstance());
                    ViLog.v(TAG + "::创建对象:" + targetViView.getClass().getSimpleName());
                    if (objet != null) {
                        //处理页面传参
                        Bundle bundle = targetViView.getArguments();
                        bundle.putSerializable(ViKey.ARGUMENT_OBJECT_KEY, (Serializable) objet);
                        targetViView.setArguments(bundle);
                    }
                    //新页面要绑定
                    targetViView.bind(rootView);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                //将页面放在栈顶
                targetView.bringToFront();
                //处理页面传参
                Bundle bundle = targetViView.getArguments();
                bundle.remove(ViKey.ARGUMENT_OBJECT_KEY);
                if (objet != null) {
                    //处理页面传参
                    bundle.putSerializable(ViKey.ARGUMENT_OBJECT_KEY, (Serializable) objet);
                    targetViView.setArguments(bundle);
                }
                //旧的页面启动只要处理onNext时间即可
                targetViView.handleArguments();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回所有的路由页面
     *
     * @param rootView 根页面
     * @return 路由栈
     */
    public static List<String> getRouter(ViewGroup rootView) {
        List<String> routerNameLists = new ArrayList<>();
        if (rootView != null) {
            for (int i = 0; i < rootView.getChildCount(); i++) {
                View targetView = rootView.getChildAt(i);
                ViView targetViView = getViView(rootView, targetView);
                if (targetViView != null) {
                    routerNameLists.add(targetViView.getClass().getSimpleName());
                }
            }
        }
        return routerNameLists;
    }
}