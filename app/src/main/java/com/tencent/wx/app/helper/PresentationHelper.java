package com.tencent.wx.app.helper;


import android.content.Context;
import android.hardware.display.DisplayManager;
import android.view.Display;
import com.tencent.vigatom.Vigatom;
import com.tencent.vigatom.helper.DialogHelper;
import com.tencent.wx.app.dialog.ViPresentation;

/**
 * 副屏相关的单例
 */
public class PresentationHelper {

    private static final String TAG = PresentationHelper.class.getSimpleName();
    private ViPresentation viPresentation;

    private static volatile PresentationHelper instance;

    private PresentationHelper() {
        init();
    }

    public static PresentationHelper getInstance() {
        if (instance == null) {
            synchronized (DialogHelper.class) {
                if (instance == null) {
                    instance = new PresentationHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     */
    public void init() {
        DisplayManager displayManager = (DisplayManager) Vigatom.getAppContext()
                .getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = displayManager.getDisplays();
        if (displayManager != null && displays.length > 1) {
            viPresentation = new ViPresentation(Vigatom.getAppContext(), displays[1]);
        }
    }

    /**
     * 显示
     */
    public void showDisplay() {
        if (viPresentation != null && !viPresentation.isShowing()) {
            viPresentation.show();
        }
    }

    /**
     * 隐藏
     */
    public void dismiss() {
        if (viPresentation != null && viPresentation.isShowing()) {
            viPresentation.dismiss();
        }
    }
}
