package com.example.memeeditor.meme_editor.presentaion.util

import androidx.compose.ui.unit.IntSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FitCanvasMappingTest {

    @Test
    fun fitScaleUsesMinAxis() {
        val mapping = FitCanvasMapping.fromTemplateAndBitmap(
            templateSize = IntSize(200, 100),
            bitmapSize = IntSize(100, 100),
        )
        // template is wider: scale limited by height → 100/100 = 1, wait:
        // s = min(200/100, 100/100) = min(2, 1) = 1
        assertEquals(1f, mapping.scale)
        assertEquals(50f, mapping.insetX)
        assertEquals(0f, mapping.insetY)
    }

    @Test
    fun templateDistanceScalesByInverseFit() {
        val mapping = FitCanvasMapping(scale = 0.5f, insetX = 0f, insetY = 0f)
        assertEquals(20f, mapping.templateDistanceToBitmap(10f))
    }

    @Test
    fun zeroSizesDoNotCrash() {
        val mapping = FitCanvasMapping.fromTemplateAndBitmap(
            templateSize = IntSize(0, 0),
            bitmapSize = IntSize(0, 0),
        )
        assertTrue(mapping.scale > 0f)
    }
}
