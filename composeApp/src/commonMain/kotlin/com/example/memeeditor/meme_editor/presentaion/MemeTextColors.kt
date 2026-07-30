package com.example.memeeditor.meme_editor.presentaion

/**
 * Packed ARGB colors for meme text fill. Platform-agnostic so exporters and UI stay in sync.
 */
object MemeTextColors {
    const val White: Long = 0xFFFFFFFFL
    const val Black: Long = 0xFF000000L
    const val Yellow: Long = 0xFFFFEB3BL
    const val Red: Long = 0xFFF44336L
    const val Blue: Long = 0xFF2196F3L
    const val Green: Long = 0xFF4CAF50L
    const val Orange: Long = 0xFFFF9800L
    const val Pink: Long = 0xFFE91E63L
    const val Purple: Long = 0xFF9C27B0L
    const val Cyan: Long = 0xFF00BCD4L

    /** Default fill for new text boxes. */
    const val DefaultFill: Long = White

    val Palette: List<Long> = listOf(
        White,
        Black,
        Yellow,
        Red,
        Blue,
        Green,
        Orange,
        Pink,
        Purple,
        Cyan,
    )
}
/**
 * Pack ARGB [Long] into Compose sRGB [androidx.compose.ui.graphics.Color].
 * Do NOT use [androidx.compose.ui.graphics.Color] ULong ctor — that expects ColorLong (color-space bits),
 * which crashes with ArrayIndexOutOfBoundsException on plain 0xAARRGGBB values.
 */
fun Long.toComposeColor(): androidx.compose.ui.graphics.Color =
    androidx.compose.ui.graphics.Color((this and 0xFFFFFFFFL).toInt())
