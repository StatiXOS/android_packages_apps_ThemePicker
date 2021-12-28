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

import static com.android.customization.model.ResourceConstants.OVERLAY_CATEGORY_ICON_ANDROID;
import static com.android.customization.model.ResourceConstants.OVERLAY_CATEGORY_ICON_SETTINGS;
import static com.android.customization.model.ResourceConstants.OVERLAY_CATEGORY_ICON_SYSUI;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.android.wallpaper.R;
import com.android.wallpaper.util.ResourceUtils;

import com.android.customization.model.CustomizationManager;
import com.android.customization.model.CustomizationOption;
import com.android.customization.model.ResourceConstants;
import com.android.customization.model.theme.OverlayManagerCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IconPackOption implements CustomizationOption<IconPackOption> {

    public static final int THUMBNAIL_ICON_POSITION = 0;
    private static int[] mIconIds = {
            R.id.preview_icon_0, R.id.preview_icon_1, R.id.preview_icon_2, R.id.preview_icon_3,
            R.id.preview_icon_4, R.id.preview_icon_5
    };

    private List<Drawable> mIcons = new ArrayList<>();
    private String mTitle;
    private final Map<String, String> mOverlayPackageNames = new HashMap<>();

    public IconPackOption(String title) {
        mTitle = title;
    }

    @Override
    public void bindThumbnailTile(View view) {
        Resources res = view.getContext().getResources();
        Drawable icon = mIcons.get(THUMBNAIL_ICON_POSITION)
                .getConstantState().newDrawable().mutate();
        icon.setTint(ResourceUtils.getColorAttr(
                 view.getContext(), android.R.attr.textColorSecondary));
        ((ImageView) view.findViewById(R.id.option_icon)).setImageDrawable(
                 icon);
        view.setContentDescription(mTitle);
    }

    @Override
    public boolean isActive(CustomizationManager<IconPackOption> manager) {
        IconPackManager iconManager = (IconPackManager) manager;
        OverlayManagerCompat overlayManager = iconManager.getOverlayManager();
        if (mOverlayPackageNames.isEmpty()) {
            return overlayManager.getEnabledPackageName("com.android.systemui", OVERLAY_CATEGORY_ICON_SYSUI) == null &&
                   overlayManager.getEnabledPackageName("com.android.settings", OVERLAY_CATEGORY_ICON_SETTINGS) == null &&
                   overlayManager.getEnabledPackageName("android", OVERLAY_CATEGORY_ICON_ANDROID) == null;
        }
        for (Map.Entry<String, String> overlayEntry : mOverlayPackageNames.entrySet()) {
            if(!overlayManager.getEnabledPackageName(overlayEntry.getKey(), OVERLAY_CATEGORY_ICON_ANDROID).equals(overlayEntry.getKey())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.theme_icon_option;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    public void addIcon(Drawable previewIcon) {
        mIcons.add(previewIcon);
    }

    public void addOverlayPackage(String category, String overlayPackage) {
        mOverlayPackageNames.put(category, overlayPackage);
    }

    public Map<String, String> getOverlayPackages() {
        return mOverlayPackageNames;
    }

    /**
     * @return whether this icon option has overlays and previews for all the required packages
     */
    public boolean isValid(Context context) {
        return mOverlayPackageNames.keySet().size() ==
                ResourceConstants.getPackagesToOverlay(context).length;
    }
}
