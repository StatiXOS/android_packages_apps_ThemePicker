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
package com.android.customization.model.iconpack;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.android.customization.model.CustomizationManager.Callback;
import com.android.customization.model.CustomizationManager.OptionsFetchedListener;
import com.android.customization.picker.iconpack.IconPackSectionView;

import com.android.wallpaper.R;
import com.android.wallpaper.model.CustomizationSectionController;
import com.android.wallpaper.util.LaunchUtils;

import java.util.List;

/** A {@link CustomizationSectionController} for system icons. */

public class IconPackSectionController implements CustomizationSectionController<IconPackSectionView> {

    private static final String TAG = "IconPackSectionController";

    private final IconPackManager mIconPackOptionsManager;
    private final CustomizationSectionNavigationController mSectionNavigationController;
    private final Callback mApplyIconCallback = new Callback() {
        @Override
        public void onSuccess() {
        }

        @Override
        public void onError(@Nullable Throwable throwable) {
        }
    };

    public IconPackSectionController(IconPackManager iconPackOptionsManager,
            CustomizationSectionNavigationController sectionNavigationController) {
        mIconPackOptionsManager = iconPackOptionsManager;
        mSectionNavigationController = sectionNavigationController;
    }

    @Override
    public boolean isAvailable(Context context) {
        return mIconPackOptionsManager.isAvailable();
    }

    @Override
    public IconPackSectionView createView(Context context) {
        IconPackSectionView iconPackSectionView = (IconPackSectionView) LayoutInflater.from(context)
                .inflate(R.layout.icon_section_view, /* root= */ null);

        mIconPackOptionsManager.fetchOptions(new OptionsFetchedListener<IconPackOption>() {
            @Override
            public void onOptionsLoaded(List<IconPackOption> options) {
                IconSectionAdapter adapter = new IconSectionAdapter(options);
                ViewPager2 vp2 = iconPackSectionView.findViewById(R.id.icon_view_pager);
                vp2.setAdapter(adapter);
            }

            @Override
            public void onError(@Nullable Throwable throwable) {
                if (throwable != null) {
                    Log.e(TAG, "Error loading icon options", throwable);
                }
            }
        }, /* reload= */ true);

        return iconPackSectionView;
    }

    public class IconSectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<IconPackOption> mIconPacks;

        public class IconOptionsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public IconOptionsViewHolder(IconSectionAdapter iconSectionAdapter, View view) {
                super(view);
            }

            @Override
            public void onClick(View v) {
                mIconPackOptionsManager.apply(mIconPacks.get(getAdapterPosition()), mApplyIconCallback);
            }
        }

        public IconSectionAdapter(List<IconPackOption> iconPacks) {
            mIconPacks = iconPacks;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return mIconPacks.size();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return R.layout.icon_option_view;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            View view = viewHolder.itemView;
            if (!(view instanceof RecyclerView)) {
                return;
            }
        }


        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            return new IconOptionsViewHolder(this, LayoutInflater.from(viewGroup.getContext()).inflate(position, viewGroup, false));
        }
    }
}
