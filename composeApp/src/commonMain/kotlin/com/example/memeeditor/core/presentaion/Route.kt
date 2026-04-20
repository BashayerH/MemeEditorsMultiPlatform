package com.example.memeeditor.core.presentaion

import kotlinx.serialization.Serializable


sealed interface Route {

    @Serializable
    data object MemeGallery: Route

    @Serializable
    data class MemeEditor(val templateId: String): Route
}