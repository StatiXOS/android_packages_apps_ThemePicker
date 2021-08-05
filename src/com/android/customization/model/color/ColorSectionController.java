package com.android.customization.model.color;

import android.app.Activity;
import android.app.WallpaperColors;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.wallpaper.model.CustomizationSectionController;
import com.android.wallpaper.model.WallpaperColorsViewModel;
import com.android.wallpaper.module.InjectorProvider;

import com.android.customization.picker.color.ColorSectionView;
import com.android.customization.model.CustomizationManager.Callback;
import com.android.customization.model.theme.OverlayManagerCompat;
import com.android.customization.module.ThemesUserEventLogger;
import com.android.customization.widget.OptionSelectorController;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.lang.Integer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ColorSectionController implements CustomizationSectionController<ColorSectionView> {

    private ColorCustomizationManager mColorManager;
    private ColorOption mSelectedColor;
    private ColorSectionAdapter mColorSectionAdapter;
    private LifecycleOwner mLifecycleOwner;
    private ViewPager2 mColorViewPager;
    private WallpaperColorsViewModel mWallpaperColorsViewModel;
    private ThemesUserEventLogger mEventLogger;

    private long mLastColorApplyingTime;

    private WallpaperColors mHomeWallpaperColors;
    private WallpaperColors mLockWallpaperColors;

    private List<ColorOption> mPresetColorOptions;
    private List<ColorOption> mWallpaperColorOptions;

    private TabLayout mTabLayout;
    private TabLayoutMediator mTabLayoutMediator;
    private Optional<Integer> mTabPositionToRestore;

    private final Callback mApplyColorCallback = new Callback() {
        @Override
        public void onSuccess() {
        }

        @Override
        public void onError(@Nullable Throwable throwable) {
        }
    };


    public ColorSectionController(Activity a, WallpaperColorsViewModel wcvm, LifecycleOwner lc, Bundle savedInstanceState) {
        mWallpaperColorsViewModel = wcvm;
        mLifecycleOwner = lc;
        mPresetColorOptions = new ArrayList<ColorOption>();
        mWallpaperColorOptions = new ArrayList<ColorOption>();
        mTabPositionToRestore = Optional<Integer>.empty();
        mEventLogger = (ThemesUserEventLogger)
                InjectorProvider.getInjector().getUserEventLogger(a);
        OverlayManagerCompat overlayManager = new OverlayManagerCompat(a);
        mColorManager = ColorCustomizationManager.getInstance(a, overlayManager);
        if (savedInstanceState == null) {
            return;
        }
        if (savedInstanceState.containsKey("COLOR_TAB_POSITION")) {
            mTabPositionToRestore = Optional<Integer>.of(savedInstanceState.getInt("COLOR_TAB_POSITION"));
        }
        mColorSectionAdapter = new ColorSectionAdapter(this);
    }

    @Override
    public ColorSectionView createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context, null);
        ColorSectionView csv = (ColorSectionView) inflater.inflate(R.layout.color_section_view);
        mTabLayout = (TabLayout) csv.findViewById(R.id.separated_tabs);
        mColorViewPager = (ViewPager2) csv.findViewById(R.id.color_view_pager);
        if (mColorViewPager.getAdapter() == null) {
            mColorViewPager.setAdapter(mColorSectionAdapter);
        }
        mHomeWallpaperColors = mWallpaperColorsViewModel.getHomeWallpaperColors();
        mLockWallpaperColors = mWallpaperColorsViewModel.getLockWallpaperColors();
        mColorManager.setLockWallpaperColors(mLockWallpaperColors);
        mColorManager.setHomeWallpaperColors(mHomeWallpaperColors);
        mColorManager.fetchOptions(new OptionsFetchedListener<ColorOption>() {
            @Override
            public void onOptionsLoaded(List<ColorOption> options) {
                mWallpaperColorOptions.clear();
                mPresetColorOptions.clear();
                for (ColorOption colorOption : options) {
                    if (colorOption instanceof ColorSeedOption) {
                        mWallpaperColorOptions.add(colorOption);
                    } else if (colorOption instanceof ColorBundle) {
                        mPresetColorOptions.add(colorOption);
                    }
                }
            }

            @Override
            public void onError(@Nullable Throwable throwable) {
                if (throwable != null) {
                    Log.e(TAG, "Error loading icon options", throwable);
                }
            }
        }, /* reload= */ true);
    }

    @Override
    public boolean isAvailable(Context context) {
        return context != null && ColorUtils.isMonetEnabled(context);
    }

    @Override
    public void onSaveInstanceState(Bundle instanceState) {
        if (mColorViewPager != null) {
            instanceState.putInt("COLOR_TAB_POSITION", mColorViewPager.getCurrentItem());
        }
    }

    private void setUpColorOptionsController(OptionSelectorController<ColorOption> controller) {
        if (mSelectedColor == null || !controller.containsOption(mSelectedColor)) {
        } else {
            controller.setSelectedOption(mSelectedColor);
            controller.addListener(new OptionSelectedListener() {
                @Override
                public void onOptionSelected(CustomizationOption selected) {
                    mColorManager.apply((ColorOption) selected, mApplyColorCallback);
                    optionSelectorController.setAppliedOption((ColorOption) selected);
                }
            });
        }
    }

    private class ColorSectionAdapter extends RecyclerView.Adapter<ColorOptionsViewHolder> {

        private int mItemsCount;
        private ColorSectionController mController;

        ColorSectionAdapter(ColorSectionController controller) {
            super();
            mController = controller;
            mItemsCount = 2;
        }

        @Override
        public int getItemCount() {
            return mItemsCount;
        }

        @Override
        public int getItemViewType() {
            return R.layout.color_options_view;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
            View v = vh.itemView;
            if (v instanceof RecyclerView) {
                if (position == 0) {
                    OptionSelectorController<ColorOption> optionController = new OptionSelectorController<>(v, mWallpaperColorOptions);
                } else if (position == 1) {
                    OptionSelectorController<ColorOption> optionController = new OptionSelectorController<>(v, mPresetColorOptions);
                }
                optionController.initOptions(mColorManager);
                mController.setUpColorOptionsController(optionController);
            }
        }

        @Override
        public void onCreateViewHolder(ViewGroup parent, int position) {
            Context context = parent.getContext();
            return new ColorOptionsViewHolder(this, LayoutInflater.from(context).inflate(position, parent, false));
        }

        private class ColorOptionsViewHolder extends RecyclerView.ViewHolder {
            ColorOptionsViewHolder(ColorSectionAdapter adapter, View itemView) {
                super(itemView);
            }
        }
    }
}
