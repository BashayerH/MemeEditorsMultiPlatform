package com.example.memeeditor.meme_editor.presentaion.util

import androidx.compose.ui.unit.TextUnit
import platform.UIKit.UIScreen

actual fun TextUnit.toPx(): Float {
    return this.value * UIScreen.mainScreen.scale.toFloat()
}