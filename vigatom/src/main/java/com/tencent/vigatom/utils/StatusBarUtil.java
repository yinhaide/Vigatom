package com.tencent.vigatom.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.tencent.vigatom.R;

/**
 * fitsSystemWindows：
 * 根据官方文档，如果某个View 的fitsSystemWindows 设为true，那么该View的padding属性将由系统设置，
 * 用户在布局文件中设置的padding会被忽略。系统会为该View设置一个paddingTop，值为statusbar的高度。fitsSystemWindows默认为false。
 * 只有将statusbar设为透明，或者界面设为全屏显示时，fitsSystemWindows才会起作用。不然statusbar的空间轮不到用户处理
 *
 * 状态栏的状态资料参考：
 * https://www.jianshu.com/p/e6656707f56c
 */
public class StatusBarUtil {

    //自定义状态栏对应的id
    private static final int FAKE_TRANSLUCENT_VIEW_ID = R.id.vigatom_statusbarutil;
    //ViewMargin偏移的tag
    private static final int TAG_KEY_HAVE_SET_MARGIN = -123;
    //ViewPadding偏移的tag
    private static final int TAG_KEY_HAVE_SET_PADDING = -124;
    //原始的状态栏颜色
    private static int oldStatusBarColor = Color.parseColor("#398EFF");

    /**
     * 设置状态栏颜色
     *
     * @param window 需要设置的window
     * @param color 状态栏颜色值
     * @param isImmersion 是否要沉浸风格，是的话将会隐藏状态栏然后创建自定义的状态栏
     * @param customView 自定义的状态栏
     */
    public static void setStatusBarColor(Window window,int color,boolean isImmersion,View customView) {
        if (window == null) {
            return;
        }
        //每次设置之前先清掉以前的属性
        //clearPreviousSetting(window);
        if (isImmersion) {
            setImmersionBarColor(window, customView, color);
        } else {
            setStatusBarColor(window, color);
        }
    }

    /**
     * 使指定的View向下Padding偏移一个状态栏高度，留出状态栏空间，主要用于设置沉浸式后,页面顶到顶端有突兀感
     *
     * @param context 需要设置的context
     * @param targetView 需要偏移的View
     * @param enable 开启或者关闭
     * @param isPaddingOrMargin 向下偏移是padding还是margin，true的话是padding，false的话是margin
     */
    public static void setOffsetStatusBar(Context context, View targetView,boolean enable,boolean isPaddingOrMargin) {
        if(isPaddingOrMargin){
            setPaddingStatusBar(context,targetView,enable);
        }else{
            setMarginStatusBar(context,targetView,enable);
        }
    }

