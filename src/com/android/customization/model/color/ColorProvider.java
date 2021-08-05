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
    public List<ColorBundle> colorBundles;
    @Nullable
    public WallpaperColors homeWallpaperColors;
    @Nullable
    public WallpaperColors lockWallpaperColors;

    public ColorProvider(Context context, String stubPackageName) {
        super(context, stubPackageName);
    }

    private void loadSeedColors(@NonNull ColorProvider colorProvider, WallpaperColors wallpaperColors, WallpaperColors wallpaperColors2) {
        if (wallpaperColors != null) {
            ArrayList arrayList = new ArrayList();
            int i = wallpaperColors2 == null ? 4 : 2;
            if (wallpaperColors2 != null) {
                WallpaperManagerCompatV16 wallpaperManagerCompat = InjectorProvider.getInjector().getWallpaperManagerCompat(colorProvider.mContext);
                boolean z = true;
                if (wallpaperManagerCompat.getWallpaperId(2) <= wallpaperManagerCompat.getWallpaperId(1)) {
                    z = false;
                }
                colorProvider.buildColorSeeds(z ? wallpaperColors2 : wallpaperColors, i, z ? "lock_wallpaper" : "home_wallpaper", true, arrayList);
                colorProvider.buildColorSeeds(z ? wallpaperColors : wallpaperColors2, i, z ? "home_wallpaper" : "lock_wallpaper", false, arrayList);
            } else {
                colorProvider.buildColorSeeds(wallpaperColors, i, "home_wallpaper", true, arrayList);
            }
            List<ColorBundle> list = colorProvider.colorBundles;
            if (list == null) {
                emptyList = null;
            } else {
                ArrayList arrayList2 = new ArrayList();
                for (T t : list) {
                    if (!(t instanceof ColorSeedOption)) {
                        arrayList2.add(t);
                    }
                }
                emptyList = arrayList2;
            }
            if (emptyList == null) {
                emptyList = EmptyList.INSTANCE;
            }
            arrayList.addAll(emptyList);
            colorProvider.colorBundles = arrayList;
        }
    }

    private void buildBundle(int i, int i2, boolean z, String str, List<ColorOption> list) {
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
        List<Integer> list3 = ArraysKt___ArraysKt.toList(Shades.of(hue, 16.0f));
        List<Integer> list4 = ArraysKt___ArraysKt.toList(Shades.of(hue + 60.0f, 32.0f));
        ArraysKt___ArraysKt.toList(Shades.of(hue, 4.0f));
        ArraysKt___ArraysKt.toList(Shades.of(hue, 8.0f));
        Cam fromInt2 = Cam.fromInt(i);
        float hue2 = fromInt2.getHue();
        float chroma2 = fromInt2.getChroma();
        if (chroma2 >= 48.0f) {
            f = chroma2;
        }
        List<Integer> list5 = ArraysKt___ArraysKt.toList(Shades.of(hue2, f));
        List<Integer> list6 = ArraysKt___ArraysKt.toList(Shades.of(hue2, 16.0f));
        List<Integer> list7 = ArraysKt___ArraysKt.toList(Shades.of(60.0f + hue2, 32.0f));
        ArraysKt___ArraysKt.toList(Shades.of(hue2, 4.0f));
        ArraysKt___ArraysKt.toList(Shades.of(hue2, 8.0f));
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
        list.add(new ColorSeedOption(null, hashMap, z, str, i2 + 1, new ColorSeedOption.PreviewInfo(iArr, iArr2, null)));
    }

    private void buildColorSeeds(WallpaperColors wallpaperColors, int i, String str, boolean z, List<ColorOption> list) {
        List<Object> list2;
        List<Number> list3;
        List<Integer> seedColors = ColorScheme.Companion.getSeedColors(wallpaperColors);
        buildBundle(((Number) CollectionsKt___CollectionsKt.first(seedColors)).intValue(), 0, z, str, list);
        ArrayList arrayList = (ArrayList) seedColors;
        int size = arrayList.size() - 1;
        if (size <= 0) {
            list2 = EmptyList.INSTANCE;
        } else if (size != 1) {
            ArrayList arrayList2 = new ArrayList(size);
            int size2 = arrayList.size();
            for (int i2 = 1; i2 < size2; i2++) {
                arrayList2.add(arrayList.get(i2));
            }
            list2 = arrayList2;
        } else if (!arrayList.isEmpty()) {
            list2 = R$bool.listOf(arrayList.get(arrayList.size() - 1));
        } else {
            throw new NoSuchElementException("List is empty.");
        }
        int i3 = i - 1;
        Intrinsics.checkNotNullParameter(list2, "$this$take");
        int i4 = 0;
        if (i3 >= 0) {
            if (i3 == 0) {
                list3 = EmptyList.INSTANCE;
            } else if (i3 >= list2.size()) {
                list3 = CollectionsKt___CollectionsKt.toList(list2);
            } else if (i3 == 1) {
                list3 = R$bool.listOf(CollectionsKt___CollectionsKt.first(list2));
            } else {
                ArrayList arrayList3 = new ArrayList(i3);
                int i5 = 0;
                for (Object obj : list2) {
                    arrayList3.add(obj);
                    i5++;
                    if (i5 == i3) {
                        break;
                    }
                }
                list3 = R$bool.optimizeReadOnlyList(arrayList3);
            }
            for (Number number : list3) {
                i4++;
                buildBundle(number.intValue(), i4, false, str, list);
            }
            return;
        }
        throw new IllegalArgumentException(("Requested element count " + i3 + " is less than zero.").toString());
    }
}
