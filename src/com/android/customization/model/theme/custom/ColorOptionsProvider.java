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
package com.android.customization.model.theme.custom;

import static com.android.customization.model.ResourceConstants.ACCENT_COLOR_DARK_NAME;
import static com.android.customization.model.ResourceConstants.ACCENT_COLOR_LIGHT_NAME;
import static com.android.customization.model.ResourceConstants.ANDROID_PACKAGE;
import static com.android.customization.model.ResourceConstants.ICONS_FOR_PREVIEW;
import static com.android.customization.model.ResourceConstants.OVERLAY_CATEGORY_ANDROID_THEME;
import static com.android.customization.model.ResourceConstants.OVERLAY_CATEGORY_ICON_ANDROID;
import static com.android.customization.model.ResourceConstants.OVERLAY_CATEGORY_SHAPE;
import static com.android.customization.model.ResourceConstants.PATH_SIZE;
import static com.android.customization.model.ResourceConstants.SYSUI_PACKAGE;
import static com.android.customization.model.theme.custom.ThemeComponentOption.ColorOption.COLOR_TILES_ICON_IDS;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AccentUtils;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.graphics.PathParser;
import androidx.preference.Preference;

import com.android.customization.model.ResourceConstants;
import com.android.customization.model.theme.OverlayManagerCompat;
import com.android.customization.model.theme.custom.ThemeComponentOption.ColorOption;
import com.android.wallpaper.R;

import java.util.ArrayList;
import java.util.List;

import com.statix.support.preferences.CustomSeekBarPreference;

/**
 * Implementation of {@link ThemeComponentOptionProvider} that reads {@link ColorOption}s from
 * icon overlays.
 */
public class ColorOptionsProvider extends ThemeComponentOptionProvider<ColorOption> implements Preference.onPreferenceChangeListener {

    private static final String TAG = "ColorOptionsProvider";
    private final CustomThemeManager mCustomThemeManager;
    private final String mDefaultThemePackage;
    private CustomSeekBarPreference hue;
    private CustomSeekBarPreference saturation;
    private CustomSeekBarPreference value;

    public ColorOptionsProvider(Context context, OverlayManagerCompat manager,
            CustomThemeManager customThemeManager) {
        super(context, manager, OVERLAY_CATEGORY_COLOR);
        mCustomThemeManager = customThemeManager;
        // System color is set with a static overlay for android.theme category, so let's try to
        // find that first, and if that's not present, we'll default to System resources.
        // (see #addDefault())
        List<String> themePackages = manager.getOverlayPackagesForCategory(
                OVERLAY_CATEGORY_ANDROID_THEME, UserHandle.myUserId(), ANDROID_PACKAGE);
        mDefaultThemePackage = themePackages.isEmpty() ? null : themePackages.get(0);
    }

    @Override
    protected void loadOptions() {
        List<Drawable> previewIcons = new ArrayList<>();
        String iconPackage =
                mCustomThemeManager.getOverlayPackages().get(OVERLAY_CATEGORY_ICON_ANDROID);
        if (TextUtils.isEmpty(iconPackage)) {
            iconPackage = ANDROID_PACKAGE;
        }
        for (String iconName : ICONS_FOR_PREVIEW) {
            try {
                previewIcons.add(loadIconPreviewDrawable(iconName, iconPackage));
            } catch (NameNotFoundException | NotFoundException e) {
                Log.w(TAG, String.format("Couldn't load icon in %s for color preview, will skip it",
                        iconPackage), e);
            }
        }
        String shapePackage = mCustomThemeManager.getOverlayPackages().get(OVERLAY_CATEGORY_SHAPE);
        if (TextUtils.isEmpty(shapePackage)) {
            shapePackage = ANDROID_PACKAGE;
        }
        Drawable shape = loadShape(shapePackage);
        // create 3 HSV sliders
        // hue
        hue = new CustomSeekBarPreference(mContext);
        // saturation
        saturation = new CustomSeekBarPreference(mContext);
        // value
        value = new CustomSeekBarPreference(mContext);
        // set slider values
        setSliders(previewIcons, shape);
        // add sliders to screen
        // customize ColorOption class
        ColorOption option = new ColorOption("Custom" /* Label */);
        option.setPreviewIcons(previewIcons);
        option.setShapeDrawable(shape);
        mBarOptions.add(option); // TODO: look into this
    }

    private void setSliders(List<Drawable> previewIcons, Drawable shape) {
        hue.setMin(0);
        saturation.setMin(0);
        value.setMin(0);
        hue.setMax(360);
        saturation.setMax(100);
        value.setMax(100);
        int lightColor;
        Resources system = Resources.getSystem();
        try {
            Resources r = getOverlayResources(mDefaultThemePackage);
            lightColor = r.getColor(
                    r.getIdentifier(ACCENT_COLOR_LIGHT_NAME, "color", mDefaultThemePackage),
                    null);
        } catch (NotFoundException | NameNotFoundException e) {
            Log.d(TAG, "Didn't find default color, will use system option", e);
            lightColor = system.getColor(
                    system.getIdentifier(ACCENT_COLOR_LIGHT_NAME, "color", ANDROID_PACKAGE), null);
        }
        int useLightColor = AccentUtils.getLightAccentColor(lightColor);
        float[] outputs = new float[3];
        Color.colorToHSV(useLightColor, outputs);
        hue.setValue((int) outputs[0]);
        saturation.setValue((int) outputs[1]);
        value.setValue((int) outputs[2]);
    }

    private Drawable loadShape(String packageName) {
        String path = null;
        try {
            Resources r = getOverlayResources(packageName);

            path = ResourceConstants.getIconMask(r, packageName);
        } catch (NameNotFoundException e) {
            Log.d(TAG, String.format("Couldn't load shape icon for %s, skipping.", packageName), e);
        }
        ShapeDrawable shapeDrawable = null;
        if (!TextUtils.isEmpty(path)) {
            PathShape shape = new PathShape(PathParser.createPathFromPathData(path),
                    PATH_SIZE, PATH_SIZE);
            shapeDrawable = new ShapeDrawable(shape);
            shapeDrawable.setIntrinsicHeight((int) PATH_SIZE);
            shapeDrawable.setIntrinsicWidth((int) PATH_SIZE);
        }
        return shapeDrawable;
    }

}
