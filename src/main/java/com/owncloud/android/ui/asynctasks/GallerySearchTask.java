/*
 * Nextcloud Android client application
 *
 * @author Tobias Kaminsky
 * Copyright (C) 2019 Tobias Kaminsky
 * Copyright (C) 2019 Nextcloud GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.owncloud.android.ui.asynctasks;

import android.os.AsyncTask;

import com.nextcloud.client.account.User;
import com.owncloud.android.datamodel.FileDataStorageManager;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.common.utils.Log_OC;
import com.owncloud.android.lib.resources.files.SearchRemoteOperation;
import com.owncloud.android.lib.resources.files.model.RemoteFile;
import com.owncloud.android.ui.adapter.OCFileListAdapter;
import com.owncloud.android.ui.fragment.ExtendedListFragment;
import com.owncloud.android.ui.fragment.GalleryFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class GallerySearchTask extends AsyncTask<Void, Void, RemoteOperationResult> {

    private int columnCount;
    private User user;
    private WeakReference<GalleryFragment> photoFragmentWeakReference;
    private SearchRemoteOperation searchRemoteOperation;
    private FileDataStorageManager storageManager;
    private int limit;
    private long lastYearTimestamp = 0;

    // CS427 Issue link: https://github.com/nextcloud/android/issues/8766
    /**
     * GallerySearchTask constructor, which now contains a timestamp parameter
     * which signifies whether this is an 'On this day' search task and also the
     * day that should be searched.
     */
    public GallerySearchTask(int columnsCount,
                             GalleryFragment photoFragment,
                             User user,
                             SearchRemoteOperation searchRemoteOperation,
                             FileDataStorageManager storageManager,
                             long lastYearTimestamp) {
        this.columnCount = columnsCount;
        this.user = user;
        this.photoFragmentWeakReference = new WeakReference<>(photoFragment);
        this.searchRemoteOperation = searchRemoteOperation;
        this.storageManager = storageManager;
        this.lastYearTimestamp = lastYearTimestamp;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (photoFragmentWeakReference.get() == null) {
            return;
        }
        GalleryFragment photoFragment = photoFragmentWeakReference.get();
        photoFragment.setPhotoSearchQueryRunning(true);
    }

    // CS427 Issue link: https://github.com/nextcloud/android/issues/8766
    @Override
    protected RemoteOperationResult doInBackground(Void... voids) {
        if (photoFragmentWeakReference.get() == null) {
            return new RemoteOperationResult(new Exception("Photo fragment is null"));
        }
        GalleryFragment photoFragment = photoFragmentWeakReference.get();
        OCFileListAdapter adapter = photoFragment.getAdapter();

        if (isCancelled()) {
            return new RemoteOperationResult(new Exception("Cancelled"));
        } else {
            limit = 15 * columnCount;

            long timestamp = -1;
            if (adapter.getLastTimestamp() > 0) {
                timestamp = adapter.getLastTimestamp();
            } else if (lastYearTimestamp != 0) {
                timestamp = lastYearTimestamp;
            }

            searchRemoteOperation.setLimit(limit);
            searchRemoteOperation.setTimestamp(timestamp);

            if (photoFragment.getContext() != null) {
                return searchRemoteOperation.execute(user.toPlatformAccount(), photoFragment.getContext());
            } else {
                return new RemoteOperationResult(new IllegalStateException("No context available"));
            }
        }
    }

    // CS427 Issue link: https://github.com/nextcloud/android/issues/8766
    @Override
    protected void onPostExecute(RemoteOperationResult result) {
        if (photoFragmentWeakReference.get() != null) {
            GalleryFragment photoFragment = photoFragmentWeakReference.get();

            if (result.isSuccess() && result.getData() != null && !isCancelled()) {

                if (lastYearTimestamp != 0) {
                    ArrayList<Object> resultFileList = new ArrayList<>();
                    long tempCutoff = ((lastYearTimestamp * 1000) - 86400000);
                    for (Object object : result.getData()) {
                        if (((RemoteFile)object).getModifiedTimestamp() >= tempCutoff) {
                            resultFileList.add(object);
                        }
                    }
                    result.setData(resultFileList);
                }

                if (result.getData() == null || result.getData().size() == 0) {
                    photoFragment.setSearchDidNotFindNewPhotos(true);
                } else {
                    OCFileListAdapter adapter = photoFragment.getAdapter();

                    if (result.getData().size() < limit) {
                        // stop loading spinner
                        photoFragment.setSearchDidNotFindNewPhotos(true);
                    }

                    adapter.setData(result.getData(),
                                    ExtendedListFragment.SearchType.GALLERY_SEARCH,
                                    storageManager,
                                    null,
                                    false);
                    adapter.notifyDataSetChanged();
                    Log_OC.d(this, "Search: count: " + result.getData().size() + " total: " + adapter.getFiles().size());
                }
            }

            photoFragment.setLoading(false);

            if (!result.isSuccess() && !isCancelled()) {
                photoFragment.setEmptyListMessage(ExtendedListFragment.SearchType.GALLERY_SEARCH);
            }

            photoFragment.setPhotoSearchQueryRunning(false);
        }
    }

    /**
     * Returns whether this gallery search task is used for 'On this day' functionality.
     * This will be true only if lastYearTimestamp was set to a non-zero value.
     */
    public boolean isOnThisDaySearchTask() {
        return lastYearTimestamp != 0;
    }
}
