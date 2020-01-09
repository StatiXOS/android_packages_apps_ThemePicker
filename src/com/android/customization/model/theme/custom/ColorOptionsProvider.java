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
public class ColorOptionsProvider extends ThemeComponentOptionProvider<ColorOption> implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "ColorOptionsProvider";
    private final CustomThemeManager mCustomThemeManager;
    private final String mDefaultThemePackage;
    private CustomSeekBarPreference mHue;
    private CustomSeekBarPreference mSaturation;
    private CustomSeekBarPreference mValue;
    private List<Drawable> mPreviewIcons;

    public ColorOptionsProvider(Context context, OverlayManagerCompat manager,
            CustomThemeManager customThemeManager) {
        super(context, manager, "");
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
        mPreviewIcons = new ArrayList<>();
        String iconPackage =
                mCustomThemeManager.getOverlayPackages().get(OVERLAY_CATEGORY_ICON_ANDROID);
        if (TextUtils.isEmpty(iconPackage)) {
            iconPackage = ANDROID_PACKAGE;
        }
        for (String iconName : ICONS_FOR_PREVIEW) {
            try {
                mPreviewIcons.add(loadIconPreviewDrawable(iconName, iconPackage));
            } catch (NameNotFoundException | NotFoundException e) {
                Log.w(TAG, String.format("Couldn't load icon in %s for color preview, will skip it",
                        iconPackage), e);
            }
        }
        // create 3 HSV sliders
        mHue = new CustomSeekBarPreference(mContext);
        mSaturation = new CustomSeekBarPreference(mContext);
        mValue = new CustomSeekBarPreference(mContext);
        // set slider mValues
        setSliders(mPreviewIcons);
    }

    private float[] getSliders() {
        float[] ret = new float[3];
        ret[0] = mHue.getProgress();
        ret[1] = mSaturation.getProgress()/100;
        ret[2] = mValue.getProgress()/100;
        return ret;
    }
    private void setSliders(List<Drawable> previewIcons) {
        mHue.setMin(0);
        mSaturation.setMin(0);
        mValue.setMin(0);
        mHue.setMax(360);
        mSaturation.setMax(100);
        mValue.setMax(100);
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
        mHue.setValue((int) outputs[0]);
        mSaturation.setValue((int) outputs[1]);
        mValue.setValue((int) outputs[2]);
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

    private Drawable loadIconPreviewDrawable(String drawableName, String packageName)
            throws NameNotFoundException, NotFoundException {

        Resources overlayRes = getOverlayResources(packageName);
        return overlayRes.getDrawable(
                overlayRes.getIdentifier(drawableName, "drawable", packageName), null);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        float[] customLight = getSliders();
        float[] customDark = getSliders();
        if (preference == mHue) {
            int newHue = (Integer) objValue;
            customLight[0] = newHue;
            customDark[0] = newHue;
        } else if (preference == mSaturation) {
            int newSat = (Integer) objValue;
            customLight[1] = newSat;
            customDark[1] = (float) Math.abs(newSat - customDark[1] - 0.12);
        } else if (preference == mValue) {
            int newVal = (Integer) objValue;
            customLight[2] = newVal;
            customDark[2] = newVal;
        } else {
            return false;
        }
        int lightColor = Color.HSVToColor(customLight);
        int darkColor = Color.HSVToColor(customDark);
        // add sliders to screen
        ColorOption option = new ColorOption("Custom" /* Label */, lightColor, darkColor);
        String shapePackage = mCustomThemeManager.getOverlayPackages().get(OVERLAY_CATEGORY_SHAPE);
        if (TextUtils.isEmpty(shapePackage)) {
            shapePackage = ANDROID_PACKAGE;
        }
        Drawable shape = loadShape(shapePackage);
        option.setPreviewIcons(mPreviewIcons);
        option.setShapeDrawable(shape);
        return true;
    }
}
