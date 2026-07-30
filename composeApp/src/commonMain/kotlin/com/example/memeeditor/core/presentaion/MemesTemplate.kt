package com.example.memeeditor.core.presentaion

import memeeditor.composeapp.generated.resources.Res
import memeeditor.composeapp.generated.resources.allDrawableResources

val memesListTemplates = Res.allDrawableResources
    .filterKeys { it.startsWith("meme_template") }
    .map { (key, value) ->
        MemesTemplate(
            id = key,
            drawableResource = value,
        )
    }
