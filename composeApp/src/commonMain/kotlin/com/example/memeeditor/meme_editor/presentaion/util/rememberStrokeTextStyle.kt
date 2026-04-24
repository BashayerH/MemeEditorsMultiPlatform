package com.example.memeeditor.meme_editor.presentaion.util

import com.example.memeeditor.core.theme.Fonts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun rememberStrokeTextStyle(
    fontSize: TextUnit = 36.sp,
    lineHeight: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily = Fonts.Impact,
    strokeColor: Color = Color.Black,
    strokeWidth: Dp = 3.dp,
    textAlign: TextAlign = TextAlign.Center
): TextStyle {
    val density = LocalDensity.current
    return remember(fontSize, lineHeight, fontWeight, fontFamily, strokeWidth, strokeColor, textAlign) {
        TextStyle(
            color = strokeColor,
            textAlign = textAlign,
            fontSize = fontSize,
            lineHeight = lineHeight,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            drawStyle = Stroke(
                width = with(density) { strokeWidth.toPx() },
                miter = 10f,
                join = StrokeJoin.Round
            )
        )
    }
}