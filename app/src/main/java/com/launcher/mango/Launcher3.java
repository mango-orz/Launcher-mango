package com.launcher.mango;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.launcher.mango.model.BoardModel;
import com.launcher.mango.util.ComponentKeyMapper;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tic
 * created on 18-9-14
 */
public class Launcher3 extends Launcher {

    private BoardModel mBoardModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLauncherCallbacks(new MangoLauncherCallbacks());
        super.onCreate(savedInstanceState);
    }

    class MangoLauncherCallbacks implements LauncherCallbacks {

        @Override
        public void preOnCreate() {
            mBoardModel = new BoardModel();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {

        }

        @Override
        public void preOnResume() {

        }

        @Override
        public void onResume() {

        }

        @Override
        public void onStart() {

        }

        @Override
        public void onStop() {

        }

        @Override
        public void onPause() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public void onSaveInstanceState(Bundle outState) {

        }

        @Override
        public void onPostCreate(Bundle savedInstanceState) {

        }

        @Override
        public void onNewIntent(Intent intent) {

        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {

        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        }

        @Override
        public void onWindowFocusChanged(boolean hasFocus) {

        }

        @Override
        public void onAttachedToWindow() {

        }

        @Override
        public void onDetachedFromWindow() {

        }

        @Override
        public boolean onPrepareOptionsMenu(Menu menu) {
            return false;
        }

        @Override
        public void dump(String prefix, FileDescriptor fd, PrintWriter w, String[] args) {

        }

        @Override
        public void onHomeIntent() {

        }

        @Override
        public boolean handleBackPressed() {
            return false;
        }

        @Override
        public void onTrimMemory(int level) {

        }

        @Override
        public void onLauncherProviderChange() {

        }

        @Override
        public void finishBindingItems(boolean upgradePath) {

        }

        @Override
        public void bindAllApplications(ArrayList<AppInfo> apps) {

        }

        @Override
        public void onInteractionBegin() {

        }

        @Override
        public void onInteractionEnd() {

        }

        @Override
        public void onWorkspaceLockedChanged() {

        }

        @Override
        public boolean startSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData) {
            return false;
        }

        @Override
        public boolean hasCustomContentToLeft() {
            return true;
        }

        @Override
        public void populateCustomContentContainer() {
            mBoardModel.inflateCustomContent(Launcher3.this);
        }

        @Override
        public View getQsbBar() {
            return null;
        }

        @Override
        public Bundle getAdditionalSearchWidgetOptions() {
            return null;
        }

        @Override
        public boolean shouldMoveToDefaultScreenOnHomeIntent() {
            return true;
        }

        @Override
        public boolean hasSettings() {
            return true;
        }

        @Override
        public List<ComponentKeyMapper<AppInfo>> getPredictedApps() {
            return null;
        }

        @Override
        public int getSearchBarHeight() {
            return 0;
        }
    }
}
