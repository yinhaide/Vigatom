package com.tencent.wx.app.utils;

import android.view.KeyEvent;
import com.tencent.vigatom.impl.ViKeyboardImpl;

/**
 * 各个厂商的按键实现：盛思达消费机的
 */
public class SSDAKeyboard extends ViKeyboardImpl {

    @Override
    public boolean isCancel() {
        return getKeyCode() == KeyEvent.KEYCODE_F2;
    }
}
