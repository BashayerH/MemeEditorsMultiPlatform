@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")


package com.example.memeeditor.meme_editor.data



import androidx.compose.ui.unit.IntSize
import com.example.memeeditor.meme_editor.domain.MemeExporter
import com.example.memeeditor.meme_editor.domain.SaveToStorageStrategy
import com.example.memeeditor.meme_editor.presentaion.MemeText


expect class PlatformMemeExporter: MemeExporter {
    override suspend fun exportMeme(
        backgroundImageBytes: ByteArray,
        memeTexts: List<MemeText>,
        templateSize: IntSize,
        saveToStorageStrategy: SaveToStorageStrategy,
        fileName: String
    ): Result<String>
}