package com.example.memeeditor.meme_editor.presentaion.util

fun CharSequence.containsArabicScript(): Boolean = any { ch ->
    val c = ch.code
    c in 0x0600..0x06FF ||
        c in 0x0750..0x077F ||
        c in 0x08A0..0x08FF ||
        c in 0xFB50..0xFDFF ||
        c in 0xFE70..0xFEFF
}
