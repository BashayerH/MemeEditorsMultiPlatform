package com.example.memeeditor.meme_editor.platform

/** Thrown / returned when the user denies photo-library add access. */
class GalleryPermissionDeniedException(
    message: String = "Photo library permission denied",
) : Exception(message)
