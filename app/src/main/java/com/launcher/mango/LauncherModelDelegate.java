package com.launcher.mango;

import android.content.Context;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.Pair;

import com.google.common.base.Preconditions;
import com.launcher.mango.model.BgDataModel;
import com.launcher.mango.util.LongArrayMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.launcher.mango.Workspace.FIRST_SCREEN_ID;

/**
 * @author tic
 * created on 18-9-18
 */
public class LauncherModelDelegate {

    private final InvariantDeviceProfile mProfile;
    private final Context mContext;

    public LauncherModelDelegate(Context context) {
        this.mContext = context;
        this.mProfile = LauncherAppState.getInstance(context).getInvariantDeviceProfile();
    }

    /**
     * sort workspace items by name before adding to workspace
     */
    public ArrayList<Long> sortWorkspaceItemsSpatially(List<ItemInfo> added, BgDataModel model) {
        Preconditions.checkNotNull(added);

        if (added.isEmpty()) {
            return null;
        }
        ArrayList<Long> workspaceScreens = new ArrayList<>(model.workspaceScreens);
        LongArrayMap<ItemInfo> copy = model.itemsIdMap.clone();
        for (ItemInfo info : added) {
            // init container
            info.container = info.container == -1 ?
                    LauncherSettings.Favorites.CONTAINER_DESKTOP : info.container;
            Pair<Long, int[]> pair = findSpaceForItem(workspaceScreens, copy, info.spanX, info.spanY);

            if (pair != null) {
                info.screenId = pair.first;
                info.cellX = pair.second[0];
                info.cellY = pair.second[1];
            }
            // update db
            ShortcutInfo si;
            if (info instanceof AppInfo) {
                si = ((AppInfo) info).makeShortcut();
            } else {
                si = (ShortcutInfo) info;
            }
            LauncherModel.updateFavorites(mContext, si);
        }
        if (!workspaceScreens.isEmpty()) {
            LauncherModel.updateWorkspaceScreenOrder(mContext, workspaceScreens);
            Log.d("LauncherModel", "==> " + Arrays.toString(workspaceScreens.toArray()));
        }
        return workspaceScreens;
    }

    private Pair<Long, int[]> findSpaceForItem(ArrayList<Long> workspaceScreens,
                                               LongArrayMap<ItemInfo> sBgItemsIdMap,
                                               int spanX,
                                               int spanY) {
        LongSparseArray<ArrayList<ItemInfo>> screenItems = new LongSparseArray<>();

        for (ItemInfo info : sBgItemsIdMap) {
            if (info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
                ArrayList<ItemInfo> items = screenItems.get(info.screenId);
                if (items == null) {
                    items = new ArrayList<>();
                    screenItems.put(info.screenId, items);
                }
                items.add(info);
            }
        }

        // Find appropriate space for the item.
        long screenId = 0;
        int[] cordinates = new int[2];
        boolean found = false;

        int screenCount = workspaceScreens.size();
        // Search on any of the screens starting from the zero screen, but homeScreen.
        for (int screen = 0; screen < screenCount; screen++) {
            screenId = workspaceScreens.get(screen);
            // TODO ignore Default screen, right now FIRST_SCREEN_ID is ok
            if (screenId == FIRST_SCREEN_ID) {
                continue;
            }

            if (findNextAvailableIconSpaceInScreen(mProfile,
                    screenItems.get(screenId), cordinates, spanX, spanY)) {
                // We found a space for it
                found = true;
                break;
            }
        }

        if (!found) {
            // Still no position found. Add a new screen to the end.
            screenId = LauncherSettings.Settings.call(mContext.getContentResolver(),
                    LauncherSettings.Settings.METHOD_NEW_SCREEN_ID)
                    .getLong(LauncherSettings.Settings.EXTRA_VALUE);

            // Save the screen id for binding in the workspace
            workspaceScreens.add(screenId);

            // If we still can't find an empty space, then God help us all!!!
            if (!findNextAvailableIconSpaceInScreen(mProfile,
                    screenItems.get(screenId), cordinates, spanX, spanY)) {
                throw new RuntimeException("Can't find space to add the item");
            }
        }
        return Pair.create(screenId, cordinates);
    }

    private boolean findNextAvailableIconSpaceInScreen(InvariantDeviceProfile profile,
                                                       ArrayList<ItemInfo> occupiedPos,
                                                       int[] xy, int spanX, int spanY) {
        final int xCount = profile.numColumns;
        final int yCount = profile.numRows;
        boolean[][] occupied = new boolean[xCount][yCount];
        if (occupiedPos != null) {
            for (ItemInfo r : occupiedPos) {
                int right = r.cellX + r.spanX;
                int bottom = r.cellY + r.spanY;
                for (int x = r.cellX; 0 <= x && x < right && x < xCount; x++) {
                    for (int y = r.cellY; 0 <= y && y < bottom && y < yCount; y++) {
                        occupied[x][y] = true;
                    }
                }
            }
        }
        return findVacantCell(xy, spanX, spanY, xCount, yCount, occupied);
    }

    /**
     * Find the first vacant cell, if there is one.
     *
     * @param vacant Holds the x and y coordinate of the vacant cell
     * @param spanX  Horizontal cell span.
     * @param spanY  Vertical cell span.
     * @return true if a vacant cell was found
     */
    private boolean findVacantCell(int[] vacant, int spanX, int spanY,
                                   int xCount, int yCount, boolean[][] occupied) {
        for (int y = 0; (y + spanY) <= yCount; y++) {
            for (int x = 0; (x + spanX) <= xCount; x++) {
                boolean available = !occupied[x][y];
                out:
                for (int i = x; i < x + spanX; i++) {
                    for (int j = y; j < y + spanY; j++) {
                        available = available && !occupied[i][j];
                        if (!available) {
                            break out;
                        }
                    }
                }

                if (available) {
                    vacant[0] = x;
                    vacant[1] = y;
                    return true;
                }
            }
        }
        return false;
    }
}
