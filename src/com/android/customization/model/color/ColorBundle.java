package com.android.customization.model.color;

import static android.content.res.Configuration.UI_MODE_NIGHT_MASK;
import static android.content.res.Configuration.UI_MODE_NIGHT_YES;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;

import com.android.wallpaper.R;

import java.util.List;
import java.util.Map;
import java.util.Objects;

// from resource apk stub
public class ColorBundle extends ColorOption {
    private PreviewInfo mPreviewInfo;

    public ColorBundle(String title, Map<String, String> overlayPackages, boolean isDefault, int position, @NonNull PreviewInfo previewInfo) {
        super(title, overlayPackages, isDefault, position);
        mPreviewInfo = previewInfo;
    }

    @Override // com.android.customization.model.CustomizationOption
    public void bindThumbnailTile(View view) {
        Resources resources = view.getContext().getResources();
        int color = (resources.getConfiguration().uiMode & UI_MODE_NIGHT_MASK) == UI_MODE_NIGHT_YES ? previewInfo.secondaryColorDark : previewInfo.secondaryColorLight;
        GradientDrawable gradientDrawable = (GradientDrawable) view.getResources().getDrawable(R.drawable.color_chip_medium_filled, ((ImageView) view.findViewById(R.id.color_preview_icon)).getContext().getTheme());
        if (color) {
            gradientDrawable.setTintList(ColorStateList.valueOf(color));
        } else {
            gradientDrawable.setTintList(ColorStateList.valueOf(resources.getColor(R.color.sliding_tab_text_color_active)));
        }
        ((ImageView) view.findViewById(R.id.color_preview_icon)).setImageDrawable(gradientDrawable);
        Context context = view.getContext();
        if (mContentDescription == null) {
            String defaultTitle = context.getString(R.string.default_theme_title);
            if (mIsDefault) {
                mContentDescription = defaultTitle;
            } else {
                mContentDescription = mTitle;
            }
        }
        view.setContentDescription(mContentDescription);
    }

    @Override // com.android.customization.model.CustomizationOption
    public int getLayoutResId() {
        return R.layout.color_option;
    }

    @Override // com.android.customization.model.color.ColorOption
    public String getSource() {
        return "preset";
    }

    public static class PreviewInfo {
        public List<Drawable> icons;
        public int secondaryColorDark;
        public int secondaryColorLight;

        public PreviewInfo(int light, int dark, List<Drawable> list) {
            secondaryColorLight = i;
            secondaryColorDark = i2;
            icons = list;
        }
    }
}
