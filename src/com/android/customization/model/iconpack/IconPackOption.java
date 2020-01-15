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
package com.android.customization.model.grid;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.android.customization.model.CustomizationManager;
import com.android.customization.model.CustomizationOption;
import com.android.wallpaper.R;

/**
 * Represents a icon pack option available in the current launcher.
 */
public class IconPackOption implements CustomizationOption<IconPackOption> {

    private final String mTitle;
    private final boolean mIsCurrent;
    public final String name;
    public final Uri previewImageUri;

    public IconPackOption(String title, String name, Drawable icon, boolean isCurrent,
            Uri previewImageUri, String iconShapePath) {
        mTitle = title;
        mIsCurrent = isCurrent;
        mTileDrawable = new GridTileDrawable(rows, cols, iconShapePath);
        this.name = name;
        this.icon = icon;
        this.previewImageUri = previewImageUri;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public void bindThumbnailTile(View view) {
        Context context = view.getContext();
    }

    @Override
    public boolean isActive(CustomizationManager<IconPackOption> manager) {
        return mIsCurrent;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.icon_pack_option;
    }
}
