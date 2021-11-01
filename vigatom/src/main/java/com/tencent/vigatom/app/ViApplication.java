package com.tencent.vigatom.app;

import android.support.multidex.MultiDexApplication;
import com.tencent.vigatom.Vigatom;

/**
 * 本类给出一个Application的Demo，当然开发者也可以不用这个，使用自己的
 */
public class ViApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Vigatom.getInstance().init(this);
    }
}
