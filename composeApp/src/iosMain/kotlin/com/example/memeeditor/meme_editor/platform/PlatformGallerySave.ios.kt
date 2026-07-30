package com.example.memeeditor.meme_editor.platform

actual fun isGallerySaveSupported(): Boolean = true

actual suspend fun saveJpegFileToGallery(filePath: String, displayName: String): Result<Unit> =
    IosGallerySaver.save(filePath, displayName)
