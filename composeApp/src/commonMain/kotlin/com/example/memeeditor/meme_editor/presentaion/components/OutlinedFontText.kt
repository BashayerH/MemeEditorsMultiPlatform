package com.example.memeeditor.meme_editor.presentaion.components


import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.memeeditor.core.theme.MemeCreatorTheme
import com.example.memeeditor.meme_editor.presentaion.util.rememberFillTextStyle
import com.example.memeeditor.meme_editor.presentaion.util.rememberStrokeTextStyle

@Composable
fun OutlinedImpactText(
    text: String,
    strokeTextStyle: TextStyle,
    fillTextStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Text(
            text = text,
            style = strokeTextStyle.copy(textAlign = TextAlign.Center),
            textAlign = TextAlign.Center
        )
        Text(
            text = text,
            style = fillTextStyle.copy(textAlign = TextAlign.Center),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
@Preview
fun OutlinedImpactTextPreview() {
    MemeCreatorTheme {
        OutlinedImpactText(
            text = "HELLO WORLD!",
            strokeTextStyle = rememberStrokeTextStyle(),
            fillTextStyle = rememberFillTextStyle()
        )
    }
}