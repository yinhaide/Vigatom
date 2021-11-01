package com.tencent.vigatom.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * 控制View的可视化帮助类,用法
 * ViewAnimUtil.showFade(fatherView, 0f,1f,100);
 * ViewAnimUtil.showFromBottom(childView,200);
 * ViewAnimUtil.hideToBottom(childView, 200);
 * ViewAnimUtil.hideFade(fatherView, 1f,0f,100);
 */
public class ViewAnimUtil {

    private static final String TAG = ViewAnimUtil.class.getSimpleName();

    /**
     * 从底下弹出
     *
     * @param view      the view
     * @param duraction 动画持续时间
     */
    public static void showFromBottom(View view, int duraction ,Animation.AnimationListener animationListener) {
        if (view != null) {
            view.clearAnimation();
            view.setEnabled(true);
            TranslateAnimation showAnim = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f);
            showAnim.setDuration(duraction);
            showAnim.setAnimationListener(animationListener);
            view.startAnimation(showAnim);
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 到底下消失
     *
     * @param view      the view
     * @param duraction 动画持续时间
     */
    public static void hideToBottom(View view, int duraction ,Animation.AnimationListener animationListener) {
        if (view != null) {
            view.clearAnimation();
            view.setEnabled(false);
            TranslateAnimation hideAnim = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 1.0f);
            hideAnim.setDuration(duraction);
            hideAnim.setAnimationListener(animationListener);
            view.startAnimation(hideAnim);
            view.setVisibility(View.GONE);
        }
    }

    /**
     * 渐变显示
     *
     * @param view       the view
     * @param startAlpha 开始的渐变
     * @param endAlpha   结束的渐变
     * @param duraction  动画持续时间
     */
    public static void showFade(View view, float startAlpha, float endAlpha, int duraction ,Animation.AnimationListener animationListener) {
        if (view != null) {
            view.clearAnimation();
            view.setEnabled(true);
            Animation animation = new AlphaAnimation(startAlpha, endAlpha);
            animation.setDuration(duraction);
            animation.setAnimationListener(animationListener);
            view.startAnimation(animation);
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 渐变消失
     *
     * @param view       the view
     * @param startAlpha 开始的渐变
     * @param endAlpha   结束的渐变
     * @param duraction  动画持续时间
     */
    public static void hideFade(View view, float startAlpha, float endAlpha, int duraction ,Animation.AnimationListener animationListener) {
        if (view != null) {
            view.clearAnimation();
            view.setEnabled(false);
            Animation animation = new AlphaAnimation(startAlpha, endAlpha);
            animation.setDuration(duraction);
            animation.setAnimationListener(animationListener);
            view.startAnimation(animation);
            view.setVisibility(View.GONE);
        }
    }
}
