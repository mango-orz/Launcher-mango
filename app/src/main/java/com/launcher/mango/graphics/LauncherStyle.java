package com.launcher.mango.graphics;

import android.app.ProgressDialog;
import android.content.Context;
import android.preference.ListPreference;
import android.preference.Preference;

import com.launcher.mango.AppModule;
import com.launcher.mango.R;
import com.launcher.mango.util.RestartLauncherHandler;

import static com.launcher.mango.Utilities.getDevicePrefs;

/**
 * Launcher Style
 *
 * @author tic
 * created on 18-9-17
 */
public class LauncherStyle {

    private static final String TAG = LauncherStyle.class.getSimpleName();

    public static final String KEY_PREFERENCE = "pref_launcher_style";
    private static final String STYLE_DRAWER = "drawer";

    public static boolean isSupported(Context context) {
        return true;
    }

    public static void handlePreferenceUi(ListPreference preference) {
        Context context = preference.getContext();
        preference.setValue(getAppliedValue(context));
        preference.setOnPreferenceChangeListener(new LauncherStyle.PreferenceChangeHandler(context));
    }

    /**
     * drawer style or standard
     */
    public static boolean isDrawer(Context context) {
        return getAppliedValue(context).equals(STYLE_DRAWER);
    }

    private static String getAppliedValue(Context context) {
        return getDevicePrefs(context).getString(KEY_PREFERENCE, STYLE_DRAWER);
    }

    private static class PreferenceChangeHandler implements Preference.OnPreferenceChangeListener {
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
                        mContext.getString(R.string.launcher_style_progress),
                        true /* indeterminate */,
                        false /* cancelable */);
                RestartLauncherHandler handler = new RestartLauncherHandler(mContext);
                handler.setCallback(() -> {
                    // Synchronously write the preference.
                    getDevicePrefs(mContext).edit().putString(KEY_PREFERENCE, newValue).commit();
                });

                AppModule.provideLooper().execute(handler);
            }
            return false;
        }
    }

}
