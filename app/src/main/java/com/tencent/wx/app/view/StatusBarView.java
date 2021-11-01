package com.tencent.wx.app.view;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import com.haideyin.app.R;
import com.tencent.vigatom.Vigatom;
import com.tencent.vigatom.ue.view.ViView;
import com.tencent.vigatom.utils.ScreenUtil;

/**
 * 状态栏演示
 */
public class StatusBarView extends ViView {

    //键盘是否存在
    private boolean hasKeyBoard = true;
    //显示内容
    private TextView contentTv;

    @Override
    public int onInflateLayout() {
        return ScreenUtil.isLanscape(Vigatom.getAppContext()) ? R.layout.view_statusbar_l
                : R.layout.view_statusbar_p;
    }

    @Override
    public void onCreate(View inflateView) {
        initView(inflateView);
    }

    /**
     * 页面初始化
     */
    private void initView(View inflateView) {
        contentTv = inflateView.findViewById(R.id.content_tv);
        SeekBar sbColor = inflateView.findViewById(R.id.sb_color);
        //自定义状态栏
        inflateView.findViewById(R.id.bt_immersion).setOnClickListener(v -> {
            contentTv.setText(((Button) v).getText());
            if (getViStatusBarView() != null) {
                getViStatusBarView().setVigatomStyle();
            }
        });
        inflateView.findViewById(R.id.bt_white_vigatom).setOnClickListener(v -> {
            contentTv.setText(((Button) v).getText());
            if (getViStatusBarView() != null) {
                //白色风格状态栏
                getViStatusBarView().setWhiteStyle();
            }
        });
        inflateView.findViewById(R.id.bt_black_vigatom).setOnClickListener(v -> {
            contentTv.setText(((Button) v).getText());
            if (getViStatusBarView() != null) {
                //白色风格状态栏
                getViStatusBarView().setBlackStyle();
            }
        });
        inflateView.findViewById(R.id.bt_black_keyboard).setOnClickListener(v -> {
            contentTv.setText(((Button) v).getText());
            if (getViStatusBarView() != null) {
                hasKeyBoard = !hasKeyBoard;
                //白色风格状态栏
                getViStatusBarView().setHasKeyboard(hasKeyBoard);
            }
        });
        //系统状态栏
        inflateView.findViewById(R.id.bt_not_immersion).setOnClickListener(v -> {
            contentTv.setText(((Button) v).getText());
            if (getViStatusBarView() != null) {
                getViStatusBarView().setSystemStyle();
            }
        });
        //状态栏颜色设置
        inflateView.findViewById(R.id.bt_color_black).setOnClickListener(v -> {
            contentTv.setText(((Button) v).getText());
            if (getViStatusBarView() != null) {
                getViStatusBarView().setBackgroundColor(Color.BLACK);
            }
        });
        inflateView.findViewById(R.id.bt_color_white).setOnClickListener(v -> {
            contentTv.setText(((Button) v).getText());
            if (getViStatusBarView() != null) {
                getViStatusBarView().setBackgroundColor(Color.WHITE);
            }
        });
        inflateView.findViewById(R.id.bt_color_red).setOnClickListener(v -> {
            contentTv.setText(((Button) v).getText());
            if (getViStatusBarView() != null) {
                getViStatusBarView().setBackgroundColor(Color.RED);
            }
        });
        //状态栏透明度设置
        sbColor.setMax(255);
        sbColor.setProgress(255);
        sbColor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                getViStatusBarView().setBackgroundAlpha((float) progress / 255);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
