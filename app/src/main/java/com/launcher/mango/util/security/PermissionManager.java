package com.launcher.mango.util.security;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.launcher.mango.BaseActivity;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author tic
 *         created on 18-6-27
 */

public final class PermissionManager {

    private BaseActivity activity;
    private String[] unauthorizedPermission;

    /**
     * 请求获取权限
     *
     * @param activity
     */
    public void requestPermission(BaseActivity activity) {
        this.activity = activity;
        Security permissions = activity.getClass().getAnnotation(Security.class);
        if (permissions == null) {
            Log.d("PermissionManager", "no permission request");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !havePermission(permissions.value())) {
            activity.requestPermissions(this.unauthorizedPermission, 0x123);
        }
    }

    /**
     * 检查已经申请到的权限，并保存未授权的权限，再次申请
     *
     * @param permissions
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean havePermission(String... permissions) {
        ArrayList<String> permissionList = new ArrayList<>();

        for (String permission : permissions) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        if (permissionList.size() > 0) {
            this.unauthorizedPermission = permissionList.toArray(new String[permissionList.size()]);
            Log.d("PermissionManager", "尚未赋予的权限:" + Arrays.toString(unauthorizedPermission));
            return false;
        }

        return true;
    }

    /**
     * 权限申请结果通知，需要在Activity onRequestPermissionResult方法中调用
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                activity.onPermissionRefuse(permissions[i]);
            }
        }
    }
}
