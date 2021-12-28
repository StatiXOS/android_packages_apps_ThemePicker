/*
 * Copyright (C) 2019 The Android Open Source Project
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
package com.android.customization.model.iconpack;

import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.customization.model.CustomizationManager;
import com.android.customization.model.theme.OverlayManagerCompat;

import java.util.Map;
import java.util.List;

public class IconPackManager implements CustomizationManager<IconPackOption> {

    private static IconPackManager sIconPackOptionManager;
    private IconPackOption mActiveOption;
    private OverlayManagerCompat mOverlayManager;
    private IconPackOptionProvider mProvider;
    private static final String TAG = "IconPackManager";
    private static final String KEY_STATE_CURRENT_SELECTION = "IconPackManager.currentSelection";

    IconPackManager(OverlayManagerCompat overlayManager, IconPackOptionProvider provider) {
        mProvider = provider;
        mOverlayManager = overlayManager;
    }

    @Override
    public boolean isAvailable() {
        return mOverlayManager.isAvailable();
    }

    @Override
    public void apply(IconPackOption option, @Nullable Callback callback) {
        if (option.getTitle().equals("Default")) {
            if (mActiveOption.getTitle().equals("Default")) return;
            mActiveOption.getOverlayPackages().forEach((category, overlay) -> mOverlayManager.disableOverlay(overlay, UserHandle.myUserId()));
        } else {
            option.getOverlayPackages().forEach((category, overlay) -> mOverlayManager.setEnabledExclusiveInCategory(overlay, UserHandle.myUserId()));
        }
        if (callback != null) {
            callback.onSuccess();
        }
        mActiveOption = option;
    }

    @Override
    public void fetchOptions(OptionsFetchedListener<IconPackOption> callback, boolean reload) {
        List<IconPackOption> options = mProvider.getOptions();
        for (IconPackOption option : options) {
            if (option.isActive(this)) {
                mActiveOption = option;
                break;
            }
        }
        callback.onOptionsLoaded(options);
    }

    public OverlayManagerCompat getOverlayManager() {
        return mOverlayManager;
    }

    public static IconPackManager getInstance(Context context, OverlayManagerCompat overlayManager) {
        if (sIconPackOptionManager == null) {
            Context applicationContext = context.getApplicationContext();
            sIconPackOptionManager = new IconPackManager(overlayManager, new IconPackOptionProvider(applicationContext, overlayManager));
        }
        return sIconPackOptionManager;
    }

}
