package com.example.memeeditor.meme_editor.presentaion

data class MemeText (

    val id : String,
    val text : String,
    val fontSize : Float = 20f,
    val offsetRatioX : Float = 0f,
    val offsetRatioY : Float =0f,
    val rotation : Float = 0f,
    val scale : Float = 1f,
)
