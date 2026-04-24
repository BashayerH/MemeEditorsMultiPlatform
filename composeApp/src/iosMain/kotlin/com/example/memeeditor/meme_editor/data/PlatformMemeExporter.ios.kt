@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package com.example.memeeditor.meme_editor.data

import androidx.compose.ui.unit.IntSize
import com.example.memeeditor.meme_editor.domain.MemeExporter
import com.example.memeeditor.meme_editor.domain.SaveToStorageStrategy
import com.example.memeeditor.meme_editor.presentaion.MemeText
import com.example.memeeditor.meme_editor.presentaion.util.FitCanvasMapping
import com.example.memeeditor.meme_editor.presentaion.util.MemeRenderCalculator
import com.example.memeeditor.meme_editor.presentaion.util.ScaledMemeText
import com.example.memeeditor.meme_editor.presentaion.util.containsArabicScript
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import platform.UIKit.UIScreen
import kotlinx.coroutines.withContext
import memeeditor.composeapp.generated.resources.Res
import platform.CoreFoundation.CFDataCreate
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreGraphics.CGDataProviderCreateWithCFData
import platform.CoreGraphics.CGFontCreateWithDataProvider
import platform.CoreText.CTFontManagerRegisterGraphicsFont
import platform.Foundation.NSData
import platform.Foundation.writeToFile
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGContextRef
import platform.CoreGraphics.CGContextRestoreGState
import platform.CoreGraphics.CGContextRotateCTM
import platform.CoreGraphics.CGContextSaveGState
import platform.CoreGraphics.CGContextScaleCTM
import platform.CoreGraphics.CGContextTranslateCTM
import platform.CoreGraphics.CGFloat
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.Foundation.create
import platform.UIKit.NSFontAttributeName
import platform.UIKit.NSForegroundColorAttributeName
import platform.UIKit.NSLineBreakByWordWrapping
import platform.UIKit.NSMutableParagraphStyle
import platform.UIKit.NSParagraphStyleAttributeName
import platform.UIKit.NSStrokeColorAttributeName
import platform.UIKit.NSStrokeWidthAttributeName
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.NSWritingDirectionNatural
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetCurrentContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.boundingRectWithSize
import platform.UIKit.drawWithRect
import kotlin.math.PI
import kotlin.math.max
import platform.CoreFoundation.CFErrorRefVar

