package com.tencent.vigatom.ue.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.TextView;
import com.tencent.vigatom.R;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 支持基于百分比的布局的帮助类。如果指定layoutWidthPercent，则无需指定layoutwidth/height。
 * 但是，如果希望视图能够占用的空间超过百分比值所允许的空间，则可以添加{layoutWidth/height="wrap_content"}。
 * 在这种情况下，如果百分比大小对于视图的内容来说太小，则将使用wrap_content规则调整其大小。
 */
public class PercentLayoutHelper {

    private final static String TAG = PercentLayoutHelper.class.getSimpleName();
    //百分比正则表达式
    private static final String REGEX_PERCENT = "^(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)%([s]?[wh]?)$";
    //父类布局
    private final ViewGroup mHost;
    //默认的屏幕长度(不给默认值的话用sh或者sw无法预览出来)
    private static int SCREEN_LONG = 1920;
    //默认的屏幕短度(不给默认值的话用sh或者sw无法预览出来)
    private static int SCREEN_SHOT = 1080;
    //屏幕宽度(不给默认值的话用sh或者sw无法预览出来)
    private static int mWidthScreen = SCREEN_SHOT;
    //屏幕高度(不给默认值的话用sh或者sw无法预览出来)
    private static int mHeightScreen = SCREEN_LONG;

    public PercentLayoutHelper(ViewGroup host, AttributeSet attrs) {
        mHost = host;
        if (!mHost.isInEditMode()) {//如果不是预览模式就读取真实的屏幕尺寸
            WindowManager wm = (WindowManager) mHost.getContext().getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            mWidthScreen = outMetrics.widthPixels;
            mHeightScreen = outMetrics.heightPixels;
        } else {
            //预览模式下需要调整父布局的预览的分辨率和横竖屏
            TypedArray array = mHost.getContext().obtainStyledAttributes(attrs, R.styleable.PercentLayout);
            boolean isVertical = array.getBoolean(R.styleable.PercentLayout_layout_isPreviewVertical, true);
            //isVertical = false;
            //屏幕宽度(不给默认值的话用sh或者sw无法预览出来)
            mWidthScreen = isVertical ? SCREEN_SHOT : SCREEN_LONG;
            //屏幕高度(不给默认值的话用sh或者sw无法预览出来)
            mHeightScreen = isVertical ? SCREEN_LONG : SCREEN_SHOT;
            array.recycle();
        }
    }

    /**
     * 设置预览尺寸，一般不用改，除非有特殊需求
     *
     * @param width 宽度
     * @param height 高度
     */
    public static void setPreviewSize(int width, int height) {
        SCREEN_LONG = height;
        SCREEN_SHOT = width;
    }

    /**
     * 获取屏幕高度
     */
    public int getScreenHeight() {
        return mHeightScreen;
    }

    /**
     * 要从{viewgroup.layoutparams setbaseattributes}调用的帮助程序方法重写，该方法读取布局宽度和布局高度属性值，如果它们不存在，则不引发异常。
     */
    public static void fetchWidthAndHeight(LayoutParams params, TypedArray array, int widthAttr,
            int heightAttr) {
        if (!array.hasValue(widthAttr)) {
            params.width = LayoutParams.WRAP_CONTENT;
        } else {
            params.width = array.getLayoutDimension(widthAttr, 0);
        }
        if (!array.hasValue(heightAttr)) {
            params.height = LayoutParams.WRAP_CONTENT;
        } else {
            params.height = array.getLayoutDimension(heightAttr, 0);
        }
    }

