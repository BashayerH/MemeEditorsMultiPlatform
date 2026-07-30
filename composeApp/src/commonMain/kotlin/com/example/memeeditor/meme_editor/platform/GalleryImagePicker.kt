package com.example.memeeditor.meme_editor.platform

import androidx.compose.runtime.Composable

/**
 * Returns a launcher that opens the system image picker.
 * Success delivers an absolute path to a resized JPEG in app cache.
 */
@Composable
expect fun rememberGalleryImagePicker(
    onPicked: (filePath: String) -> Unit,
    onError: (message: String) -> Unit,
    onCancelled: () -> Unit = {},
    onProcessingStarted: () -> Unit = {},
): () -> Unit
