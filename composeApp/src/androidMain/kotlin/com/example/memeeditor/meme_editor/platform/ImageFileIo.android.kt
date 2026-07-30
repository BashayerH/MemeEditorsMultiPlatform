package com.example.memeeditor.meme_editor.platform

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File

actual fun readFileBytes(path: String): ByteArray = File(path).readBytes()

actual fun decodeImageBitmap(bytes: ByteArray): ImageBitmap {
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        ?: error("Could not decode image bytes")
    return bitmap.asImageBitmap()
}