    /**
     * 在子级上迭代，并将其宽度和高度更改为根据百分比值计算的宽度和高度。
     *
     * @param widthMeasureSpec Width MeasureSpec of the parent ViewGroup.
     * @param heightMeasureSpec Height MeasureSpec of the parent ViewGroup.
     */
    public void adjustChildren(int widthMeasureSpec, int heightMeasureSpec) {
        int widthHint = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightHint = View.MeasureSpec.getSize(heightMeasureSpec);
        for (int i = 0, N = mHost.getChildCount(); i < N; i++) {
            View view = mHost.getChildAt(i);
            LayoutParams params = view.getLayoutParams();
            if (params instanceof PercentLayoutParams) {
                PercentLayoutInfo info = ((PercentLayoutParams) params).getPercentLayoutInfo();
                if (info != null) {
                    supportTextSize(widthHint, heightHint, view, info);
                    supportPadding(widthHint, heightHint, view, info);
                    supportMinOrMaxDimesion(widthHint, heightHint, view, info);
                    if (params instanceof ViewGroup.MarginLayoutParams) {
                        //add by haide 新增view往下传 -- start
                        info.fillMarginLayoutParams(view, (ViewGroup.MarginLayoutParams) params, widthHint, heightHint);
                        //add by haide -- end
                    } else if (params instanceof AbsoluteLayout.LayoutParams) {
                        info.fillAbsoluteLayout(view, (AbsoluteLayout.LayoutParams) params, widthHint,
                                heightHint);
                    } else {
                        info.fillLayoutParams(params, widthHint, heightHint);
                    }
                }
            }
        }
    }

    /**
     * 设置padding属性
     */
    private void supportPadding(int widthHint, int heightHint, View view, PercentLayoutInfo info) {
        int left = view.getPaddingLeft(), right = view.getPaddingRight(), top = view.getPaddingTop(), bottom = view
                .getPaddingBottom();
        int start = view.getPaddingStart(), end = view.getPaddingEnd();
        PercentLayoutInfo.PercentVal percentVal = info.paddingLeftPercent;
        if (percentVal != null) {
            int base = getBaseByModeAndVal(widthHint, heightHint, percentVal.basemode);
            left = (int) (base * percentVal.percent);
        }
        percentVal = info.paddingRightPercent;
        if (percentVal != null) {
            int base = getBaseByModeAndVal(widthHint, heightHint, percentVal.basemode);
            right = (int) (base * percentVal.percent);
        }
        percentVal = info.paddingTopPercent;
        if (percentVal != null) {
            int base = getBaseByModeAndVal(widthHint, heightHint, percentVal.basemode);
            top = (int) (base * percentVal.percent);
        }
        percentVal = info.paddingBottomPercent;
        if (percentVal != null) {
            int base = getBaseByModeAndVal(widthHint, heightHint, percentVal.basemode);
            bottom = (int) (base * percentVal.percent);
        }
        // add by haide.yin start -- 20200606
        percentVal = info.paddingStartPercent;
        if (percentVal != null) {
            int base = getBaseByModeAndVal(widthHint, heightHint, percentVal.basemode);
            start = (int) (base * percentVal.percent);
        }
        percentVal = info.paddingEndPercent;
        if (percentVal != null) {
            int base = getBaseByModeAndVal(widthHint, heightHint, percentVal.basemode);
            end = (int) (base * percentVal.percent);
        }
        view.setPadding(left, top, right, bottom);
        if (start > 0 || end > 0) {
            if (start == 0) {
                // TODO: 2020/6/7 这里还需要根据判断是否是中东国家，暂时不作处理，如果是中东国家则：start = right
                start = left;
            }
            if (end == 0) {
                // TODO: 2020/6/7 这里还需要根据判断是否是中东国家，暂时不作处理，如果是中东国家则：end = left
                end = right;
            }
            view.setPaddingRelative(start, top, end, bottom);
        }
        // add by haide.yin end -- 20200606
    }

