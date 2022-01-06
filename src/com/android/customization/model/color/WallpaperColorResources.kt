package com.android.customization.model.color

import android.annotation.ColorInt
import android.app.WallpaperColors
import android.util.SparseIntArray

import dev.kdrag0n.colorkt.Color
import dev.kdrag0n.colorkt.cam.Zcam
import dev.kdrag0n.colorkt.conversion.ConversionGraph.convert
import dev.kdrag0n.colorkt.data.Illuminants
import dev.kdrag0n.colorkt.rgb.Srgb
import dev.kdrag0n.colorkt.tristimulus.CieXyzAbs.Companion.toAbs
import dev.kdrag0n.colorkt.ucs.lab.CieLab
import dev.kdrag0n.monet.theme.DynamicColorScheme
import dev.kdrag0n.monet.theme.MaterialYouTargets

class WallpaperColorResources {
    @get:JvmName("getColorOverlay")
    val mColorOverlay = SparseIntArray()

    private val cond = Zcam.ViewingConditions(
        surroundFactor = Zcam.ViewingConditions.SURROUND_AVERAGE,
        // sRGB
        adaptingLuminance = 0.4 * SRGB_WHITE_LUMINANCE,
        // Gray world
        backgroundLuminance = CieLab(
            L = 50.0,
            a = 0.0,
            b = 0.0,
        ).toXyz().y * SRGB_WHITE_LUMINANCE,
        referenceWhite = Illuminants.D65.toAbs(SRGB_WHITE_LUMINANCE),
    )

    private val targets = MaterialYouTargets(
        chromaFactor = 1.0,
        useLinearLightness = false,
        cond = cond,
    )

    constructor(wallpaperColors: WallpaperColors) {
        // Generate color scheme
        val colorScheme = DynamicColorScheme(
            targets = targets,
            seedColor = Srgb(wallpaperColors.primaryColor.toArgb()),
            chromaFactor = 1.0,
            cond = cond,
            accurateShades = true,
        )
        addOverlayColor(colorScheme.neutral1, android.R.color.system_neutral1_0)
        addOverlayColor(colorScheme.neutral2, android.R.color.system_neutral2_0)
        addOverlayColor(colorScheme.accent1, android.R.color.system_accent1_0)
        addOverlayColor(colorScheme.accent2, android.R.color.system_accent2_0)
        addOverlayColor(colorScheme.accent3, android.R.color.system_accent3_0)
    }

    private fun addOverlayColor(swatch: Map<Int, Color>, @ColorInt color: Int) {
        var i = color
        swatch.forEach { _, v ->
            mColorOverlay.append(i, v.convert<Srgb>().toRgb8() or (0xff shl 24))
            i++
        }
    }

    companion object {
        private const val SRGB_WHITE_LUMINANCE = 200.0 // cd/m^2
    }
}
