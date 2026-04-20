package com.example.memeeditor.core.di

import com.example.memeeditor.meme_editor.presentaion.MemeEditorViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val  appModule  = module {
    viewModelOf(::MemeEditorViewModel)
}