actual class PlatformMemeExporter : MemeExporter {

    private companion object {
        private val fontRegistrationMutex = Mutex()
        private var composeMemeFontsRegistered: Boolean = false
    }

    private val memeRenderCalculator = MemeRenderCalculator(
        density = UIScreen.mainScreen.scale.toFloat(),
        fontScale = 1f
    )

    actual override suspend fun exportMeme(
        backgroundImageBytes: ByteArray,
        memeTexts: List<MemeText>,
        templateSize: IntSize,
        saveToStorageStrategy: SaveToStorageStrategy,
        fileName: String
    ): Result<String> = withContext(Dispatchers.Default) {
        try {
            ensureComposeMemeFontsRegistered()
            val backgroundImage = createBackgroundImage(
                imageBytes = backgroundImageBytes
            ) ?: return@withContext Result.failure(Exception("Failed to create background image"))
            val outputImage = renderMeme(
                backgroundImage = backgroundImage,
                memeTexts = memeTexts,
                templateSize = templateSize
            ) ?: return@withContext Result.failure(Exception("Failed to create output image"))

            saveMemeToFile(
                image = outputImage,
                fileName = fileName,
                saveToStorageStrategy = saveToStorageStrategy
            )
        } catch(e: Exception) {
            coroutineContext.ensureActive()
            Result.failure(e)
        }
    }

    private fun saveMemeToFile(
        image: UIImage,
        fileName: String,
        saveToStorageStrategy: SaveToStorageStrategy
    ): Result<String> {
        val jpegData = UIImageJPEGRepresentation(image, 90.0)
            ?: return Result.failure(Exception("Failed to create JPEG image"))

        val filePath = saveToStorageStrategy.getFilePath(fileName)
        val saved = jpegData.writeToFile(filePath, atomically = true)

        return if(saved) {
            Result.success(filePath)
        } else {
            Result.failure(Exception("Failed to save file."))
        }
    }

    /** Loads `Res.font.tajawal` / `Res.font.impact` bytes and registers them with Core Text (same files as Fonts.kt). */
    private suspend fun ensureComposeMemeFontsRegistered() {
        if (composeMemeFontsRegistered) return
        val tajBytes = Res.readBytes("font/tajawal.ttf")
        val impBytes = Res.readBytes("font/impact.ttf")
        fontRegistrationMutex.withLock {
            if (composeMemeFontsRegistered) return@withLock
            registerFontBytesForProcess(tajBytes)
            registerFontBytesForProcess(impBytes)
            composeMemeFontsRegistered = true
        }
    }

    private fun registerFontBytesForProcess(bytes: ByteArray) {
        bytes.usePinned { pinned ->
            val cfData = CFDataCreate(
                allocator = kCFAllocatorDefault,
                bytes = pinned.addressOf(0).reinterpret<UByteVar>(),
                length = bytes.size.toLong(),
            ) ?: return@usePinned
            val provider = CGDataProviderCreateWithCFData(cfData) ?: return@usePinned
            val cgFont = CGFontCreateWithDataProvider(provider) ?: return@usePinned
            memScoped {
                val err = alloc<CFErrorRefVar>()
                CTFontManagerRegisterGraphicsFont(cgFont, err.ptr)
            }
        }
    }

    private fun createBackgroundImage(imageBytes: ByteArray): UIImage? {
        val imageData = imageBytes.usePinned { pinned ->
            NSData.Companion.create(
                bytes = pinned.addressOf(0),
                length = imageBytes.size.toULong()
            )
        }
        return UIImage.imageWithData(imageData)
    }

    private fun renderMeme(
        backgroundImage: UIImage,
        memeTexts: List<MemeText>,
        templateSize: IntSize
    ): UIImage? {
        val pointW = backgroundImage.size.useContents { width }
        val pointH = backgroundImage.size.useContents { height }
        val imageScale = max(backgroundImage.scale.toDouble(), 1.0)
        val pixelW = max((pointW * imageScale).toInt(), 1)
        val pixelH = max((pointH * imageScale).toInt(), 1)
        val bitmapPixelSize = IntSize(width = pixelW, height = pixelH)

        // scale=1 so one context unit = one bitmap pixel (matches Android Canvas + MemeRenderCalculator).
        UIGraphicsBeginImageContextWithOptions(
            CGSizeMake(pixelW.toDouble(), pixelH.toDouble()),
            false,
            1.0
        )

        val context = UIGraphicsGetCurrentContext()
        if(context == null) {
            UIGraphicsEndImageContext()
            return null
        }

        backgroundImage.drawInRect(
            CGRectMake(
                x = 0.0,
                y = 0.0,
                width = pixelW.toDouble(),
                height = pixelH.toDouble()
            )
        )

        val mapping = FitCanvasMapping.fromTemplateAndBitmap(
            templateSize = templateSize,
            bitmapSize = bitmapPixelSize,
        )
        val scaledMemeTexts = memeTexts.map { memeText ->
            memeRenderCalculator.calculateScaledMemeText(
                memeText = memeText,
                mapping = mapping,
                templateSize = templateSize,
            )
        }

        scaledMemeTexts.forEach { memeText ->
            drawText(
                context = context,
                memeText = memeText
            )
        }

        val resultImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        return resultImage
    }

    private fun drawText(context: CGContextRef, memeText: ScaledMemeText) {
        val text = memeText.text
        val textNS = NSString.create(text)
        val font = memeUIFontForExport(text, memeText.scaledFontSizePx.toDouble())
        val paragraphStyle = memeParagraphStyle(text)
        val fontSizePx = max(memeText.scaledFontSizePx.toDouble(), 1.0)
        val strokePercent = memeText.strokeWidth.toDouble() / fontSizePx * 100.0

        // Match Android: stroke pass then fill pass. Single attributed draw with negative stroke
        // can drop Arabic (RTL) fill on UIKit; two passes keep white fill visible.
        @Suppress("UNCHECKED_CAST")
        val outlineAttrs = mapOf(
            NSFontAttributeName to font,
            NSForegroundColorAttributeName to UIColor.blackColor,
            NSStrokeColorAttributeName to UIColor.blackColor,
            NSStrokeWidthAttributeName to NSNumber(strokePercent),
            NSParagraphStyleAttributeName to paragraphStyle,
        ) as Map<Any?, Any?>
        @Suppress("UNCHECKED_CAST")
        val fillAttrs = mapOf(
            NSFontAttributeName to font,
            NSForegroundColorAttributeName to UIColor.whiteColor,
            NSStrokeWidthAttributeName to NSNumber(0.0),
            NSParagraphStyleAttributeName to paragraphStyle,
        ) as Map<Any?, Any?>

        val boundingRect = textNS.boundingRectWithSize(
            size = CGSizeMake(memeText.constraintWidth.toDouble(), CGFloat.MAX_VALUE),
            options = 1L shl 0,
            attributes = outlineAttrs,
            context = null,
        )

        val textHeight = boundingRect.useContents { size.height.toFloat() }
        val textWidth = boundingRect.useContents { size.width.toFloat() }

        val boxWidth = textWidth + memeText.textPaddingX * 2
        val boxHeight = textHeight + memeText.textPaddingY * 2

        val centerX = memeText.scaledOffset.x + boxWidth / 2
        val centerY = memeText.scaledOffset.y + boxHeight / 2

        CGContextSaveGState(context)
        CGContextTranslateCTM(context, centerX.toDouble(), centerY.toDouble())
        CGContextScaleCTM(context, memeText.scale.toDouble(), memeText.scale.toDouble())
        CGContextRotateCTM(context, memeText.rotation * PI / 180.0)

        val textCenteringOffset = (memeText.constraintWidth - textWidth) / 2f
        CGContextTranslateCTM(
            context,
            (-boxWidth / 2f + memeText.textPaddingX - textCenteringOffset).toDouble(),
            (-boxHeight / 2f + memeText.textPaddingY).toDouble(),
        )

        val drawRect = CGRectMake(0.0, 0.0, memeText.constraintWidth.toDouble(), textHeight.toDouble())
        val drawOptions = 1L shl 0
        textNS.drawWithRect(drawRect, options = drawOptions, attributes = outlineAttrs, context = null)
        textNS.drawWithRect(drawRect, options = drawOptions, attributes = fillAttrs, context = null)

        CGContextRestoreGState(context)
    }

    private fun memeParagraphStyle(text: String): NSMutableParagraphStyle {
        return NSMutableParagraphStyle().apply {
            setAlignment(NSTextAlignmentCenter)
            setLineBreakMode(NSLineBreakByWordWrapping)
            if (text.containsArabicScript()) {
                setBaseWritingDirection(NSWritingDirectionNatural)
            }
        }
    }

    /** Bundled tajawal.ttf is PostScript "Tajawal-Medium"; apply bold trait to align with editor ExtraBold. */
    private fun memeUIFontForExport(text: String, pointSize: Double): UIFont {
        if (!text.containsArabicScript()) {
            UIFont.fontWithName("Impact", pointSize)?.let { return it }
            return UIFont.boldSystemFontOfSize(pointSize)
        }
        val base = sequenceOf(
            "Tajawal-Medium",
            "Tajawal-Bold",
            "Tajawal-Regular",
            "Tajawal",
        ).firstNotNullOfOrNull { name -> UIFont.fontWithName(name, pointSize) }
            ?: return UIFont.boldSystemFontOfSize(pointSize)
        return tajwalWithBoldTrait(base) ?: base
    }

    private fun tajwalWithBoldTrait(font: UIFont): UIFont? {
        val desc = font.fontDescriptor
        // UIFontDescriptorTraitBold == 1u shl 1
        val combined = desc.symbolicTraits or 2u
        val withTraits = desc.fontDescriptorWithSymbolicTraits(combined) ?: return null
        return UIFont.fontWithDescriptor(withTraits, font.pointSize)
    }

}