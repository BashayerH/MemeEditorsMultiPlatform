package com.example.memeeditor.meme_editor.platform

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.mp.KoinPlatform
import java.io.File
import java.io.FileInputStream

actual fun isGallerySaveSupported(): Boolean = true

actual suspend fun saveJpegFileToGallery(filePath: String, displayName: String): Result<Unit> =
    withContext(Dispatchers.IO) {
        try {
            val context = KoinPlatform.getKoin().get<Context>()
            val file = File(filePath)
            if (!file.exists()) {
                return@withContext Result.failure(IllegalStateException("File not found"))
            }
            val resolver = context.contentResolver
            val name = if (displayName.endsWith(".jpg", ignoreCase = true)) {
                displayName
            } else {
                "$displayName.jpg"
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        android.os.Environment.DIRECTORY_PICTURES + "/MemeEditor"
                    )
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
                val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                val uri = resolver.insert(collection, values)
                    ?: return@withContext Result.failure(IllegalStateException("Could not create gallery entry"))
                resolver.openOutputStream(uri)?.use { out ->
                    FileInputStream(file).copyTo(out)
                } ?: return@withContext Result.failure(IllegalStateException("Could not open gallery stream"))
                values.clear()
                values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            } else {
                @Suppress("DEPRECATION")
                val inserted = MediaStore.Images.Media.insertImage(
                    resolver,
                    file.absolutePath,
                    name.removeSuffix(".jpg"),
                    null,
                )
                if (inserted == null) {
                    return@withContext Result.failure(IllegalStateException("Could not save to gallery"))
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
