package com.android.customization.model.color

import android.annotation.ColorInt
import android.app.WallpaperColors
import android.util.SparseIntArray

import com.android.internal.graphics.ColorUtils
import com.android.internal.graphics.cam.Cam

import dev.kdrag0n.colorkt.tristimulus.CieXyzAbs.Companion.toAbs
import dev.kdrag0n.colorkt.ucs.lab.CieLab
import dev.kdrag0n.colorkt.cam.Zcam
import dev.kdrag0n.colorkt.conversion.ConversionGraph.convert
import dev.kdrag0n.colorkt.data.Illuminants
import dev.kdrag0n.colorkt.rgb.Srgb
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

        val intValue = ((colorScheme.accent1.values.first()) as Srgb).toRgb8()
        val fromInt = Cam.fromInt(if (intValue == 0 || Cam.fromInt(intValue).getChroma() < 5.0f) GOOGLE_BLUE else intValue)
        val hue = fromInt.getHue()
        val chroma = fromInt.getChroma()
        addOverlayColor(shadesOf(hue, 4.0f), android.R.color.system_neutral1_10)
        addOverlayColor(shadesOf(hue, 8.0f), android.R.color.system_neutral2_10)
        addOverlayColor(shadesOf(hue, if (chroma < 48.0f) 48.0f else chroma), android.R.color.system_accent1_10)
        addOverlayColor(shadesOf(hue, 16.0f), android.R.color.system_accent2_10)
        addOverlayColor(shadesOf(60.0f + hue, 32.0f), android.R.color.system_accent3_10)
    }

    private fun addOverlayColor(list: IntArray, @ColorInt color: Int) {
        var i = color
        list.forEach {
            mColorOverlay.append(i, it)
            i++
        }
    }

    companion object {
        private const val SRGB_WHITE_LUMINANCE = 200.0 // cd/m^2
        private const val MIDDLE_LSTAR = 49.6f
        private const val GOOGLE_BLUE = 0xFF1B6EF3.toInt()

        private fun shadesOf(hue: Float, chroma: Float): IntArray {
            val array = IntArray(12)
            array[0] = ColorUtils.CAMToColor(hue, chroma, 99.0f)
            array[1] = ColorUtils.CAMToColor(hue, chroma, 95.0f)
            for (i in 2..11) {
                val lStar = if (i == 6) MIDDLE_LSTAR else ((100 - 10 * (i - 1)) as Float)
                array[i] = ColorUtils.CAMToColor(hue, chroma, lStar)
            }
            return array
        }
    }
}
