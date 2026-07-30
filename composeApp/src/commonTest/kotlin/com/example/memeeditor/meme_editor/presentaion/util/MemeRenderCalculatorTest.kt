package com.example.memeeditor.meme_editor.presentaion.util

import com.example.memeeditor.meme_editor.presentaion.MemeText
import com.example.memeeditor.meme_editor.presentaion.MemeTextColors
import kotlin.test.Test
import kotlin.test.assertEquals

class MemeRenderCalculatorTest {

    @Test
    fun scaledTextKeepsOriginalColor() {
        val calculator = MemeRenderCalculator(density = 2f, fontScale = 1f)
        val memeText = MemeText(
            id = "1",
            text = "HI",
            colorArgb = MemeTextColors.Yellow,
        )
        val mapping = FitCanvasMapping(scale = 1f, insetX = 0f, insetY = 0f)
        val scaled = calculator.calculateScaledMemeText(
            memeText = memeText,
            mapping = mapping,
            templateSize = androidx.compose.ui.unit.IntSize(400, 400),
        )
        assertEquals(MemeTextColors.Yellow, scaled.originalText.colorArgb)
        assertEquals("HI", scaled.text)
    }
}
