package com.tencent.vigatom.api;

import android.view.KeyEvent;

/**
 * Keg ability
 */
public interface IViKeyboard {

    /**
     * set keycode
     * @param keyCode code
     */
    void setKeyCode(int keyCode);

    /**
     * set keyevent
     * @param keyEvent event
     */
    void setKeyEvent(KeyEvent keyEvent);

    /**
     * get keycode
     * @return  code
     */
    int getKeyCode();

    /**
     * get keyevent
     * @return  event
     */
    KeyEvent getKeyEvent();

    /**
     * 是否为回车键
     * @return 结果
     */
    boolean isEnter();

    /**
     * 是否为取消键
     * @return 结果
     */
    boolean isCancel();

    /**
     * 是否为查找键
     * @return 结果
     */
    boolean isFind();

    /**
     * 是否为加号键
     * @return 结果
     */
    boolean isAdd();

    /**
     * 是否为删除键
     * @return 结果
     */
    boolean isDelete();

    /**
     * 是否为功能键
     * @return 结果
     */
    boolean isFunction();

    /**
     * 是否为UP键
     * @return 结果
     */
    boolean isUp();

    /**
     * 是否为DOWN键
     * @return 结果
     */
    boolean isDown();

    /**
     * 是否为点键
     * @return 结果
     */
    boolean isPoint();

    /**
     * 是否为数字键0-9
     * @return 结果
     */
    boolean isNum();

    /**
     * 是否为数字键0
     * @return 结果
     */
    boolean isNum0();

    /**
     * 是否为数字键1
     * @return 结果
     */
    boolean isNum1();

    /**
     * 是否为数字键2
     * @return 结果
     */
    boolean isNum2();

    /**
     * 是否为数字键3
     * @return 结果
     */
    boolean isNum3();

    /**
     * 是否为数字键4
     * @return 结果
     */
    boolean isNum4();

    /**
     * 是否为数字键5
     * @return 结果
     */
    boolean isNum5();

    /**
     * 是否为数字键6
     * @return 结果
     */
    boolean isNum6();

    /**
     * 是否为数字键7
     * @return 结果
     */
    boolean isNum7();

    /**
     * 是否为数字键8
     * @return 结果
     */
    boolean isNum8();

    /**
     * 是否为数字键9
     * @return 结果
     */
    boolean isNum9();

    /**
     * 是否是按键抬起
     * @return 结果
     */
    boolean isKeyUp();

    /**
     * 是否是按键按下
     * @return 结果
     */
    boolean isKeyDown();

    /**
     * 获取按键和值的隐射值，比如KEYCODE_1返回1
     * @return 映射值
     */
    String getKeyMapping();
}
