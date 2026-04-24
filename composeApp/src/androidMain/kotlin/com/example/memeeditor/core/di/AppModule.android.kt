package com.example.memeeditor.core.di

import com.example.memeeditor.meme_editor.data.CacheStorageStrategy
import com.example.memeeditor.meme_editor.data.PlatformMemeExporter
import com.example.memeeditor.meme_editor.domain.MemeExporter
import com.example.memeeditor.meme_editor.domain.SaveToStorageStrategy
import com.example.memeeditor.meme_editor.presentaion.util.PlatformShareSheet
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module


actual val platformAppModule = module {
    factoryOf(::CacheStorageStrategy) bind SaveToStorageStrategy::class
    factoryOf(::PlatformMemeExporter) bind MemeExporter::class
    factoryOf(::PlatformShareSheet)

}