    /**
     * 设置最大最小属性
     */
    private void supportMinOrMaxDimesion(int widthHint, int heightHint, View view, PercentLayoutInfo info) {
        try {
            Class clazz = view.getClass();
            invokeMethod("setMaxWidth", widthHint, heightHint, view, clazz, info.maxWidthPercent);
            invokeMethod("setMaxHeight", widthHint, heightHint, view, clazz, info.maxHeightPercent);
            invokeMethod("setMinWidth", widthHint, heightHint, view, clazz, info.minWidthPercent);
            invokeMethod("setMinHeight", widthHint, heightHint, view, clazz, info.minHeightPercent);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 反射
     */
    @SuppressWarnings("unchecked")
    private void invokeMethod(String methodName, int widthHint, int heightHint, View view, Class clazz,
            PercentLayoutInfo.PercentVal percentVal)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (percentVal != null) {
            Method setMaxWidthMethod = clazz.getMethod(methodName, int.class);
            setMaxWidthMethod.setAccessible(true);
            int base = getBaseByModeAndVal(widthHint, heightHint, percentVal.basemode);
            setMaxWidthMethod.invoke(view, (int) (base * percentVal.percent));
        }
    }

    /**
     * 设置文本大小属性
     */
    private void supportTextSize(int widthHint, int heightHint, View view, PercentLayoutInfo info) {
        //textsize percent support
        PercentLayoutInfo.PercentVal textSizePercent = info.textSizePercent;
        if (textSizePercent == null) {
            return;
        }
        int base = getBaseByModeAndVal(widthHint, heightHint, textSizePercent.basemode);
        float textSize = (int) (base * textSizePercent.percent);
        //Button 和 EditText 是TextView的子类
        if (view instanceof TextView) {
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
    }

    /**
     * 读取宽高模式(相对父类还是整个屏幕)
     */
    private static int getBaseByModeAndVal(int widthHint, int heightHint, PercentLayoutInfo.BASEMODE basemode) {
        switch (basemode) {
            case BASE_HEIGHT:
                return heightHint;
            case BASE_WIDTH:
                return widthHint;
            case BASE_SCREEN_WIDTH:
                return mWidthScreen;
            case BASE_SCREEN_HEIGHT:
                return mHeightScreen;
        }
        return 0;
    }

    /**
     * 读取布局中百分比的相关属性
     */
    public static PercentLayoutInfo getPercentLayoutInfo(Context context, AttributeSet attrs) {
        PercentLayoutInfo info = null;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PercentLayout);
        info = setAbsoluteLayoutVal(array, info);
        info = setWidthAndHeightVal(array, info);
        info = setMarginRelatedVal(array, info);
        info = setTextSizeSupportVal(array, info);
        info = setMinMaxWidthHeightRelatedVal(array, info);
        info = setPaddingRelatedVal(array, info);
        array.recycle();
        return info;
    }

    /**
     * 设置宽高相关属性
     */
    private static PercentLayoutInfo setAbsoluteLayoutVal(TypedArray array, PercentLayoutInfo info) {
        PercentLayoutInfo.PercentVal percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_xPercent,
                false);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.xPercent = percentVal;
        }
        percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_yPercent, false);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.yPercent = percentVal;
        }
        return info;
    }

    /**
     * 设置宽高相关属性
     */
    private static PercentLayoutInfo setWidthAndHeightVal(TypedArray array, PercentLayoutInfo info) {
        PercentLayoutInfo.PercentVal percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_widthPercent,
                true);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.widthPercent = percentVal;
        }
        percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_heightPercent, false);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.heightPercent = percentVal;
        }
        return info;
    }

    /**
     * 设置文本相关属性
     */
    private static PercentLayoutInfo setTextSizeSupportVal(TypedArray array, PercentLayoutInfo info) {
        //textSizePercent 默认以高度作为基准
        PercentLayoutInfo.PercentVal percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_textSizePercent,
                false);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.textSizePercent = percentVal;
        }
        return info;
    }

    /**
     * 设置最大最小宽高相关属性
     */
    private static PercentLayoutInfo setMinMaxWidthHeightRelatedVal(TypedArray array, PercentLayoutInfo info) {
        //maxWidth
        PercentLayoutInfo.PercentVal percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_maxWidthPercent,
                true);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.maxWidthPercent = percentVal;
        }
        //maxHeight
        percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_maxHeightPercent, false);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.maxHeightPercent = percentVal;
        }
        //minWidth
        percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_minWidthPercent, true);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.minWidthPercent = percentVal;
        }
        //minHeight
        percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_minHeightPercent, false);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.minHeightPercent = percentVal;
        }
        return info;
    }

    /**
     * 设置间距相关属性
     */
    private static PercentLayoutInfo setMarginRelatedVal(TypedArray array, PercentLayoutInfo info) {
        //默认margin参考宽度
        PercentLayoutInfo.PercentVal percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_marginPercent,
                true);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.leftMarginPercent = percentVal;
            info.topMarginPercent = percentVal;
            info.rightMarginPercent = percentVal;
            info.bottomMarginPercent = percentVal;
        }
        percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_marginLeftPercent, true);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.leftMarginPercent = percentVal;
        }
        percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_marginTopPercent, false);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.topMarginPercent = percentVal;
        }
        percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_marginRightPercent, true);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.rightMarginPercent = percentVal;
        }
        percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_marginBottomPercent, false);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.bottomMarginPercent = percentVal;
        }
        percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_marginStartPercent, true);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.startMarginPercent = percentVal;
        }
        percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_marginEndPercent, true);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.endMarginPercent = percentVal;
        }
        return info;
    }

    /**
     * 设置paddingPercent相关属性
     *
     * @param array array
     * @param info info
     */
    private static PercentLayoutInfo setPaddingRelatedVal(TypedArray array, PercentLayoutInfo info) {
        //默认padding以宽度为标准
        PercentLayoutInfo.PercentVal percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_paddingPercent,
                true);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.paddingLeftPercent = percentVal;
            info.paddingRightPercent = percentVal;
            info.paddingBottomPercent = percentVal;
            info.paddingTopPercent = percentVal;
            info.paddingStartPercent = percentVal;
            info.paddingEndPercent = percentVal;
        }
        percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_paddingLeftPercent, true);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.paddingLeftPercent = percentVal;
        }
        percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_paddingRightPercent, true);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.paddingRightPercent = percentVal;
        }
        percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_paddingTopPercent, true);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.paddingTopPercent = percentVal;
        }
        percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_paddingBottomPercent, true);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.paddingBottomPercent = percentVal;
        }
        percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_paddingStartPercent, true);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.paddingStartPercent = percentVal;
        }
        percentVal = getPercentVal(array, R.styleable.PercentLayout_layout_paddingEndPercent, true);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.paddingEndPercent = percentVal;
        }
        return info;
    }

    private static PercentLayoutInfo.PercentVal getPercentVal(TypedArray array, int index, boolean baseWidth) {
        String sizeStr = array.getString(index);
        return getPercentVal(sizeStr, baseWidth);
    }

    private static PercentLayoutInfo checkForInfoExists(PercentLayoutInfo info) {
        info = info != null ? info : new PercentLayoutInfo();
        return info;
    }

    /**
     * 宽度的百分比，例如：35%w => new PercentVal(35, true)
     *
     * @param percentStr percentStr
     * @param isOnWidth isOnWidth
     * @return PercentVal
     */
    private static PercentLayoutInfo.PercentVal getPercentVal(String percentStr, boolean isOnWidth) {
        //valid param
        if (percentStr == null) {
            return null;
        }
        Pattern p = Pattern.compile(REGEX_PERCENT);
        Matcher matcher = p.matcher(percentStr);
        if (!matcher.matches()) {
            throw new RuntimeException("the value of layout_xxxPercent invalid! ==>" + percentStr);
        }
        //int len = percentStr.length();
        //extract the float value
        String floatVal = matcher.group(1);
        //String lastAlpha = percentStr.substring(len - 1);
        float percent = Float.parseFloat(floatVal) / 100f;
        PercentLayoutInfo.PercentVal percentVal = new PercentLayoutInfo.PercentVal();
        percentVal.percent = percent;
        if (percentStr.endsWith(PercentLayoutInfo.BASEMODE.SW)) {
            percentVal.basemode = PercentLayoutInfo.BASEMODE.BASE_SCREEN_WIDTH;
        } else if (percentStr.endsWith(PercentLayoutInfo.BASEMODE.SH)) {
            percentVal.basemode = PercentLayoutInfo.BASEMODE.BASE_SCREEN_HEIGHT;
        } else if (percentStr.endsWith(PercentLayoutInfo.BASEMODE.PERCENT)) {
            if (isOnWidth) {
                percentVal.basemode = PercentLayoutInfo.BASEMODE.BASE_WIDTH;
            } else {
                percentVal.basemode = PercentLayoutInfo.BASEMODE.BASE_HEIGHT;
            }
        } else if (percentStr.endsWith(PercentLayoutInfo.BASEMODE.W)) {
            percentVal.basemode = PercentLayoutInfo.BASEMODE.BASE_WIDTH;
        } else if (percentStr.endsWith(PercentLayoutInfo.BASEMODE.H)) {
            percentVal.basemode = PercentLayoutInfo.BASEMODE.BASE_HEIGHT;
        } else {
            throw new IllegalArgumentException("the " + percentStr + " must be endWith [%|w|h|sw|sh]");
        }

        return percentVal;
    }

    /**
     * 在子级上迭代并恢复其原始维度，这些维度已更改为百分比值。只有在以前调用adjustchildren时，调用此方法才有意义。
     */
    public void restoreOriginalParams() {
        for (int i = 0, N = mHost.getChildCount(); i < N; i++) {
            View view = mHost.getChildAt(i);
            LayoutParams params = view.getLayoutParams();
            if (params instanceof PercentLayoutParams) {
                PercentLayoutInfo info = ((PercentLayoutParams) params).getPercentLayoutInfo();
                if (info != null) {
                    if (params instanceof ViewGroup.MarginLayoutParams) {
                        info.restoreMarginLayoutParams((ViewGroup.MarginLayoutParams) params);
                    } else {
                        info.restoreLayoutParams(params);
                    }
                }
            }
        }
    }

    /**
     * 遍历子级并检查是否有任何子级希望获得比通过百分比维度接收到的空间更多的空间。如果要构建支持百分比标注的布局，建议您使用此方法。
     * 开发人员应该能够通过添加带有{wrap_content}值的普通维度属性来指定应该重新测量子项。
     * 例如，他可以将子属性指定为app:layout_widthPercent="60%p"和android:layout_width="wrap_content"。
     * 在这种情况下，如果子对象接收到的空间太小，则将使用设置为wrap_content的宽度重新测量它。
     *
     * @return 如果子类需要更多空间的话返回true.
     */
    public boolean handleMeasuredStateTooSmall() {
        boolean needsSecondMeasure = false;
        for (int i = 0, N = mHost.getChildCount(); i < N; i++) {
            View view = mHost.getChildAt(i);
            LayoutParams params = view.getLayoutParams();
            if (params instanceof PercentLayoutParams) {
                PercentLayoutInfo info = ((PercentLayoutParams) params).getPercentLayoutInfo();
                if (info != null) {
                    if (shouldHandleMeasuredWidthTooSmall(view, info)) {
                        needsSecondMeasure = true;
                        params.width = LayoutParams.WRAP_CONTENT;
                    }
                    if (shouldHandleMeasuredHeightTooSmall(view, info)) {
                        needsSecondMeasure = true;
                        params.height = LayoutParams.WRAP_CONTENT;
                    }
                }
            }
        }
        return needsSecondMeasure;
    }

    /**
     * 处理宽度异常情况
     */
    @SuppressWarnings("deprecation")
    private static boolean shouldHandleMeasuredWidthTooSmall(View view, PercentLayoutInfo info) {
        int state = ViewCompat.getMeasuredWidthAndState(view) & ViewCompat.MEASURED_STATE_MASK;
        if (info == null || info.widthPercent == null) {
            return false;
        }
        return state == ViewCompat.MEASURED_STATE_TOO_SMALL && info.widthPercent.percent >= 0
                && info.mPreservedParams.width == LayoutParams.WRAP_CONTENT;
    }

    /**
     * 处理高度异常情况
     */
    @SuppressWarnings("deprecation")
    private static boolean shouldHandleMeasuredHeightTooSmall(View view, PercentLayoutInfo info) {
        int state = ViewCompat.getMeasuredHeightAndState(view) & ViewCompat.MEASURED_STATE_MASK;
        if (info == null || info.heightPercent == null) {
            return false;
        }
        return state == ViewCompat.MEASURED_STATE_TOO_SMALL && info.heightPercent.percent >= 0
                && info.mPreservedParams.height == LayoutParams.WRAP_CONTENT;
    }

    /* **************************************** 自定义内部类 ******************************************** */

    /**
     * 百分比布局的基本结构
     */
    public static class PercentLayoutInfo {

        /**
         * 宽高模式枚举类(相对父类还是屏幕)
         */
        private enum BASEMODE {

            BASE_WIDTH, BASE_HEIGHT, BASE_SCREEN_WIDTH, BASE_SCREEN_HEIGHT;

            /**
             * width_parent
             */
            public static final String PERCENT = "%";
            /**
             * width_parent
             */
            public static final String W = "w";
            /**
             * height_parent
             */
            public static final String H = "h";
            /**
             * width_screen
             */
            public static final String SW = "sw";
            /**
             * height_screen
             */
            public static final String SH = "sh";
        }

        public static class PercentVal {

            //百分比
            float percent = -1;
            //宽高模式
            BASEMODE basemode;

            PercentVal() {
            }

            @Override
            public String toString() {
                return "PercentVal{" +
                        "percent=" + percent +
                        ", basemode=" + basemode +
                        '}';
            }
        }

        //宽高
        PercentVal widthPercent;
        PercentVal heightPercent;
        //间距
        PercentVal leftMarginPercent;
        PercentVal topMarginPercent;
        PercentVal rightMarginPercent;
        PercentVal bottomMarginPercent;
        PercentVal startMarginPercent;
        PercentVal endMarginPercent;
        //文本大小
        PercentVal textSizePercent;
        //最大最小宽高范围值
        PercentVal maxWidthPercent;
        PercentVal maxHeightPercent;
        PercentVal minWidthPercent;
        PercentVal minHeightPercent;
        //内部间距
        PercentVal paddingLeftPercent;
        PercentVal paddingRightPercent;
        PercentVal paddingTopPercent;
        PercentVal paddingBottomPercent;
        PercentVal paddingStartPercent;
        PercentVal paddingEndPercent;
        //AbsoluteLayout专用的属性
        PercentVal xPercent;
        PercentVal yPercent;
        //缓存旧的间距参数
        final ViewGroup.MarginLayoutParams mPreservedParams;

        PercentLayoutInfo() {
            mPreservedParams = new ViewGroup.MarginLayoutParams(0, 0);
        }

        /**
         * 处理宽高参数
         */
        void fillLayoutParams(LayoutParams params, int widthHint, int heightHint) {
            //缓存原始宽高参数，在测量阶段之后就可以恢复
            mPreservedParams.width = params.width;
            mPreservedParams.height = params.height;
            if (widthPercent != null) {
                int base = getBaseByModeAndVal(widthHint, heightHint, widthPercent.basemode);
                params.width = (int) (base * widthPercent.percent);
            }
            if (heightPercent != null) {
                int base = getBaseByModeAndVal(widthHint, heightHint, heightPercent.basemode);
                params.height = (int) (base * heightPercent.percent);
            }
        }

        /**
         * 设置AbsoluteLayout属性
         */
        private void fillAbsoluteLayout(View childView, AbsoluteLayout.LayoutParams params, int widthHint,
                int heightHint) {
            fillLayoutParams(params, widthHint, heightHint);
            //x间距
            if (xPercent != null) {
                int base = getBaseByModeAndVal(widthHint, heightHint, xPercent.basemode);
                params.x = (int) (base * xPercent.percent);
            }
            //y间距
            if (yPercent != null) {
                int base = getBaseByModeAndVal(widthHint, heightHint, yPercent.basemode);
                params.y = (int) (base * yPercent.percent);
            }
            childView.setLayoutParams(params);
        }

        /**
         * 处理间距参数
         */
        void fillMarginLayoutParams(View childView, ViewGroup.MarginLayoutParams params, int widthHint,
                int heightHint) {
            fillLayoutParams(params, widthHint, heightHint);
            //缓存原始间距参数，在测量阶段之后就可以恢复
            mPreservedParams.leftMargin = params.leftMargin;
            mPreservedParams.topMargin = params.topMargin;
            mPreservedParams.rightMargin = params.rightMargin;
            mPreservedParams.bottomMargin = params.bottomMargin;
            //MarginLayoutParamsCompat.setMarginStart(mPreservedParams, MarginLayoutParamsCompat.getMarginStart(params));
            //MarginLayoutParamsCompat.setMarginEnd(mPreservedParams, MarginLayoutParamsCompat.getMarginEnd(params));
            if (leftMarginPercent != null) {
                int base = getBaseByModeAndVal(widthHint, heightHint, leftMarginPercent.basemode);
                params.leftMargin = (int) (base * leftMarginPercent.percent);
            }
            if (topMarginPercent != null) {
                int base = getBaseByModeAndVal(widthHint, heightHint, topMarginPercent.basemode);
                params.topMargin = (int) (base * topMarginPercent.percent);
            }
            if (rightMarginPercent != null) {
                int base = getBaseByModeAndVal(widthHint, heightHint, rightMarginPercent.basemode);
                params.rightMargin = (int) (base * rightMarginPercent.percent);
            }
            if (bottomMarginPercent != null) {
                int base = getBaseByModeAndVal(widthHint, heightHint, bottomMarginPercent.basemode);
                params.bottomMargin = (int) (base * bottomMarginPercent.percent);
            }
            if (startMarginPercent != null) {
                int base = getBaseByModeAndVal(widthHint, heightHint, startMarginPercent.basemode);
                //MarginLayoutParamsCompat.setMarginStart(params, (int) (base * startMarginPercent.percent));
                //距离Start部分特殊处理，childView要从上个方法传 add by haide -- start
                params.setMarginStart((int) (base * startMarginPercent.percent));
                childView.setLayoutParams(params);
                //add by haide -- end
            }
            if (endMarginPercent != null) {
                int base = getBaseByModeAndVal(widthHint, heightHint, endMarginPercent.basemode);
                //MarginLayoutParamsCompat.setMarginEnd(params, (int) (base * endMarginPercent.percent));
                //距离End部分特殊处理，childView要从上个方法传 add by haide -- start
                params.setMarginEnd((int) (base * endMarginPercent.percent));
                childView.setLayoutParams(params);
                //add by haide -- end
            }
        }

        /**
         * 在百分比布局基本参数改变之后恢复之前存储的间距参数。仅会在之前调用过fillMarginLayoutParams的情况下使用
         */
        void restoreMarginLayoutParams(ViewGroup.MarginLayoutParams params) {
            restoreLayoutParams(params);
            params.leftMargin = mPreservedParams.leftMargin;
            params.topMargin = mPreservedParams.topMargin;
            params.rightMargin = mPreservedParams.rightMargin;
            params.bottomMargin = mPreservedParams.bottomMargin;
            //MarginLayoutParamsCompat.setMarginStart(params, MarginLayoutParamsCompat.getMarginStart(mPreservedParams));
            //MarginLayoutParamsCompat.setMarginEnd(params, MarginLayoutParamsCompat.getMarginEnd(mPreservedParams));
        }

        /**
         * 在百分比布局基本参数改变之后恢复之前存储的宽高参数。仅会在之前调用过fillLayoutParams的情况下使用
         */
        void restoreLayoutParams(LayoutParams params) {
            params.width = mPreservedParams.width;
            params.height = mPreservedParams.height;
        }
    }

    /* **************************************** 接口回调 ******************************************** */

    /**
     * 如果布局想要支持百分比布局以及使用这个Helper类，需要实现这个接口以及包含PercentLayoutInfo
     */
    public interface PercentLayoutParams {

        PercentLayoutInfo getPercentLayoutInfo();
    }
}
