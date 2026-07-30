package com.example.memeeditor.meme_editor.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.example.memeeditor.ComposeUiPresentationAnchor
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Image
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.create
import platform.Foundation.writeToFile
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIViewController
import platform.UIKit.UIWindowScene
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.math.max

private const val MAX_IMAGE_DIMENSION = 2048.0
private const val JPEG_QUALITY = 0.9

@Composable
actual fun rememberGalleryImagePicker(
    onPicked: (filePath: String) -> Unit,
    onError: (message: String) -> Unit,
    onCancelled: () -> Unit,
    onProcessingStarted: () -> Unit,
): () -> Unit {
    val controller = remember(onPicked, onError, onCancelled, onProcessingStarted) {
        GalleryPickerController(onPicked, onError, onCancelled, onProcessingStarted)
    }
    return remember(controller) { { controller.present() } }
}

@OptIn(ExperimentalForeignApi::class)
private class GalleryPickerController(
    private val onPicked: (String) -> Unit,
    private val onError: (String) -> Unit,
    private val onCancelled: () -> Unit,
    private val onProcessingStarted: () -> Unit,
) {
    private var delegateRef: PHPickerDelegate? = null

    fun present() {
        val config = PHPickerConfiguration().apply {
            setFilter(PHPickerFilter.imagesFilter)
            setSelectionLimit(1)
        }
        val picker = PHPickerViewController(configuration = config)
        val delegate = PHPickerDelegate(
            onPicked = { path ->
                delegateRef = null
                onPicked(path)
            },
            onError = { msg ->
                delegateRef = null
                onError(msg)
            },
            onCancelled = {
                delegateRef = null
                onCancelled()
            },
            onProcessingStarted = onProcessingStarted,
        )
        delegateRef = delegate
        picker.delegate = delegate
        val presenter = resolvePresenter() ?: run {
            onError("Could not open photo library")
            return
        }
        presenter.presentViewController(picker, animated = true, completion = null)
    }

    private fun resolvePresenter(): UIViewController? {
        ComposeUiPresentationAnchor.rootViewController?.let { return it.topMost() }
        val app = UIApplication.sharedApplication
        for (raw in app.connectedScenes) {
            val scene = raw as? UIWindowScene ?: continue
            if (scene.activationState != UISceneActivationStateForegroundActive) continue
            scene.keyWindow?.rootViewController?.let { return it.topMost() }
        }
        return null
    }

    private fun UIViewController.topMost(): UIViewController {
        var current = this
        while (true) {
            val next = current.presentedViewController ?: break
            current = next
        }
        return current
    }
}

@OptIn(ExperimentalForeignApi::class)
private class PHPickerDelegate(
    private val onPicked: (String) -> Unit,
    private val onError: (String) -> Unit,
    private val onCancelled: () -> Unit,
    private val onProcessingStarted: () -> Unit,
) : NSObject(), PHPickerViewControllerDelegateProtocol {

    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.dismissViewControllerAnimated(true, completion = null)
        val result = didFinishPicking.firstOrNull() as? PHPickerResult
        if (result == null) {
            onCancelled()
            return
        }
        val provider = result.itemProvider
        if (!provider.hasItemConformingToTypeIdentifier("public.image")) {
            onError("Unsupported format. Choose a JPEG or PNG image.")
            return
        }
        onProcessingStarted()
        provider.loadDataRepresentationForTypeIdentifier("public.image") { data, error ->
            dispatch_async(dispatch_get_main_queue()) {
                if (error != null || data == null) {
                    onError(error?.localizedDescription ?: "Could not load image")
                    return@dispatch_async
                }
                try {
                    val path = compressPickerDataToCache(data)
                    onPicked(path)
                } catch (t: Throwable) {
                    onError(t.message ?: "Could not process image")
                }
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun compressPickerDataToCache(data: NSData): String {
    val image = UIImage.imageWithData(data) ?: error("Invalid image")
    val resized = resizeIfNeeded(image, MAX_IMAGE_DIMENSION)
    val jpeg = UIImageJPEGRepresentation(resized, JPEG_QUALITY)
        ?: error("Could not compress image")
    val path = NSTemporaryDirectory() + "picked_${kotlin.random.Random.nextLong()}.jpg"
    if (!jpeg.writeToFile(path, atomically = true)) {
        error("Could not save image")
    }
    return path
}

@OptIn(ExperimentalForeignApi::class)
private fun resizeIfNeeded(image: UIImage, maxDim: Double): UIImage {
    val size = image.size
    val width = size.useContents { width }
    val height = size.useContents { height }
    val longest = max(width, height)
    if (longest <= maxDim) return image
    val scale = maxDim / longest
    val newW = width * scale
    val newH = height * scale
    UIGraphicsBeginImageContextWithOptions(CGSizeMake(newW, newH), false, 1.0)
    image.drawInRect(CGRectMake(0.0, 0.0, newW, newH))
    val result = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return result ?: image
}

@OptIn(ExperimentalForeignApi::class)
actual fun readFileBytes(path: String): ByteArray {
    val data = NSData.create(contentsOfFile = path) ?: error("File not found")
    val length = data.length.toInt()
    val bytes = ByteArray(length)
    if (length > 0) {
        bytes.usePinned { pinned ->
            platform.posix.memcpy(pinned.addressOf(0), data.bytes, data.length)
        }
    }
    return bytes
}

actual fun decodeImageBitmap(bytes: ByteArray): ImageBitmap {
    return Image.makeFromEncoded(bytes).toComposeImageBitmap()
}
