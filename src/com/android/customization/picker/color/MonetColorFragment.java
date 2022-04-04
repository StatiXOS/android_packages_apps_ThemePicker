/*
 * Copyright (C) 2018 The Android Open Source Project
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
package com.android.customization.picker.color;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.wallpaper.R;
import com.android.wallpaper.picker.AppbarFragment;

/**
 * Fragment that contains the UI for selecting and applying a IconPackOption.
 */
public class MonetColorFragment extends AppbarFragment {

    private static final String TAG = "MonetColorFragment";

    private static final String ACCURATE_SHADES_PREF = "monet_engine_accurate_shades";
    private static final String LINEAR_LIGHTNESS_PREF = "monet_engine_linear_lightness";
    private static final String CHROMA_FACTOR_PREF = "monet_engine_chroma_factor";

    public static MonetColorFragment newInstance(CharSequence title) {
        MonetColorFragment fragment = new MonetColorFragment();
        fragment.setArguments(AppbarFragment.createArguments(title));
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_monet_picker, container, /* attachToRoot */ false);

        setUpToolbar(view);

        Switch accurateShades = view.findViewById(R.id.monet_engine_accurate_shades);
        Switch linearLightness = view.findViewById(R.id.monet_engine_linear_lightness);
        SeekBar chroma = view.findViewById(R.id.monet_engine_chroma_factor);
        Context context = inflater.getContext();

        accurateShades.setChecked(Settings.Secure.getInt(context.getContentResolver(), ACCURATE_SHADES_PREF, 1) == 1);
        linearLightness.setChecked(Settings.Secure.getInt(context.getContentResolver(), LINEAR_LIGHTNESS_PREF, 0) == 1);
        chroma.setProgress((int) (Settings.Secure.getFloat(context.getContentResolver(), CHROMA_FACTOR_PREF, 1.0f)*10));
        accurateShades.setOnCheckedChangeListener((buttonView, isChecked) ->
                Settings.Secure.putInt(context.getContentResolver(), ACCURATE_SHADES_PREF, isChecked ? 1 : 0)
        );
        linearLightness.setOnCheckedChangeListener((buttonView, isChecked) ->
                Settings.Secure.putInt(context.getContentResolver(), LINEAR_LIGHTNESS_PREF, isChecked ? 1 : 0)
        );
        chroma.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float realProgress = ((float)seekBar.getProgress())/10.0f;
                Settings.Secure.putFloat(context.getContentResolver(), CHROMA_FACTOR_PREF, realProgress);
            }
        });

        // For nav bar edge-to-edge effect.
        view.setOnApplyWindowInsetsListener((v, windowInsets) -> {
            v.setPadding(
                    v.getPaddingLeft(),
                    windowInsets.getSystemWindowInsetTop(),
                    v.getPaddingRight(),
                    windowInsets.getSystemWindowInsetBottom());
            return windowInsets.consumeSystemWindowInsets();
        });

        return view;
    }
}
