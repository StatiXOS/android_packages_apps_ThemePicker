package com.android.customization.model.color;

import android.text.TextUtils;
import android.util.Log;

import com.android.customization.model.CustomizationManager;
import com.android.customization.model.CustomizationOption;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ColorOption implements CustomizationOption<ColorOption> {
    private static final String TIMESTAMP_FIELD = "_applied_timestamp";
    private CharSequence mContentDescription;
    private int mIndex;
    private boolean mIsDefault;
    private Map<String, String> mPackagesByCategory;
    private String mTitle;

    public ColorOption(String title, Map<String, String> overlayPackages, boolean isDefault, int position) {
        mTitle = title;
        mIsDefault = isDefault;
        mIndex = position;
        mPackagesByCategory = Collections.unmodifiableMap((Map) map.entrySet()
                                                                   .stream()
                                                                   .filter(entry -> entry.getValue() != null)
                                                                   .collect(Collectors.toMap(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));
    }

    private JSONObject getJsonPackages(boolean insertTimestamp) {
        JSONObject jSONObject;
        if (mIsDefault) {
            jSONObject = new JSONObject();
        } else {
            JSONObject jSONObject2 = new JSONObject(mPackagesByCategory);
            Iterator<String> keys = jSONObject2.keys();
            HashSet keysToRemove = new HashSet();
            while (keys.hasNext()) {
                String next = keys.next();
                if (jSONObject2.isNull(next)) {
                    hashSet.add(next);
                }
            }
            for (String key : keysToRemove) {
                jSONObject2.remove(key);
            }
            jSONObject = jSONObject2;
        }
        if (insertTimestamp) {
            try {
                jSONObject.put(TIMESTAMP_FIELD, System.currentTimeMillis());
            } catch (JSONException unused) {
                Log.e("ColorOption", "Couldn't add timestamp to serialized themebundle");
            }
        }
        return jSONObject;
    }

    public abstract String getSource();

    @Override // com.android.customization.model.CustomizationOption
    public String getTitle() {
        return mTitle;
    }

    @Override // com.android.customization.model.CustomizationOption
    public boolean isActive(CustomizationManager<ColorOption> customizationManager) {
        ColorCustomizationManager colorCustomizationManager = (ColorCustomizationManager) customizationManager;
        if (mIsDefault) {
            String storedOverlays = colorCustomizationManager.getStoredOverlays();
            if (!TextUtils.isEmpty(storedOverlays) && !"{}".equals(storedOverlays)) {
                if (colorCustomizationManager.mCurrentOverlays == null) {
                    colorCustomizationManager.parseSettings(colorCustomizationManager.getStoredOverlays());
                }
                if (!colorCustomizationManager.mCurrentOverlays.isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        if (colorCustomizationManager.mCurrentOverlays == null) {
            colorCustomizationManager.parseSettings(colorCustomizationManager.getStoredOverlays());
        }
        Map<String, String> map = colorCustomizationManager.mCurrentOverlays;
        String currentColorSource = colorCustomizationManager.getCurrentColorSource();
        return (TextUtils.isEmpty(currentColorSource) || getSource().equals(currentColorSource)) && mPackagesByCategory.equals(map);
    }
}
