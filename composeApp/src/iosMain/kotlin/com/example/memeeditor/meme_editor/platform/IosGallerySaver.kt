package com.example.memeeditor.meme_editor.platform

import kotlinx.cinterop.ExperimentalForeignApi
import com.example.memeeditor.meme_editor.platform.GalleryPermissionDeniedException
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSData
import platform.Foundation.create
import platform.Photos.PHAssetChangeRequest
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusDenied
import platform.Photos.PHAuthorizationStatusLimited
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.Photos.PHAuthorizationStatusRestricted
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIImage
import kotlin.coroutines.resume

internal object IosGallerySaver {

    @OptIn(ExperimentalForeignApi::class)
    suspend fun save(filePath: String, displayName: String): Result<Unit> {
        val data = NSData.create(contentsOfFile = filePath)
            ?: return Result.failure(IllegalStateException("File not found"))
        val image = UIImage.imageWithData(data)
            ?: return Result.failure(IllegalStateException("Invalid image data"))

        val authorized = ensureAddOnlyAuthorization()
        if (!authorized) {
            return Result.failure(GalleryPermissionDeniedException())
        }

        return suspendCancellableCoroutine { cont ->
            PHPhotoLibrary.sharedPhotoLibrary().performChanges(
                {
                    PHAssetChangeRequest.creationRequestForAssetFromImage(image)
                },
                completionHandler = { success, error ->
                    if (success) {
                        cont.resume(Result.success(Unit))
                    } else {
                        cont.resume(
                            Result.failure(
                                IllegalStateException(
                                    error?.localizedDescription ?: "Could not save to gallery"
                                )
                            )
                        )
                    }
                },
            )
        }
    }

    private suspend fun ensureAddOnlyAuthorization(): Boolean {
        val status = PHPhotoLibrary.authorizationStatusForAccessLevel(
            platform.Photos.PHAccessLevelAddOnly
        )
        return when (status) {
            PHAuthorizationStatusAuthorized, PHAuthorizationStatusLimited -> true
            PHAuthorizationStatusDenied, PHAuthorizationStatusRestricted -> false
            PHAuthorizationStatusNotDetermined -> requestAddOnly()
            else -> false
        }
    }

    private suspend fun requestAddOnly(): Boolean =
        suspendCancellableCoroutine { cont ->
            PHPhotoLibrary.requestAuthorizationForAccessLevel(
                platform.Photos.PHAccessLevelAddOnly
            ) { newStatus ->
                cont.resume(
                    newStatus == PHAuthorizationStatusAuthorized ||
                        newStatus == PHAuthorizationStatusLimited
                )
            }
        }
}
