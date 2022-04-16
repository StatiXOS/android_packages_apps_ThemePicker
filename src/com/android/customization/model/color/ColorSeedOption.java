package com.android.customization.model.color;

import static android.content.res.Configuration.UI_MODE_NIGHT_MASK;
import static android.content.res.Configuration.UI_MODE_NIGHT_YES;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.android.wallpaper.R;
import java.util.Map;
import java.util.Objects;

// seed option from wallpaper
public class ColorSeedOption extends ColorOption {
    private static final int[] mPreviewColorIds = {R.id.color_preview_0, R.id.color_preview_1, R.id.color_preview_2, R.id.color_preview_3};
    @NonNull
    private PreviewInfo mPreviewInfo;
    private String mSource;

    public static class PreviewInfo {
        public int[] mDarkColors;
        public int[] mLightColors;

        public PreviewInfo(int[] lightColors, int[] darkColors) {
            mLightColors = lightColors;
            mDarkColors = darkColors;
        }
    }

    public ColorSeedOption(String title, Map<String, String> settingsMap, boolean isDefault, String source, int position, PreviewInfo previewInfo) {
        super(title, settingsMap, isDefault, position);
        mSource = source;
        mPreviewInfo = previewInfo;
    }

    @Override
    public void bindThumbnailTile(View view) {
        int padding;
        Resources resources = view.getContext().getResources();
        int[] colors = (resources.getConfiguration().uiMode & UI_MODE_NIGHT_MASK) == UI_MODE_NIGHT_YES ? previewInfo.darkColors : previewInfo.lightColors;
        if (view.isActivated()) {
            padding = resources.getDimensionPixelSize(R.dimen.color_seed_option_tile_padding_selected);
        } else {
            padding = resources.getDimensionPixelSize(R.dimen.color_seed_option_tile_padding);
        }
        int i = 0;
        while (true) {
            if (i < mPreviewColorIds.length) {
                ImageView imageView = (ImageView) view.findViewById(mPreviewColorIds[i]);
                imageView.getDrawable().setColorFilter(colors[i], PorterDuff.Mode.SRC);
                imageView.setPadding(padding, padding, padding, padding);
                i++;
            } else {
                view.setContentDescription(view.getContext().getString(R.string.wallpaper_color_title));
                return;
            }
        }
    }

    @Override
    public int getLayoutResId() {
        return R.layout.color_seed_option;
    }

    @Override
    public String getSource() {
        return mSource;
    }
}
