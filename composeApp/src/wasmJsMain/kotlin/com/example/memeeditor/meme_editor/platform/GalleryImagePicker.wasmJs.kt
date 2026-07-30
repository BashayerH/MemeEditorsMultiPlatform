package com.example.memeeditor.meme_editor.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap

@Composable
actual fun rememberGalleryImagePicker(
    onPicked: (filePath: String) -> Unit,
    onError: (message: String) -> Unit,
    onCancelled: () -> Unit,
    onProcessingStarted: () -> Unit,
): () -> Unit = remember {
    { onError("Gallery pick is not supported on this platform") }
}

actual fun readFileBytes(path: String): ByteArray = error("Not supported")

actual fun decodeImageBitmap(bytes: ByteArray): ImageBitmap = error("Not supported")
