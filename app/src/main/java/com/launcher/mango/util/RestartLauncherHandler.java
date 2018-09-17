package com.launcher.mango.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;

/**
 * restart launcher
 *
 * @author tic
 * created on 18-9-17
 */
public class RestartLauncherHandler implements Runnable {

    private static final String TAG = RestartLauncherHandler.class.getSimpleName();

    private static final long PROCESS_KILL_DELAY_MS = 1000;

    private static final int RESTART_REQUEST_CODE = 42; // the answer to everything

    private final Context mContext;

    private Runnable mCallback;

    public RestartLauncherHandler(Context context) {
        mContext = context;
    }

    public void setCallback(Runnable mCallback) {
        this.mCallback = mCallback;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void run() {
        // execute something before restart
        if (mCallback != null) {
            mCallback.run();
        }

        // Wait for it
        try {
            Thread.sleep(PROCESS_KILL_DELAY_MS);
        } catch (Exception e) {
            Log.e(TAG, "Error waiting", e);
        }

        // Schedule an alarm before we kill ourself.
        Intent homeIntent = new Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_HOME)
                .setPackage(mContext.getPackageName())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(mContext, RESTART_REQUEST_CODE,
                homeIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        mContext.getSystemService(AlarmManager.class)
                .setExact(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 50, pi);

        // Kill process
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
