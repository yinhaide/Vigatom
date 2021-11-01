package com.tencent.wx.app.view;

import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Display;
import android.view.View;
import com.haideyin.app.R;
import com.tencent.vigatom.Vigatom;
import com.tencent.vigatom.ue.view.ViView;
import com.tencent.vigatom.utils.ScreenUtil;
import com.tencent.wx.app.helper.DialogHelper;
import com.tencent.wx.app.helper.PresentationHelper;

/**
 * 副屏相关的演示页面
 */
public class PresentationView extends ViView {

    @Override
    public int onInflateLayout() {
        return ScreenUtil.isLanscape(Vigatom.getAppContext()) ? R.layout.view_presentation_l
                : R.layout.view_presentation_p;
    }

    @Override
    public void onCreate(View inflateView) {
        initView(inflateView);
    }

    /**
     * 页面初始化
     *
     * @param inflateView 页面信息
     */
    private void initView(View inflateView) {
        inflateView.findViewById(R.id.bt_presentation).setOnClickListener(v -> {
            DisplayManager displayManager = (DisplayManager) Vigatom.getAppContext()
                    .getSystemService(Context.DISPLAY_SERVICE);
            Display[] displays = displayManager.getDisplays();
            if (displayManager != null && displays.length < 2) {
                toast("设备不存在副屏");
                return;
            }
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(getContext())) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getContext().getPackageName()));
                    //getContext().startActivityForResult(intent, 10);
                    toast("没权限，需要打开应用的悬浮权限");
                    return;
                }
            }
            PresentationHelper.getInstance().showDisplay();
        });
        inflateView.findViewById(R.id.bt_dialog).setOnClickListener(v -> {
            DialogHelper.getInstance().show(getContext());
        });
    }
}
