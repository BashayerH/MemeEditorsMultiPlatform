package com.example.memeeditor.meme_editor.platform

expect fun isGallerySaveSupported(): Boolean

expect suspend fun saveJpegFileToGallery(filePath: String, displayName: String): Result<Unit>
