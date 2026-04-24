package com.example.memeeditor.meme_editor.presentaion.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.memeeditor.meme_editor.presentaion.util.rememberFillTextStyle
import com.example.memeeditor.meme_editor.presentaion.util.rememberStrokeTextStyle

@Composable
fun OutlinedFontTextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    strokeTextStyle: TextStyle = rememberStrokeTextStyle(),
    fillTextStyle: TextStyle = rememberFillTextStyle(),
    maxWidth: Dp? = null,
    maxHeight: Dp? = null
) {
    val density = LocalDensity.current
    val measurer = rememberTextMeasurer()
    val maxW = if (maxWidth != null) with(density) { maxWidth.roundToPx() } else Int.MAX_VALUE
    val maxH = if (maxHeight != null) with(density) { maxHeight.roundToPx() } else Int.MAX_VALUE

    val contentSize: IntSize = remember(text, strokeTextStyle, fillTextStyle, maxW, maxH) {
        val fillLayout = measurer.measure(
            text = text,
            style = fillTextStyle,
            constraints = Constraints(maxWidth = maxW, maxHeight = maxH)
        )
        val strokeLayout = measurer.measure(
            text = text,
            style = strokeTextStyle,
            constraints = Constraints(maxWidth = maxW, maxHeight = maxH)
        )
        IntSize(
            width = maxOf(fillLayout.size.width, strokeLayout.size.width),
            height = maxOf(fillLayout.size.height, strokeLayout.size.height)
        )
    }

    val wDpRaw = with(density) { contentSize.width.toDp() }.coerceAtLeast(8.dp)
    val wDp = if (maxWidth != null) wDpRaw.coerceAtMost(maxWidth) else wDpRaw
    val hDpRaw = with(density) { contentSize.height.toDp() }.coerceAtLeast(1.dp)
    val hDp = if (maxHeight != null) hDpRaw.coerceAtMost(maxHeight) else hDpRaw

    val widthMod = Modifier.width(wDp)
    val heightMod = Modifier.height(hDp)

    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        textStyle = fillTextStyle.copy(
            color = Color.Transparent,
            textAlign = TextAlign.Center
        ),
        cursorBrush = SolidColor(Color.White),
        singleLine = false,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .then(widthMod)
                    .then(heightMod)
            ) {
                Text(
                    text = text,
                    style = strokeTextStyle.copy(textAlign = TextAlign.Center),
                    textAlign = TextAlign.Center,
                    maxLines = Int.MAX_VALUE,
                    softWrap = true
                )
                Text(
                    text = text,
                    style = fillTextStyle.copy(textAlign = TextAlign.Center),
                    textAlign = TextAlign.Center,
                    maxLines = Int.MAX_VALUE,
                    softWrap = true
                )
                Box(Modifier.fillMaxSize()) {
                    innerTextField()
                }
            }
        },
        modifier = modifier
            .then(widthMod)
            .then(heightMod)
    )
}
