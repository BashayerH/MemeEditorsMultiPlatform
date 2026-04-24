package com.example.memeeditor.meme_editor.presentaion.util


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.memeeditor.core.theme.Fonts

@Composable
fun rememberFillTextStyle(
    fontSize: TextUnit = 36.sp,
    lineHeight: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily = Fonts.Impact,
    fillColor: Color = Color.White,
    textAlign: TextAlign = TextAlign.Center
): TextStyle {
    return remember(fontSize, lineHeight, fontWeight, fontFamily, fillColor, textAlign) {
        TextStyle(
            color = fillColor,
            textAlign = textAlign,
            fontSize = fontSize,
            lineHeight = lineHeight,
            fontWeight = fontWeight,
            fontFamily = fontFamily
        )
    }
}