    /**
     * 设置沉浸式风格的状态栏目(取消状态栏的的占位符)
     *
     * @param window 对应的 window
     * @param color    颜色
     */
    private static void setImmersionBarColor(Window window,View customView, int color) {
        //首先取消状态栏
        showStatusbarForWindow(window,false);
        //4.4
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4
            //加入新的状态栏（不加入队列的话会出现页面偏移的Bug）
            new Handler().post(() -> addStatusBarView(window, color,customView));
        }
    }

    /**
     * 设置透明,隐藏状态栏
     *
     * @param window 需要设置的 window
     */
    private static void showStatusbarForWindow(Window window,boolean show) {
        if(show){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0
                window.setStatusBarColor(oldStatusBarColor);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0
                oldStatusBarColor = window.getStatusBarColor();
                window.setStatusBarColor(Color.TRANSPARENT);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4
                window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }

    /**
     * 使指定的View向下Padding偏移一个状态栏高度，留出状态栏空间，主要用于设置沉浸式后,页面顶到顶端有突兀感
     *
     * @param context      需要设置的context
     * @param targetView    需要偏移的View
     * @param enable        开启或者关闭
     */
    private static void setPaddingStatusBar(Context context, View targetView,boolean enable) {
        if (targetView != null) {
            Object haveSetOffset = targetView.getTag(TAG_KEY_HAVE_SET_PADDING);
            int paddinTop;
            if(enable){
                if (haveSetOffset != null && (Boolean) haveSetOffset) {
                    return;
                }
                paddinTop = targetView.getPaddingTop() + getStatusBarHeight(context);
            }else{
                if (haveSetOffset != null && !(Boolean) haveSetOffset) {
                    return;
                }
                paddinTop = targetView.getPaddingTop() - getStatusBarHeight(context);
            }
            targetView.setPadding(targetView.getPaddingLeft(),paddinTop,targetView.getPaddingRight(),targetView.getPaddingBottom());
            targetView.setTag(TAG_KEY_HAVE_SET_PADDING, enable);
        }
    }

    /**
     * 使指定的View向下Margin偏移一个状态栏高度，留出状态栏空间，主要用于设置沉浸式后,页面顶到顶端有突兀感
     *
     * @param context       需要设置的context
     * @param needOffsetView 需要偏移的View
     */
    private static void setMarginStatusBar(Context context, View needOffsetView,boolean enable) {
        if (needOffsetView != null) {
            Object haveSetOffset = needOffsetView.getTag(TAG_KEY_HAVE_SET_MARGIN);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) needOffsetView.getLayoutParams();
            int marginTop;
            if(enable){
                if (haveSetOffset != null && (Boolean) haveSetOffset) {
                    return;
                }
                marginTop = layoutParams.topMargin + getStatusBarHeight(context);
            }else{
                if (haveSetOffset != null && !(Boolean) haveSetOffset) {
                    return;
                }
                marginTop = layoutParams.topMargin - getStatusBarHeight(context);
            }
            layoutParams.setMargins(layoutParams.leftMargin, marginTop,layoutParams.rightMargin, layoutParams.bottomMargin);
            needOffsetView.setLayoutParams(layoutParams);
            needOffsetView.setTag(TAG_KEY_HAVE_SET_MARGIN, enable);
        }
    }


    /**
     * 设置状态栏颜色
     *
     * @param window 需要设置的window
     * @param color    状态栏颜色值
     */

    private static void setStatusBarColor(Window window, int color) {
        //显示原来的状态栏
        cancelImmersionStyle(window);
        //设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            View fakeStatusBarView = decorView.findViewById(FAKE_TRANSLUCENT_VIEW_ID);
            if (fakeStatusBarView != null) {
                if (fakeStatusBarView.getVisibility() == View.GONE) {
                    fakeStatusBarView.setVisibility(View.VISIBLE);
                }
                fakeStatusBarView.setBackgroundColor(color);
            } else {
                decorView.addView(createStatusBarView(window, color,null));
            }
            setRootView(window);
        }
    }

    /**
     * 取消沉浸式风格的状态栏
     *
     * @param window window
     */
    private static void cancelImmersionStyle(Window window) {
        //显示原来的状态栏
        showStatusbarForWindow(window,true);
        //隐藏自定义的状态
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        ViewGroup contentView = window.findViewById(android.R.id.content);
        View fakeTranslucentView = contentView.findViewById(FAKE_TRANSLUCENT_VIEW_ID);
        if (fakeTranslucentView != null) {
            if (fakeTranslucentView.getVisibility() == View.VISIBLE) {
                fakeTranslucentView.setVisibility(View.GONE);
            }
            decorView.removeView(fakeTranslucentView);
        }
    }

    /**
     * 设置根布局参数
     *
     * @param window 需要设置的 window
     */
    private static void setRootView(Window window) {
        ViewGroup parent = window.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(true);
                ((ViewGroup) childView).setClipToPadding(true);
            }
        }
    }

    /**
     * 添加自定义状态栏条目
     *
     * @param window 需要设置的 window
     * @param color    颜色
     */
    private static void addStatusBarView(Window window, int color,View customView) {
        ViewGroup contentView = window.findViewById(android.R.id.content);
        View fakeTranslucentView = contentView.findViewById(FAKE_TRANSLUCENT_VIEW_ID);
        if (fakeTranslucentView != null) {
            if (fakeTranslucentView.getVisibility() == View.GONE) {
                fakeTranslucentView.setVisibility(View.VISIBLE);
            }
            fakeTranslucentView.setBackgroundColor(color);
        } else {
            View statusBarView = createStatusBarView(window, color,customView);
            contentView.addView(statusBarView);
        }
    }

    /**
     * 创建自定义状态栏条目
     *
     * @param color 颜色
     * @return View
     */
    private static View createStatusBarView(Window window, int color,View customView) {
        // 绘制一个和状态栏一样高的矩形
        View statusBarView;
        if(customView == null){
            statusBarView = new View(window.getContext());
        }else{
            statusBarView = customView;
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(window.getContext()));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(color);
        statusBarView.setId(FAKE_TRANSLUCENT_VIEW_ID);
        return statusBarView;
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    private static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 清除之前的设置
     *
     * @param window 需要设置的 window
     */
    private static void clearPreviousSetting(Window window) {
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        View fakeStatusBarView = decorView.findViewById(FAKE_TRANSLUCENT_VIEW_ID);
        if (fakeStatusBarView != null) {
            decorView.removeView(fakeStatusBarView);
            ViewGroup rootView = (ViewGroup) ((ViewGroup) window.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setPadding(0, 0, 0, 0);
        }
    }
}
