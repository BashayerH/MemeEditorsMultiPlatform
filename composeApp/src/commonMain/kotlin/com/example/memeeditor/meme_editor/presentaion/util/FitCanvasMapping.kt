package com.example.memeeditor.meme_editor.presentaion.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import kotlin.math.max
import kotlin.math.min

/**
 * Maps between the on-screen template box (same size as [templateSize]) and full bitmap pixels
 * when the image is shown with [androidx.compose.ui.layout.ContentScale.Fit].
 */
data class FitCanvasMapping(
    val scale: Float,
    val insetX: Float,
    val insetY: Float,
) {
    fun templateTopLeftToBitmapTopLeft(tx: Float, ty: Float): Offset =
        Offset(
            x = (tx - insetX) / scale,
            y = (ty - insetY) / scale,
        )

    /** Converts a distance measured in template pixels to bitmap pixels (uniform Fit scale). */
    fun templateDistanceToBitmap(distanceTemplatePx: Float): Float = distanceTemplatePx / scale

    companion object {
        fun fromTemplateAndBitmap(templateSize: IntSize, bitmapSize: IntSize): FitCanvasMapping {
            val tw = max(templateSize.width, 1).toFloat()
            val th = max(templateSize.height, 1).toFloat()
            val bw = max(bitmapSize.width, 1).toFloat()
            val bh = max(bitmapSize.height, 1).toFloat()
            val s = min(tw / bw, th / bh)
            val drawW = bw * s
            val drawH = bh * s
            val insetX = (tw - drawW) / 2f
            val insetY = (th - drawH) / 2f
            return FitCanvasMapping(scale = s, insetX = insetX, insetY = insetY)
        }
    }
}
