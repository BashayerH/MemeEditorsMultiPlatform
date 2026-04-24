package com.example.memeeditor.meme_editor.presentaion.util


import androidx.compose.ui.geometry.Offset
import com.example.memeeditor.meme_editor.presentaion.MemeText

data class ScaledMemeText(
    val text: String,
    val scaledOffset: Offset,
    val scaledFontSizePx: Float,
    val strokeWidth: Float,
    val constraintWidth: Int,
    val textPaddingX: Float,
    val textPaddingY: Float,
    val rotation: Float,
    val scale: Float,
    val originalText: MemeText
)