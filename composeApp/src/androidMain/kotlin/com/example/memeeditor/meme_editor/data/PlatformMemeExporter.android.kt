package com.example.memeeditor.meme_editor.data


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.ui.unit.IntSize
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.withTranslation
import com.example.memeeditor.R
import com.example.memeeditor.meme_editor.domain.MemeExporter
import com.example.memeeditor.meme_editor.domain.SaveToStorageStrategy
import com.example.memeeditor.meme_editor.presentaion.MemeText
import com.example.memeeditor.meme_editor.presentaion.util.FitCanvasMapping
import com.example.memeeditor.meme_editor.presentaion.util.MemeRenderCalculator
import com.example.memeeditor.meme_editor.presentaion.util.ScaledMemeText
import com.example.memeeditor.meme_editor.presentaion.util.containsArabicScript
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

actual class PlatformMemeExporter(
    private val context: Context
) : MemeExporter {

    private val memeRenderCalculator = MemeRenderCalculator(
        density = context.resources.displayMetrics.density,
        fontScale = context.resources.configuration.fontScale
    )

    actual override suspend fun exportMeme(
        backgroundImageBytes: ByteArray,
        memeTexts: List<MemeText>,
        templateSize: IntSize,
        saveToStorageStrategy: SaveToStorageStrategy,
        fileName: String
    ) = withContext(Dispatchers.IO) {
        var bitmap: Bitmap? = null
        var outputBitmap: Bitmap? = null
        try {
            bitmap = BitmapFactory.decodeByteArray(
                backgroundImageBytes,
                0,
                backgroundImageBytes.size
            )
            outputBitmap = renderMeme(
                background = bitmap,
                memeTexts = memeTexts,
                templateSize = templateSize
            )

            val filePath = saveToStorageStrategy.getFilePath(fileName)
            val file = File(filePath)
            FileOutputStream(file).use { out ->
                outputBitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    90,
                    out
                )
            }

            Result.success(file.absolutePath)
        } catch(e: Exception) {
            coroutineContext.ensureActive()
            Result.failure(e)
        } finally {
            bitmap?.recycle()
            outputBitmap?.recycle()
        }
    }

    private suspend fun renderMeme(
        background: Bitmap,
        memeTexts: List<MemeText>,
        templateSize: IntSize
    ): Bitmap = withContext(Dispatchers.Default) {
        val output = background.copy(
            Bitmap.Config.ARGB_8888,
            true
        )
        val canvas = Canvas(output)

        val mapping = FitCanvasMapping.fromTemplateAndBitmap(
            templateSize = templateSize,
            bitmapSize = IntSize(background.width, background.height),
        )

        val scaledMemeTexts = memeTexts.map {
            memeRenderCalculator.calculateScaledMemeText(
                memeText = it,
                mapping = mapping,
                templateSize = templateSize,
            )
        }

        scaledMemeTexts.forEach { scaledMemeText ->
            drawText(canvas, scaledMemeText)
        }

        output
    }

    private fun drawText(canvas: Canvas, memeText: ScaledMemeText) {
        val memeTypeface = memeTypefaceFor(memeText.text)

        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = memeText.strokeWidth
            textSize = memeText.scaledFontSizePx
            typeface = memeTypeface
            color = Color.BLACK
        }
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textSize = memeText.scaledFontSizePx
            typeface = memeTypeface
            color = Color.WHITE
        }

        val strokeLayout = StaticLayout.Builder.obtain(
            memeText.text,
            0,
            memeText.text.length,
            TextPaint(strokePaint),
            memeText.constraintWidth
        )
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setIncludePad(false)
            .build()

        val fillLayout = StaticLayout.Builder.obtain(
            memeText.text,
            0,
            memeText.text.length,
            TextPaint(fillPaint),
            memeText.constraintWidth
        )
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setIncludePad(false)
            .build()

        val textHeight = strokeLayout.height.toFloat()
        val textWidth = (0 until strokeLayout.lineCount)
            .maxOfOrNull { strokeLayout.getLineWidth(it) }
            ?: 0f

        val boxWidth = textWidth + memeText.textPaddingX * 2
        val boxHeight = textHeight + memeText.textPaddingY * 2

        val centerX = memeText.scaledOffset.x + boxWidth / 2f
        val centerY = memeText.scaledOffset.y + boxHeight / 2f

        canvas.withTranslation(centerX, centerY) {
            scale(memeText.scale, memeText.scale)
            rotate(memeText.rotation)

            val textCenteringOffset = (memeText.constraintWidth - textWidth) / 2f
            translate(
                -boxWidth / 2f + memeText.textPaddingX - textCenteringOffset,
                -boxHeight / 2f + memeText.textPaddingY
            )

            strokeLayout.draw(this)
            fillLayout.draw(this)
        }
    }

    private fun memeTypefaceFor(text: String): Typeface {
        val raw = if (text.containsArabicScript()) {
            ResourcesCompat.getFont(context, R.font.tajawal)
        } else {
            ResourcesCompat.getFont(context, R.font.impact)
        } ?: Typeface.DEFAULT_BOLD
        return if (text.containsArabicScript()) {
            Typeface.create(raw, Typeface.BOLD)
        } else {
            raw
        }
    }
}