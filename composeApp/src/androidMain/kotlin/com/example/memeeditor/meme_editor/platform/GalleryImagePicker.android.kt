package com.example.memeeditor.meme_editor.platform

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

private const val MAX_IMAGE_DIMENSION = 2048
private const val JPEG_QUALITY = 90

@Composable
actual fun rememberGalleryImagePicker(
    onPicked: (filePath: String) -> Unit,
    onError: (message: String) -> Unit,
    onCancelled: () -> Unit,
    onProcessingStarted: () -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        if (uri == null) {
            onCancelled()
            return@rememberLauncherForActivityResult
        }
        onProcessingStarted()
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { copyAndCompressToCache(context, uri) }
            }
            result.fold(
                onSuccess = onPicked,
                onFailure = { e ->
                    onError(e.message ?: "Could not load image")
                },
            )
        }
    }

    return remember(launcher) {
        {
            launcher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }
}

private fun copyAndCompressToCache(context: Context, uri: Uri): String {
    val resolver = context.contentResolver
    val mime = resolver.getType(uri).orEmpty()
    if (mime.isNotEmpty() && !mime.startsWith("image/") && mime != "application/octet-stream") {
        error("Unsupported format. Choose a JPEG or PNG image.")
    }

    // Single read into a temp file — some providers fail if the stream is opened twice.
    val rawFile = File(context.cacheDir, "picked_raw_${System.currentTimeMillis()}")
    resolver.openInputStream(uri)?.use { input ->
        FileOutputStream(rawFile).use { output -> input.copyTo(output) }
    } ?: error("Could not read image")

    if (!rawFile.exists() || rawFile.length() == 0L) {
        rawFile.delete()
        error("Could not read image")
    }

    val bitmap = try {
        decodeBitmapFromFile(rawFile, MAX_IMAGE_DIMENSION)
    } finally {
        rawFile.delete()
    } ?: error("Could not decode image. Try JPEG or PNG.")

    val scaled = scaleDownIfNeeded(bitmap, MAX_IMAGE_DIMENSION)
    if (scaled !== bitmap) {
        bitmap.recycle()
    }

    val outFile = File(context.cacheDir, "picked_${System.currentTimeMillis()}.jpg")
    FileOutputStream(outFile).use { stream ->
        if (!scaled.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream)) {
            scaled.recycle()
            error("Could not compress image")
        }
    }
    scaled.recycle()
    return outFile.absolutePath
}

private fun decodeBitmapFromFile(file: File, maxDim: Int): Bitmap? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        return try {
            val source = ImageDecoder.createSource(file)
            ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                val w = info.size.width
                val h = info.size.height
                val longest = max(w, h)
                if (longest > maxDim) {
                    val sample = (longest.toFloat() / maxDim).toInt().coerceAtLeast(1)
                    decoder.setTargetSampleSize(sample)
                }
                decoder.isMutableRequired = false
            }
        } catch (_: Exception) {
            decodeWithBitmapFactory(file, maxDim)
        }
    }
    return decodeWithBitmapFactory(file, maxDim)
}

private fun decodeWithBitmapFactory(file: File, maxDim: Int): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(file.absolutePath, bounds)
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
    val opts = BitmapFactory.Options().apply {
        inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, maxDim)
    }
    return BitmapFactory.decodeFile(file.absolutePath, opts)
}

private fun calculateInSampleSize(width: Int, height: Int, maxDim: Int): Int {
    var sample = 1
    val longest = max(width, height)
    while (longest / sample > maxDim * 2) {
        sample *= 2
    }
    return sample
}

private fun scaleDownIfNeeded(bitmap: Bitmap, maxDim: Int): Bitmap {
    val longest = max(bitmap.width, bitmap.height)
    if (longest <= maxDim) return bitmap
    val scale = maxDim.toFloat() / longest
    val w = (bitmap.width * scale).toInt().coerceAtLeast(1)
    val h = (bitmap.height * scale).toInt().coerceAtLeast(1)
    return Bitmap.createScaledBitmap(bitmap, w, h, true)
}
