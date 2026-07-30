package com.example.memeeditor.meme_editor.platform

/** Read raw bytes from an absolute file path (shared for custom backgrounds). */
expect fun readFileBytes(path: String): ByteArray

/** Decode image bytes into a Compose [androidx.compose.ui.graphics.ImageBitmap]. */
expect fun decodeImageBitmap(bytes: ByteArray): androidx.compose.ui.graphics.ImageBitmap
