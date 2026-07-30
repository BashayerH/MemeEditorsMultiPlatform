package com.example.memeeditor.core.presentaion

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/** Encode absolute file paths for type-safe navigation (slashes break route parsing). */
@OptIn(ExperimentalEncodingApi::class)
object NavPathCodec {
    private val codec = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)

    fun encode(path: String): String =
        codec.encode(path.encodeToByteArray())

    fun decode(encoded: String): String =
        codec.decode(encoded).decodeToString()
}
