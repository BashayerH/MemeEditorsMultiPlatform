package com.example.memeeditor.meme_editor.presentaion.util


import androidx.compose.ui.unit.IntSize
import com.example.memeeditor.meme_editor.presentaion.MemeText
import kotlin.math.roundToInt

class MemeRenderCalculator(
    private val density: Float,
    /** User font scale (accessibility); must match Compose `LocalDensity.fontScale` on Android. */
    private val fontScale: Float = 1f,
) {
    companion object {
        private const val TEXT_PADDING_DP = 8f
        private const val STROKE_WIDTH_DP = 3f
    }

    fun calculateScaledMemeText(
        memeText: MemeText,
        mapping: FitCanvasMapping,
        templateSize: IntSize,
    ): ScaledMemeText {
        val tx = memeText.offsetRatioX * templateSize.width
        val ty = memeText.offsetRatioY * templateSize.height
        val scaledOffset = mapping.templateTopLeftToBitmapTopLeft(tx, ty)

        val textPaddingTemplatePx = TEXT_PADDING_DP * density
        val textPaddingBitmap = mapping.templateDistanceToBitmap(textPaddingTemplatePx)

        val fontSizeTemplatePx = memeText.fontSize * density * fontScale
        val scaledFontSize = mapping.templateDistanceToBitmap(fontSizeTemplatePx)

        val strokeWidthTemplatePx = STROKE_WIDTH_DP * density
        val strokeWidth = mapping.templateDistanceToBitmap(strokeWidthTemplatePx)

        val paddingPx = TEXT_PADDING_DP * 2 * density
        val maxLineTemplatePx =
            (templateSize.width.toFloat() / memeText.scale) - paddingPx * 2
        val constraintWidth = mapping.templateDistanceToBitmap(
            maxLineTemplatePx.coerceAtLeast(1f)
        ).roundToInt().coerceAtLeast(1)

        return ScaledMemeText(
            text = memeText.text,
            scaledOffset = scaledOffset,
            scaledFontSizePx = scaledFontSize,
            strokeWidth = strokeWidth,
            constraintWidth = constraintWidth,
            textPaddingX = textPaddingBitmap,
            textPaddingY = textPaddingBitmap,
            rotation = memeText.rotation,
            scale = memeText.scale,
            originalText = memeText
        )
    }
}
