/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.launcher.mango.graphics;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.launcher.mango.AppModule;
import com.launcher.mango.LauncherAppState;
import com.launcher.mango.R;
import com.launcher.mango.Utilities;
import com.launcher.mango.util.RestartLauncherHandler;

import java.lang.reflect.Field;

import static com.launcher.mango.Utilities.getDevicePrefs;

/**
 * Utility class to override shape of {@link android.graphics.drawable.AdaptiveIconDrawable}.
 */
@TargetApi(Build.VERSION_CODES.O)
public class IconShapeOverride {

    private static final String TAG = "IconShapeOverride";

    public static final String KEY_PREFERENCE = "pref_override_icon_shape";

    public static boolean isSupported(Context context) {
        if (!Utilities.ATLEAST_OREO) {
            return false;
        }
        // Only supported when developer settings is enabled
        if (Settings.Global.getInt(context.getContentResolver(),
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) != 1) {
            return false;
        }

        try {
            if (getSystemResField().get(null) != Resources.getSystem()) {
                // Our assumption that mSystem is the system resource is not true.
                return false;
            }
        } catch (Exception e) {
            // Ignore, not supported
            return false;
        }

        return getConfigResId() != 0;
    }

    public static void apply(Context context) {
        if (!Utilities.ATLEAST_OREO) {
            return;
        }
        String path = getAppliedValue(context);
        if (TextUtils.isEmpty(path)) {
            return;
        }
        if (!isSupported(context)) {
            return;
        }

        // magic
        try {
            Resources override =
                    new ResourcesOverride(Resources.getSystem(), getConfigResId(), path);
            getSystemResField().set(null, override);
        } catch (Exception e) {
            Log.e(TAG, "Unable to override icon shape", e);
            // revert value.
            getDevicePrefs(context).edit().remove(KEY_PREFERENCE).apply();
        }
    }

    private static Field getSystemResField() throws Exception {
        Field staticField = Resources.class.getDeclaredField("mSystem");
        staticField.setAccessible(true);
        return staticField;
    }

    private static int getConfigResId() {
        return Resources.getSystem().getIdentifier("config_icon_mask", "string", "android");
    }

    private static String getAppliedValue(Context context) {
        return getDevicePrefs(context).getString(KEY_PREFERENCE, "");
    }

    public static void handlePreferenceUi(ListPreference preference) {
        Context context = preference.getContext();
        preference.setValue(getAppliedValue(context));
        preference.setOnPreferenceChangeListener(new PreferenceChangeHandler(context));
    }

    private static class ResourcesOverride extends Resources {

        private final int mOverrideId;
        private final String mOverrideValue;

        @SuppressWarnings("deprecation")
        public ResourcesOverride(Resources parent, int overrideId, String overrideValue) {
            super(parent.getAssets(), parent.getDisplayMetrics(), parent.getConfiguration());
            mOverrideId = overrideId;
            mOverrideValue = overrideValue;
        }

        @NonNull
        @Override
        public String getString(int id) throws NotFoundException {
            if (id == mOverrideId) {
                return mOverrideValue;
            }
            return super.getString(id);
        }
    }

    private static class PreferenceChangeHandler implements OnPreferenceChangeListener {

        private final Context mContext;

        private PreferenceChangeHandler(Context context) {
            mContext = context;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            String newValue = (String) o;
            if (!getAppliedValue(mContext).equals(newValue)) {
                // Value has changed
                ProgressDialog.show(mContext,
                        null /* title */,
                        mContext.getString(R.string.icon_shape_override_progress),
                        true /* indeterminate */,
                        false /* cancelable */);
                RestartLauncherHandler handler = new RestartLauncherHandler(mContext);
                handler.setCallback(() -> {
                    // Synchronously write the preference.
                    getDevicePrefs(mContext).edit().putString(KEY_PREFERENCE, newValue).commit();
                    // Clear the icon cache.
                    LauncherAppState.getInstance(mContext).getIconCache().clear();
                });

                AppModule.provideLooper().execute(handler);
            }
            return false;
        }
    }
}
