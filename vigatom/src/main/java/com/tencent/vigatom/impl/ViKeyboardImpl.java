package com.tencent.vigatom.impl;

import android.view.KeyEvent;

import com.tencent.vigatom.api.IViKeyboard;

public class ViKeyboardImpl implements IViKeyboard {

    private int keyCode;
    private KeyEvent keyEvent;

    public ViKeyboardImpl() {

    }

    @Override
    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    @Override
    public void setKeyEvent(KeyEvent keyEvent) {
        this.keyEvent = keyEvent;
    }

    @Override
    public int getKeyCode() {
        return keyCode;
    }

    @Override
    public KeyEvent getKeyEvent() {
        return keyEvent;
    }

    @Override
    public boolean isEnter() {
        if(keyEvent != null){
            // TODO: 2021/10/16 weixiao ssda keyboard
            return (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getScanCode() == 28)
                    || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER;
        }else{
            return keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER;
        }
    }

    @Override
    public boolean isCancel() {
        return keyCode == KeyEvent.KEYCODE_ESCAPE;
    }

    @Override
    public boolean isFind() {
        return keyCode == KeyEvent.KEYCODE_F1;
    }

    @Override
    public boolean isAdd() {
        return keyCode == KeyEvent.KEYCODE_NUMPAD_ADD;
    }

    @Override
    public boolean isDelete() {
        return keyCode == KeyEvent.KEYCODE_DEL;
    }

    @Override
    public boolean isFunction() {
        return keyCode == KeyEvent.KEYCODE_MENU;
    }

    @Override
    public boolean isUp() {
        return keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_LEFT;
    }

    @Override
    public boolean isDown() {
        return keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT;
    }

    @Override
    public boolean isPoint() {
        return keyCode == KeyEvent.KEYCODE_NUMPAD_DOT || keyCode == KeyEvent.KEYCODE_PERIOD;
    }

    @Override
    public boolean isNum() {
        return (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) ||
                (keyCode >= KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent.KEYCODE_NUMPAD_9);
    }

    @Override
    public boolean isNum0() {
        return keyCode == KeyEvent.KEYCODE_0 || keyCode == KeyEvent.KEYCODE_NUMPAD_0;
    }

    @Override
    public boolean isNum1() {
        return keyCode == KeyEvent.KEYCODE_1 || keyCode == KeyEvent.KEYCODE_NUMPAD_1;
    }

    @Override
    public boolean isNum2() {
        return keyCode == KeyEvent.KEYCODE_2 || keyCode == KeyEvent.KEYCODE_NUMPAD_2;
    }

    @Override
    public boolean isNum3() {
        return keyCode == KeyEvent.KEYCODE_3 || keyCode == KeyEvent.KEYCODE_NUMPAD_3;
    }

    @Override
    public boolean isNum4() {
        return keyCode == KeyEvent.KEYCODE_4 || keyCode == KeyEvent.KEYCODE_NUMPAD_4;
    }

    @Override
    public boolean isNum5() {
        return keyCode == KeyEvent.KEYCODE_5 || keyCode == KeyEvent.KEYCODE_NUMPAD_5;
    }

    @Override
    public boolean isNum6() {
        return keyCode == KeyEvent.KEYCODE_6 || keyCode == KeyEvent.KEYCODE_NUMPAD_6;
    }

    @Override
    public boolean isNum7() {
        return keyCode == KeyEvent.KEYCODE_7 || keyCode == KeyEvent.KEYCODE_NUMPAD_7;
    }

    @Override
    public boolean isNum8() {
        return keyCode == KeyEvent.KEYCODE_8 || keyCode == KeyEvent.KEYCODE_NUMPAD_8;
    }

    @Override
    public boolean isNum9() {
        return keyCode == KeyEvent.KEYCODE_9 || keyCode == KeyEvent.KEYCODE_NUMPAD_9;
    }

    @Override
    public boolean isKeyUp() {
        return keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_UP;
    }

    @Override
    public boolean isKeyDown() {
        return keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN;
    }

    @Override
    public String getKeyMapping() {
        if(isNum0()){
            return "0";
        }
        if(isNum1()){
            return "1";
        }
        if(isNum2()){
            return "2";
        }
        if(isNum3()){
            return "3";
        }
        if(isNum4()){
            return "4";
        }if(isNum5()){
            return "5";
        }if(isNum6()){
            return "6";
        }
        if(isNum7()){
            return "7";
        }
        if(isNum8()){
            return "8";
        }
        if(isNum9()){
            return "9";
        }
        if(isPoint()){
            return ".";
        }
        if(isAdd()){
            return "+";
        }
        return "";
    }
}
