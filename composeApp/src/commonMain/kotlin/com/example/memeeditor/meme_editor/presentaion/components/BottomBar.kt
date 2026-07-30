package com.example.memeeditor.meme_editor.presentaion.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import memeeditor.composeapp.generated.resources.Res
import memeeditor.composeapp.generated.resources.add_text
import memeeditor.composeapp.generated.resources.edit_text
import memeeditor.composeapp.generated.resources.save_meme
import memeeditor.composeapp.generated.resources.save_to_gallery
import org.jetbrains.compose.resources.stringResource

@Composable
fun BottomBar(
    onAddTextClick: () -> Unit,
    onSaveClick: () -> Unit,
    onSaveToGalleryClick: (() -> Unit)? = null,
    onEditTextClick: (() -> Unit)? = null,
    exportEnabled: Boolean = true,
    actionsEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val iconColors = IconButtonDefaults.iconButtonColors(
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
    )
    val tonal = IconButtonDefaults.filledIconButtonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.38f),
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onAddTextClick,
            enabled = actionsEnabled,
            modifier = Modifier.size(44.dp),
            colors = iconColors
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(Res.string.add_text)
            )
        }
        onEditTextClick?.let { editClick ->
            IconButton(
                onClick = editClick,
                enabled = actionsEnabled,
                modifier = Modifier.size(44.dp),
                colors = iconColors
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(Res.string.edit_text)
                )
            }
        }
        onSaveToGalleryClick?.let { galleryClick ->
            IconButton(
                onClick = galleryClick,
                enabled = actionsEnabled && exportEnabled,
                modifier = Modifier.size(44.dp),
                colors = iconColors
            ) {
                Icon(
                    imageVector = Icons.Filled.Image,
                    contentDescription = stringResource(Res.string.save_to_gallery)
                )
            }
        }
        FilledIconButton(
            onClick = onSaveClick,
            enabled = actionsEnabled && exportEnabled,
            modifier = Modifier.size(48.dp),
            colors = tonal
        ) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = stringResource(Res.string.save_meme)
            )
        }
    }
}
