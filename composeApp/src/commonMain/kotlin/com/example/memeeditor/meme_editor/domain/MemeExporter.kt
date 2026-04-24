@file:OptIn(ExperimentalUuidApi::class)

package com.example.memeeditor.meme_editor.domain

import com.example.memeeditor.meme_editor.presentaion.MemeText


import androidx.compose.ui.unit.IntSize
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface MemeExporter {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun exportMeme(
        backgroundImageBytes: ByteArray,
        memeTexts: List<MemeText>,
        templateSize: IntSize,
        saveToStorageStrategy: SaveToStorageStrategy,
        fileName: String = "meme_${Uuid.random()}.jpg"
    ): Result<String>
}