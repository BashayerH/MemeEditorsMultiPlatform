package com.example.memeeditor.meme_editor.presentaion

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MemeTextColorTest {

    @Test
    fun defaultFillIsWhite() {
        val text = MemeText(id = "1", text = "hi")
        assertEquals(MemeTextColors.DefaultFill, text.colorArgb)
        assertEquals(MemeTextColors.White, text.colorArgb)
    }

    @Test
    fun copyPreservesColorThroughTransformFields() {
        val original = MemeText(
            id = "a",
            text = "meme",
            offsetRatioX = 0.1f,
            offsetRatioY = 0.2f,
            scale = 1.5f,
            rotation = 15f,
            colorArgb = MemeTextColors.Red,
        )
        val moved = original.copy(
            offsetRatioX = 0.3f,
            offsetRatioY = 0.4f,
            scale = 1.8f,
            rotation = 30f,
        )
        assertEquals(MemeTextColors.Red, moved.colorArgb)
        assertEquals(0.3f, moved.offsetRatioX)
    }

    @Test
    fun paletteContainsCommonColors() {
        assertTrue(MemeTextColors.Palette.contains(MemeTextColors.White))
        assertTrue(MemeTextColors.Palette.contains(MemeTextColors.Black))
        assertTrue(MemeTextColors.Palette.contains(MemeTextColors.Yellow))
        assertTrue(MemeTextColors.Palette.size >= 8)
    }

    @Test
    fun updatingOneTextColorDoesNotAffectOthers() {
        val texts = listOf(
            MemeText(id = "1", text = "a", colorArgb = MemeTextColors.White),
            MemeText(id = "2", text = "b", colorArgb = MemeTextColors.Blue),
        )
        val updated = texts.map { memeText ->
            if (memeText.id == "1") memeText.copy(colorArgb = MemeTextColors.Yellow) else memeText
        }
        assertEquals(MemeTextColors.Yellow, updated[0].colorArgb)
        assertEquals(MemeTextColors.Blue, updated[1].colorArgb)
    }

    @Test
    fun toComposeColorUsesArgbIntNotColorLong() {
        // Regression: Color(argb.toULong()) treats value as ColorLong and crashes in getColorSpace.
        val color = MemeTextColors.White.toComposeColor()
        assertEquals(1f, color.alpha, 0.001f)
        assertEquals(1f, color.red, 0.001f)
        assertEquals(1f, color.green, 0.001f)
        assertEquals(1f, color.blue, 0.001f)
    }
}
