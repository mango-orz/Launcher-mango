package com.launcher.mango.model;

import android.view.View;

import com.launcher.mango.Launcher;
import com.launcher.mango.Launcher3;
import com.launcher.mango.R;

/**
 * model for adding dashboard card
 *
 * @author tic
 * created on 18-9-14
 */
public class BoardModel {

    public void inflateCustomContent(Launcher3 launcher3) {
        View customContent = launcher3.getLayoutInflater().inflate(R.layout.view_board, launcher3.getDragLayer(), false);
        launcher3.addToCustomContentPage(customContent, getCallback(), "Hi-dashboard");
    }

    private Launcher.CustomContentCallbacks getCallback() {
        return new Launcher.CustomContentCallbacks() {

            @Override
            public void onShow(boolean fromResume) {

            }

            @Override
            public void onHide() {

            }

            @Override
            public void onScrollProgressChanged(float progress) {

            }

            @Override
            public boolean isScrollingAllowed() {
                return true;
            }
        };
    }

    ;
}
