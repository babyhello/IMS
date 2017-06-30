package com.apps.ims;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by androids on 2017/4/18.
 */

public class IMSApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        MultiDex.install(getApplicationContext());
        // the following line is important
        Fresco.initialize(getApplicationContext());
    }


}
