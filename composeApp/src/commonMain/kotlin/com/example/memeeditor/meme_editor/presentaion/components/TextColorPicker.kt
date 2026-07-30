package com.example.memeeditor.meme_editor.presentaion.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.memeeditor.meme_editor.presentaion.MemeTextColors
import com.example.memeeditor.meme_editor.presentaion.toComposeColor
import memeeditor.composeapp.generated.resources.Res
import memeeditor.composeapp.generated.resources.color_black
import memeeditor.composeapp.generated.resources.color_blue
import memeeditor.composeapp.generated.resources.color_cyan
import memeeditor.composeapp.generated.resources.color_green
import memeeditor.composeapp.generated.resources.color_orange
import memeeditor.composeapp.generated.resources.color_pink
import memeeditor.composeapp.generated.resources.color_purple
import memeeditor.composeapp.generated.resources.color_red
import memeeditor.composeapp.generated.resources.color_white
import memeeditor.composeapp.generated.resources.color_yellow
import memeeditor.composeapp.generated.resources.text_color
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TextColorPicker(
    selectedColorArgb: Long,
    onColorSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = stringResource(Res.string.text_color)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .semantics { contentDescription = label },
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MemeTextColors.Palette.forEach { colorArgb ->
            val isSelected = colorArgb == selectedColorArgb
            val swatch = colorArgb.toComposeColor()
            val colorName = stringResource(colorNameResource(colorArgb))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(swatch)
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        shape = CircleShape,
                    )
                    .clickable { onColorSelected(colorArgb) }
                    .semantics {
                        contentDescription = "$label $colorName"
                    },
            )
        }
    }
}

private fun colorNameResource(colorArgb: Long): StringResource = when (colorArgb) {
    MemeTextColors.White -> Res.string.color_white
    MemeTextColors.Black -> Res.string.color_black
    MemeTextColors.Yellow -> Res.string.color_yellow
    MemeTextColors.Red -> Res.string.color_red
    MemeTextColors.Blue -> Res.string.color_blue
    MemeTextColors.Green -> Res.string.color_green
    MemeTextColors.Orange -> Res.string.color_orange
    MemeTextColors.Pink -> Res.string.color_pink
    MemeTextColors.Purple -> Res.string.color_purple
    MemeTextColors.Cyan -> Res.string.color_cyan
    else -> Res.string.text_color
}
