package com.android.customization.model.color;

import android.app.WallpaperColors;
import android.content.res.ColorStateList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import com.android.customization.model.ResourcesApkProvider;
import com.android.customization.model.color.ColorSeedOption;

import com.android.internal.graphics.cam.Cam;

import com.android.wallpaper.compat.WallpaperManagerCompatV16;
import com.android.wallpaper.module.InjectorProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ColorProvider extends ResourcesApkProvider {
    @Nullable
    public List<ColorOption> mColorOptions;
    @NonNull
    private WallpaperColors mHomeWallpaperColors;
    @Nullable
    public WallpaperColors lockWallpaperColors;

    public ColorProvider(Context context, String stubPackageName) {
        super(context, stubPackageName);
    }

    public void setWallpaperColors(WallpaperColors wpc, boolean isLock) {
        if (isLock) {
            mLockWallpaperColors = wpc;
        } else {
            mHomeWallpaperColors = wpc;
        }
    }

    private void buildPresetColors() {
        String[] colorBundles = getItemsFromStub("color_bundles");
        for (int i = 0; i < colorBundles.length; i++) {
            String bundle = colorBundles[i];
            @ColorInt
            int lightColor = getItemColorFromStub("color_primary_" + bundle);
            @ColorInt
            int darkColor = getItemColorFromStub("color_secondary_" + bundle)
            String light = ColorUtils.toColorString(lightColor);
            String dark = ColorUtils.toColorString(darkColor);
            HashMap<String, String> settingsMap = new HashMap<String, String>();
            boolean isDark = (mStubApkResources.getConfiguration().uiMode & UI_MODE_NIGHT_MASK) == UI_MODE_NIGHT_YES;
            settingsMap.put("android.theme.customization.system_palette", isDark ? dark : light);
            settingsMap.put("android.theme.customization.accent_color", isDark ? dark : light);
            mColorOptions.add(new ColorBundle(bundle, settingsMap, false, i, new ColorBundle.PreviewInfo(lightColor, darkColor));
        }
    }

    private void loadSeedColors() {
        if (mHomeWallpaperColors != null) {
            ArrayList arrayList = new ArrayList();
            int seedsFromHome = mLockWallpaperColors == null ? 4 : 2;
            if (mLockWallpaperColors != null) {
                WallpaperManagerCompat wallpaperManagerCompat = InjectorProvider.getInjector().getWallpaperManagerCompat(mContext);
                boolean homeWallpaperIsLock = true;
                if (wallpaperManagerCompat.getWallpaperId(WallpaperManager.FLAG_SYSTEM) != wallpaperManagerCompat.getWallpaperId(WallpaperManager.FLAG_LOCK)) {
                    homeWallpaperIsLock = false;
                }
                colorProvider.buildColorSeeds(homeWallpaperIsLock ? mLockWallpaperColors : mHomeWallpaperColors, seedsFromHome, homeWallpaperIsLock ? "lock_wallpaper" : "home_wallpaper", true, arrayList);
                colorProvider.buildColorSeeds(homeWallpaperIsLock ? mHomeWallpaperColors : mLockWallpaperColors, seedsFromHome, homeWallpaperIsLock ? "home_wallpaper" : "lock_wallpaper", false, arrayList);
            } else {
                colorProvider.buildColorSeeds(mHomeWallpaperColors, seedsFromHome, "home_wallpaper", true, arrayList);
            }
            if (mColorOptions == null) {
                mColorOptions = new ArrayList<ColorOption>();
            }
        }
    }

    private void buildColorSeeds(WallpaperColors wallpaperColors, boolean z, String str, List<ColorOption> list) {

        String str2;
        HashMap hashMap = new HashMap();
        Cam fromInt = Cam.fromInt(i);
        float hue = fromInt.getHue();
        float chroma = fromInt.getChroma();
        float f = 48.0f;
        if (chroma < 48.0f) {
            chroma = 48.0f;
        }
        List<Integer> list2 = Shades.of(hue, chroma));
        List<Integer> list3 = Shades.of(hue, 16.0f));
        List<Integer> list4 = Shades.of(hue + 60.0f, 32.0f));
        Shades.of(hue, 4.0f));
        Shades.of(hue, 8.0f));
        Cam fromInt2 = Cam.fromInt(i);
        float hue2 = fromInt2.getHue();
        float chroma2 = fromInt2.getChroma();
        if (chroma2 >= 48.0f) {
            f = chroma2;
        }
        List<Integer> list5 = Shades.of(hue2, f));
        List<Integer> list6 = Shades.of(hue2, 16.0f));
        List<Integer> list7 = Shades.of(60.0f + hue2, 32.0f));
        Shades.of(hue2, 4.0f));
        Shades.of(hue2, 8.0f));
        int[] iArr = new int[]{ColorUtils.setAlphaComponent(list2.get(2).intValue(), 255), ColorUtils.setAlphaComponent(list2.get(2).intValue(), 255), ColorStateList.valueOf(list4.get(6).intValue()).withLStar(85.0f).getColors()[0], ColorStateList.valueOf(list3.get(6).intValue()).withLStar(95.0f).getColors()[0]};
        int[] iArr2 = new int[]{ColorUtils.setAlphaComponent(list5.get(2).intValue(), 255), ColorUtils.setAlphaComponent(list5.get(2).intValue(), 255), ColorStateList.valueOf(list7.get(6).intValue()).withLStar(85.0f).getColors()[0], ColorStateList.valueOf(list6.get(6).intValue()).withLStar(95.0f).getColors()[0]};
        String str3 = "";
        if (z) {
            str2 = str3;
        } else {
            str2 = ColorUtils.toColorString(i);
        }
        hashMap.put("android.theme.customization.system_palette", str2);
        if (!z) {
            str3 = ColorUtils.toColorString(i);
        }
        hashMap.put("android.theme.customization.accent_color", str3);
        list.add(new ColorSeedOption(null, hashMap, z, str, i2 + 1, new ColorSeedOption.PreviewInfo(iArr, iArr2)));
    }
}
