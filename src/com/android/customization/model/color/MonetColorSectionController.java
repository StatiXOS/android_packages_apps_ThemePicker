/*
 * Copyright (C) 2021 The Android Open Source Project
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
package com.android.customization.model.color;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.customization.picker.color.MonetColorFragment;
import com.android.customization.picker.color.MonetColorSectionView;

import com.android.wallpaper.R;
import com.android.wallpaper.model.CustomizationSectionController;
import com.android.wallpaper.util.LaunchUtils;

import java.util.List;

/** A {@link CustomizationSectionController} for system icons. */

public class MonetColorSectionController implements CustomizationSectionController<MonetColorSectionView> {

    private static final String TAG = "MonetColorSectionController";

    private final CustomizationSectionNavigationController mSectionNavigationController;

    public MonetColorSectionController(CustomizationSectionNavigationController sectionNavigationController) {
        mSectionNavigationController = sectionNavigationController;
    }

    @Override
    public boolean isAvailable(Context context) {
        return ColorUtils.isMonetEnabled(context);
    }

    @Override
    public MonetColorSectionView createView(Context context) {
        MonetColorSectionView monetColorSectionView = (MonetColorSectionView) LayoutInflater.from(context)
                .inflate(R.layout.monet_section_view, /* root= */ null);

        monetColorSectionView.setOnClickListener(v -> mSectionNavigationController.navigateTo(
                MonetColorFragment.newInstance(context.getString(R.string.monet_color_section_title))));

        return monetColorSectionView;
    }

}
