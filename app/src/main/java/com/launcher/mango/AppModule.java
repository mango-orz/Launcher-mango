package com.launcher.mango;

import android.content.Context;

import com.launcher.mango.util.LooperExecutor;

/**
 * provider
 * @author tic
 * created on 18-9-17
 */
public class AppModule {

    private static LooperExecutor mLooper;


    public static void init(Context context) {

        initAsync(context);
    }

    private static void initAsync(Context context) {
        mLooper = new LooperExecutor(LauncherModel.getWorkerLooper());
    }

    public static LooperExecutor provideLooper() {
        return mLooper;
    }

}
