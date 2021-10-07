/*
 * Nextcloud Android client application
 *
 * @author Tobias Kaminsky
 * @author Andy Scherzinger
 * Copyright (C) 2017 Tobias Kaminsky
 * Copyright (C) 2017 Nextcloud GmbH
 * Copyright (C) 2018 Andy Scherzinger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.owncloud.android.utils.theme;

import android.content.Context;

import com.nextcloud.client.account.User;
import com.nextcloud.client.account.UserAccountManagerImpl;
import com.owncloud.android.datamodel.FileDataStorageManager;
import com.owncloud.android.lib.resources.status.OCCapability;

/**
 * Utility class with methods for client side button theming.
 */
public final class CapabilityUtils {
    public static OCCapability getCapability(Context context) {
        User user = null;
        if (context != null) {
            // TODO: refactor when dark theme work is completed
            user = UserAccountManagerImpl.fromContext(context).getUser();
        }

        if (user != null) {
            FileDataStorageManager storageManager = new FileDataStorageManager(user, context.getContentResolver());
            return storageManager.getCapability(user.getAccountName());
        } else {
            return new OCCapability();
        }
    }
}
