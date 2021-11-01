package com.tencent.vigatom.ue.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tencent.vigatom.R;
import com.tencent.vigatom.Vigatom;
import com.tencent.vigatom.helper.LeakHelper;
import com.tencent.vigatom.helper.TimeoutHelper;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义状态栏
 */
public class ViStatusBarView extends ViView {

    private TextView timeTv;
    //网络信号
    private ImageView mobileIv, wifiIv, ethernetIv;
    //键盘显示
    private ImageView keyboardIv;
    //容器
    //窗口的原始颜色
    private int oldWindowColor = Color.parseColor("#398EFF");
    //自定义状态栏对应的id
    private static final int FAKE_TRANSLUCENT_VIEW_ID = R.id.vigatom_statusbarutil;
    //当前的窗口
    private Window window;

    //定时器
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            String dateString = simpleDateFormat.format(new Date()); //将给定的 Date 格式化为日期/时间字符串
            Vigatom.getInstance().runUIThread(() -> timeTv.setText(dateString));
            TimeoutHelper.getInstance().putTask(runnable, 1000);
        }
    };

    public ViStatusBarView(Window window) {
        this.window = window;
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            oldWindowColor = window.getStatusBarColor();
        }
        view = LayoutInflater.from(window.getContext()).inflate(onInflateLayout(), null);
        timeTv = view.findViewById(R.id.time_tv);
        mobileIv = view.findViewById(R.id.mobile_iv);
        wifiIv = view.findViewById(R.id.wifi_iv);
        ethernetIv = view.findViewById(R.id.ethernet_iv);
        keyboardIv = view.findViewById(R.id.keyboard_iv);
        //添加生命周期
        view.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                //白色主题
                setWhiteStyle();
                //启动时间显示
                TimeoutHelper.getInstance().putTask(runnable, 0);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                TimeoutHelper.getInstance().removeTask(runnable);
                //监控内存泄漏
                LeakHelper.getInstance().monitorView(v);
            }
        });
    }

    @Override
    public int onInflateLayout() {
        // 将Xml中定义的布局解析出来。
        return R.layout.vigatom_widget_statusbar;
    }

    /**
     * 还原系统状态栏风格
     */
    public void setSystemStyle() {
        //显示状态栏
        showSystemStatusbar(window, true);
        //清除自定义状态栏
        removeCustomStatusBarView(window);
        //取消全屏显示
        setFullScreen(window, false);
    }

    /**
     * 还原系统状态栏风格
     */
    public void setVigatomStyle() {
        //去掉状态栏
        showSystemStatusbar(window, false);
        //将状态栏添加到系统
        addCustomStatusBarView(window, view);
        //全屏显示
        setFullScreen(window, true);
    }

    /**
     * 设置白色图标和文字风格
     */
    public void setWhiteStyle() {
        if (!isAttach()) {
            return;
        }
        timeTv.setTextColor(Color.WHITE);
        mobileIv.setImageResource(R.drawable.vigatom_mobile_w_selector);
        wifiIv.setImageResource(R.drawable.vigatom_wifi_w_selector);
        ethernetIv.setImageResource(R.drawable.vigatom_ethernet_w_selector);
        keyboardIv.setImageResource(R.drawable.vigatom_keyboard_w);
    }

    /**
     * 设置黑色图标和文字风格
     */
    public void setBlackStyle() {
        if (!isAttach()) {
            return;
        }
        timeTv.setTextColor(view.getContext().getResources().getColor(R.color.vigatom_viblack));
        mobileIv.setImageResource(R.drawable.vigatom_mobile_b_selector);
        wifiIv.setImageResource(R.drawable.vigatom_wifi_b_selector);
        ethernetIv.setImageResource(R.drawable.vigatom_ethernet_b_selector);
        keyboardIv.setImageResource(R.drawable.vigatom_keyboard_b);
    }

    /**
     * 设置背景色
     *
     * @param color 背景色
     */
    public void setBackgroundColor(int color) {
        if (!isAttach()) {
            return;
        }
        //设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0
            oldWindowColor = color;
            window.setStatusBarColor(color);
        }
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        View fakeStatusBarView = decorView.findViewById(FAKE_TRANSLUCENT_VIEW_ID);
        if (fakeStatusBarView != null) {
            fakeStatusBarView.setBackgroundColor(color);
        }
    }

    /**
     * 设置背景色的透明度
     *
     * @param alpha 透明度 0-1f
     */
    public void setBackgroundAlpha(float alpha) {
        //设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0
            int alphaOldWindowColor = getColorWithAlpha(alpha, window.getStatusBarColor());
            window.setStatusBarColor(alphaOldWindowColor);
        }
        ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
        //如果背景透明返回空
        if (colorDrawable != null) {
            int alphaCustomColor = getColorWithAlpha(alpha, colorDrawable.getColor());
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            View fakeStatusBarView = decorView.findViewById(FAKE_TRANSLUCENT_VIEW_ID);
            if (fakeStatusBarView != null) {
                fakeStatusBarView.setBackgroundColor(alphaCustomColor);
            }
        }
    }

    /**
     * 设置移动网络信号图标状态
     *
     * @param show 是否要显示
     * @param on 是否在线
     */
    public void setMobileStatus(boolean show, boolean on) {
        if (!isAttach()) {
            return;
        }
        if (show) {
            mobileIv.setVisibility(View.VISIBLE);
            mobileIv.setEnabled(on);
        } else {
            mobileIv.setVisibility(View.GONE);
        }
    }

    /**
     * 设置Wifi信号图标状态
     *
     * @param show 是否要显示
     * @param on 是否在线
     */
    public void setWifiStatus(boolean show, boolean on) {
        if (!isAttach()) {
            return;
        }
        if (show) {
            wifiIv.setVisibility(View.VISIBLE);
            wifiIv.setEnabled(on);
        } else {
            wifiIv.setVisibility(View.GONE);
        }
    }

    /**
     * 设置以太网信号图标状态
     *
     * @param show 是否要显示
     * @param on 是否在线
     */
    public void setEthernetStatus(boolean show, boolean on) {
        if (!isAttach()) {
            return;
        }
        if (show) {
            ethernetIv.setVisibility(View.VISIBLE);
            ethernetIv.setEnabled(on);
        } else {
            ethernetIv.setVisibility(View.GONE);
        }
    }

    /**
     * 设置外接键盘图标状态
     *
     * @param hasKeyboard 是否存在
     */
    public void setHasKeyboard(boolean hasKeyboard) {
        if (!isAttach()) {
            return;
        }
        if (hasKeyboard) {
            keyboardIv.setVisibility(View.VISIBLE);
        } else {
            keyboardIv.setVisibility(View.GONE);
        }
    }

    /**
     * 设置时候全屏展示
     *
     * @param enable 是否全屏
     */
    public void setFullScreen(Window window, boolean enable) {
        if (enable) {
            //全屏模式
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * 是否已经附着到了父类的View
     *
     * @return 结果
     */
    private boolean isAttach() {
        if (view != null) {
            return view.isAttachedToWindow();
        }
        return false;
    }

    /**
     * 添加自定义状态栏到根目录
     *
     * @param window 需要设置的 window
     * @param customView 自定义状态栏
     */
    private void addCustomStatusBarView(Window window, View customView) {
        ViewGroup contentView = window.findViewById(android.R.id.content);
        View fakeTranslucentView = contentView.findViewById(FAKE_TRANSLUCENT_VIEW_ID);
        //存在的情况下让它显示
        if (fakeTranslucentView != null) {
            if (fakeTranslucentView.getVisibility() == View.GONE) {
                fakeTranslucentView.setVisibility(View.VISIBLE);
            }
        } else {
            // 不存在的话创建绘制一个和状态栏一样高的矩形
            if (customView == null) {
                customView = new View(window.getContext());
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStatusBarHeight(window.getContext()));
            customView.setLayoutParams(params);
            customView.setId(FAKE_TRANSLUCENT_VIEW_ID);
            contentView.addView(customView);
        }
    }

    /**
     * 取消沉浸式风格的状态栏
     *
     * @param window window
     */
    private void removeCustomStatusBarView(Window window) {
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
     * 设置透明,隐藏状态栏
     *
     * @param window 需要设置的 window
     */
    private void showSystemStatusbar(Window window, boolean show) {
        if (show) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0
                window.setStatusBarColor(oldWindowColor);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0
                window.setStatusBarColor(Color.TRANSPARENT);
                window.getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4
                window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }

    /**
     * 给color添加透明度
     *
     * @param alpha 透明度 0f～1f
     * @param baseColor 基本颜色
     * @return 组装的颜色
     */
    private int getColorWithAlpha(float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        return a + rgb;
    }
}
