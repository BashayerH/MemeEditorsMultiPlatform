package com.example.memeeditor.meme_editor.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberGallerySavePermission(
    onGranted: () -> Unit,
    onDenied: () -> Unit,
): () -> Unit = remember(onGranted) { { onGranted() } }
