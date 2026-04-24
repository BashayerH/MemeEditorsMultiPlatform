package com.example.memeeditor.meme_editor.presentaion

data class MemeText (

    val id : String,
    val text : String,
    /** Display size in **sp**, must match the editor text style (see [rememberFillTextStyle]). */
    val fontSize : Float = 36f,
    val offsetRatioX : Float = 0f,
    val offsetRatioY : Float =0f,
    val rotation : Float = 0f,
    val scale : Float = 1f,
)
