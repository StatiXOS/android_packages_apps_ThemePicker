package com.android.customization.model.color;

import android.app.WallpaperColors;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.android.customization.model.CustomizationManager;
import com.android.wallpaper.R;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ColorCustomizationManager implements CustomizationManager<ColorOption> {
    private static final Set<String> COLOR_OVERLAY_SETTINGS = new HashSet();
    private static ColorCustomizationManager sColorCustomizationManager;
    private ContentResolver mContentResolver;
    private Map<String, String> mCurrentOverlays;
    private String mCurrentSource;
    private ColorOptionsProvider mProvider;

    static {
        COLOR_OVERLAY_SETTINGS.add("android.theme.customization.system_palette");
        COLOR_OVERLAY_SETTINGS.add("android.theme.customization.accent_color");
        COLOR_OVERLAY_SETTINGS.add("android.theme.customization.color_source");
    }

    private ColorCustomizationManager(ColorOptionsProvider colorOptionsProvider, ContentResolver contentResolver) {
        mProvider = colorOptionsProvider;
        mContentResolver = contentResolver;
    }

    public static ColorCustomizationManager getInstance(Context context) {
        if (sColorCustomizationManager == null) {
            Context applicationContext = context.getApplicationContext();
            sColorCustomizationManager = new ColorCustomizationManager(new ColorProvider(applicationContext, applicationContext.getString(R.string.themes_stub_package)), applicationContext.getContentResolver());
        }
        return sColorCustomizationManager;
    }

    @Override
    public void fetchOptions(OptionsFetchedListener<ColorOption> callback, boolean reload) {
        List<ColorOption> options = mProvider.getOptions();
        callback.onOptionsLoaded(options);
    }

    @Override
    public boolean isAvailable() {
        return mProvider.isAvailable();
    }

    @Override
    public void apply(ColorOption option, @Nullable Callback callback) {
        if (!applyOption(option)) {
            Toast failed = Toast.makeText(mContext, "Failed to apply icon pack, reboot to try again.", Toast.LENGTH_SHORT);
            failed.show();
            if (callback != null) {
                callback.onError(null);
            }
            return;
        }
        if (callback != null) {
            callback.onSuccess();
        }
        mCurrentSource = option.getSource();
    }

    public String getCurrentColorSource() {
        if (mCurrentSource == null) {
            parseSettings(getStoredOverlays());
        }
        return mCurrentSource;
    }

    public String getStoredOverlays() {
        return Settings.Secure.getString(mContentResolver, Settings.Secure.THEME_CUSTOMIZATION_OVERLAY_PACKAGES);
    }

    public void setWallpaperColors(WallpaperColors wpc, boolean isLock) {
        mProvider.setWallpaperColors(wpc, isLock);
    }

    private void parseSettings(String str) {
        HashMap hashMap = new HashMap();
        if (str != null) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                JSONArray names = jSONObject.names();
                if (names != null) {
                    for (int i = 0; i < names.length(); i++) {
                        String string = names.getString(i);
                        if (((HashSet) COLOR_OVERLAY_SETTINGS).contains(string)) {
                            try {
                                hashMap.put(string, jSONObject.getString(string));
                            } catch (JSONException e) {
                                Log.e("ColorCustomizationManager", "parseColorOverlays: " + e.getLocalizedMessage(), e);
                            }
                        }
                    }
                }
            } catch (JSONException jsonException) {
                StringBuilder m = new StringBuilder("parseColorOverlays: ");
                m.append(jsonException.getLocalizedMessage());
                Log.e("ColorCustomizationManager", m.toString(), jsonException);
            }
        }
        mCurrentSource = (String) hashMap.remove("android.theme.customization.color_source");
        mCurrentOverlays = hashMap;
    }

    private boolean applyOption(ColorOption option) {
        HashMap settingsMap = option.getSettings();
        for (Map.Entry<String, String> entry : settingsMap.entrySet()) {
            mCurrentOverlays.put(entry.getKey(), entry.getValue());
        }
        mCurrentOverlays.put("android.theme.customization.color_source", option.getSource());
        mCurrentOverlays.put("_applied_timestamp", System.currentTimeMillis());
        try {
            JSONObject jsonStr = new JSONObject(mCurrentOverlays);
            Setting.Secure.putString(mContentResolver, Settings.Secure.THEME_CUSTOMIZATION_OVERLAY_PACKAGES, jsonStr);
            mCurrentSource = (String) mCurrentOverlays.remove("android.theme.customization.color_source");
            mCurrentOverlays.remove("_applied_timestamp");
            return true;
        } catch (NullPointerException e) {
            Log.e("ColorCustomizationManager", e);
            mCurrentOverlays.remove("_applied_timestamp");
            mCurrentOverlays.remove("android.theme.customization.color_source");
            return false;
        }
    }
}
