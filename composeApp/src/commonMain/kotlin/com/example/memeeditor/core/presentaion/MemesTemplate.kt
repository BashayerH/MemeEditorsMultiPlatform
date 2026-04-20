package com.example.memeeditor.core.presentaion

import memeeditor.composeapp.generated.resources.Res
import memeeditor.composeapp.generated.resources.allDrawableResources
import org.jetbrains.compose.resources.DrawableResource

data class MemesTemplate(
    val id : String,
    val drawableResource: DrawableResource
)

val memesListTemplates = Res.allDrawableResources
    .filterKeys{ it.startsWith("meme_template")}
    .map { (key, value) ->
        MemesTemplate(
            id = key,
            drawableResource = value
        )
    }

