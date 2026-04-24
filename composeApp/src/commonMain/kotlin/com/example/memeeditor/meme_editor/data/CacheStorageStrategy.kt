package com.example.memeeditor.meme_editor.data

import com.example.memeeditor.meme_editor.domain.SaveToStorageStrategy


expect class CacheStorageStrategy: SaveToStorageStrategy {
    override fun getFilePath(fileName: String): String
}