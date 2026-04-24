package com.example.memeeditor.meme_editor.platform

actual fun isGallerySaveSupported(): Boolean = false

actual suspend fun saveJpegFileToGallery(filePath: String, displayName: String): Result<Unit> =
    Result.failure(UnsupportedOperationException("Gallery save is not used on iOS; use Share to save or send the meme."))
