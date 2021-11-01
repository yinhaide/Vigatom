package com.tencent.vigatom.ue.widget;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tencent.vigatom.R;
import com.tencent.vigatom.api.IViKeyboard;
import com.tencent.vigatom.ue.layout.PercentRelativeLayout;
import com.tencent.vigatom.utils.ScreenUtil;

/**
 * 具备EditText效果的widget，避免焦点冲突的问题
 */
public class ViEditTextWidget extends PercentRelativeLayout {

    private TextView tvContent;//密码内容
    private TextView tvHint;//密码内容
    private boolean isPassword = true;//是否是密码类型
    private boolean isDelete = false;//是否是密码类型
    private String content = "";//内容
    private String hint;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            StringBuffer newString = new StringBuffer();
            for (int i = 0; i < content.length(); i++) {
                newString.append("*");
            }
            tvContent.setText(newString.toString());
        }
    };

    public ViEditTextWidget(Context context) {
        this(context, null);
    }

    public ViEditTextWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViEditTextWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //View被窗体移除的时候释放动画资源
        handler.removeCallbacks(runnable);
    }

    /**
     * 初始化UI
     *
     * @param context 上下文
     * @param attrs 属性
     */
    private void init(Context context, AttributeSet attrs) {
        LinearLayout.LayoutParams viewGroupLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(viewGroupLayoutParams);
        this.setGravity(Gravity.CENTER);
        int layoutId = ScreenUtil.isLanscape(context)?R.layout.vigatom_widget_edittext_l:R.layout.vigatom_widget_edittext_p;
        View view = LayoutInflater.from(context).inflate(layoutId, this, true);
        tvContent = view.findViewById(R.id.content);
        tvHint = view.findViewById(R.id.hint);
        hint = tvHint.getText().toString();
    }

    /**
     * 设置隐藏提示
     *
     * @param hint 内容
     */
    public void setHint(String hint) {
        this.hint = hint;
        content = "";
        tvHint.setVisibility(VISIBLE);
        tvHint.setText(hint);
        tvContent.setVisibility(INVISIBLE);
    }

    /**
     * 设置是否是密码的
     *
     * @param password 是否是密码
     */
    public void setPassword(boolean password) {
        isPassword = password;
        if (TextUtils.isEmpty(content)) {
            setHint(hint);
        } else {
            setContent(content);
        }
    }

    /**
     * 读取内容
     *
     * @return 内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 获取文本控件
     *
     * @return 文本控件
     */
    public TextView getTextView() {
        return tvContent;
    }

    /**
     * 设置内容
     *
     * @param content 内容
     */
    public void setContent(String content) {
        inputContent(content, false);
    }

    /**
     * 输入内容
     *
     * @param content 内容
     * @param isInput 是否是输入模式
     */
    private void inputContent(String content, boolean isInput) {
        this.content = content;
        tvHint.setVisibility(INVISIBLE);
        if (isPassword) {
            StringBuffer newString = new StringBuffer();
            if (isDelete || !isInput) {
                for (int i = 0; i < content.length(); i++) {
                    newString.append("*");
                }
            } else {
                for (int i = 0; i < content.length() - 1; i++) {
                    newString.append("*");
                }
                newString.append(content.substring(content.length() - 1));
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 500);
            }
            tvContent.setText(newString.toString());
        } else {
            tvContent.setText(content);
        }
        tvContent.setVisibility(VISIBLE);
    }

    /**
     * 执行按键操作
     *
     * @param iViKeyBoard 按键事件
     */
    public void keyUp(IViKeyboard iViKeyBoard) {
        isDelete = false;
        if (iViKeyBoard.isDelete()) {
            if (!TextUtils.isEmpty(content)) {
                isDelete = true;
                content = content.substring(0, content.length() - 1);
            }
        } else if (iViKeyBoard.isCancel()) {
            content = "";
        }
        content = content + iViKeyBoard.getKeyMapping();
        if (TextUtils.isEmpty(content)) {
            setHint(hint);
        } else {
            inputContent(content, true);
        }
        onChangeNext(content);
    }

    /************************ 输入内容变化回调 start *******************/

    //listener全局变量
    private OnChangeListener onChangeListener;

    //供外部调用的set方法
    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    //内部调用传递信息到外部
    private void onChangeNext(String content) {
        if (this.onChangeListener != null) {
            onChangeListener.onChange(content);
        }
    }

    //定义listener
    public interface OnChangeListener {

        void onChange(String content);
    }

    /************************ 输入内容变化回调 end  *******************/
}
