package com.example.memeeditor

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform