package com.launcher.root;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author tic
 * created on 18-9-17
 */
public class NoRootReceiver extends BroadcastReceiver {

    private static final String ACTION_DUMP_DB = "com.launcher.mango.db";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_DUMP_DB.equals(action)) {

        }
    }
}
