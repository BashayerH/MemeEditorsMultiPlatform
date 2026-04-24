package com.example.memeeditor.meme_editor.domain

interface SaveToStorageStrategy {
    fun getFilePath(fileName: String): String
}