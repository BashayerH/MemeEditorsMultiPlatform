package com.example.memeeditor.core.presentaion

import org.jetbrains.compose.resources.DrawableResource

/**
 * Background for the meme editor: bundled template or user-picked cache JPEG.
 */
sealed interface MemeBackground {
    data class Template(val template: MemesTemplate) : MemeBackground
    data class CustomImage(val filePath: String) : MemeBackground
}

data class MemesTemplate(
    val id: String,
    val drawableResource: DrawableResource,
)
