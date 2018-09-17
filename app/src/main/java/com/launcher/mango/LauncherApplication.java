package com.launcher.mango;

import android.app.Application;

/**
 * @author tic
 * created on 18-9-17
 */
public class LauncherApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppModule.init(this);
    }
}
