package com.example.memeeditor.meme_editor.platform

import androidx.compose.runtime.Composable

/**
 * Returns a launcher that ensures write access before saving to the gallery.
 * On modern Android / iOS this grants immediately.
 */
@Composable
expect fun rememberGallerySavePermission(
    onGranted: () -> Unit,
    onDenied: () -> Unit,
): () -> Unit
