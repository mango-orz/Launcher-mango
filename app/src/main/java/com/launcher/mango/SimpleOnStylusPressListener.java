package com.launcher.mango;

import android.view.MotionEvent;
import android.view.View;

import com.launcher.mango.StylusEventHelper.StylusButtonListener;

/**
 * Simple listener that performs a long click on the view after a stylus button press.
 */
public class SimpleOnStylusPressListener implements StylusButtonListener {
    private View mView;

    public SimpleOnStylusPressListener(View view) {
        mView = view;
    }

    public boolean onPressed(MotionEvent event) {
        return mView.isLongClickable() && mView.performLongClick();
    }

    public boolean onReleased(MotionEvent event) {
        return false;
    }
}