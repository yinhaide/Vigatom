package com.tencent.wx.app.view;

import android.view.View;
import android.widget.TextView;
import com.haideyin.app.R;
import com.tencent.vigatom.Vigatom;
import com.tencent.vigatom.api.IViKeyboard;
import com.tencent.vigatom.ue.view.ViView;
import com.tencent.vigatom.utils.ScreenUtil;

/**
 * 按键支持的演示页面
 */
public class KeyBoardView extends ViView {

    private TextView contentTv;

    @Override
    public int onInflateLayout() {
        return ScreenUtil.isLanscape(Vigatom.getAppContext()) ? R.layout.view_keyboard_l
                : R.layout.view_keyboard_p;
    }

    @Override
    public void onCreate(View inflateView) {
        contentTv = inflateView.findViewById(R.id.content_tv);
    }

    @Override
    public boolean onKey(IViKeyboard iViKeyBoard) {
        //只要按键抬起的场景，因为按下可能会因为长按多次回调
        if (iViKeyBoard.isKeyUp()) {
            if (iViKeyBoard.isEnter()) {
                toast("回车确认", 500);
            }
            if (iViKeyBoard.isPoint()) {
                toast("符号点", 500);
            }
            if (iViKeyBoard.isNum()) {
                toast("数字：" + iViKeyBoard.getKeyMapping(), 500);
            }
            if (iViKeyBoard.isDelete()) {
                toast("删除", 500);
            }
            if (iViKeyBoard.isAdd()) {
                toast("加号", 500);
            }
            if (iViKeyBoard.isFind()) {
                toast("查询", 500);
            }
            if (iViKeyBoard.isFunction()) {
                toast("功能", 500);
            }
            contentTv.setText("按下按键：" + iViKeyBoard.getKeyMapping());
        }
        return super.onKey(iViKeyBoard);
    }
}
