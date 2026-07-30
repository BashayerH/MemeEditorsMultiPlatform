package com.example.memeeditor.core.presentaion

import kotlin.test.Test
import kotlin.test.assertEquals

class NavPathCodecTest {

    @Test
    fun roundTripPreservesAbsolutePath() {
        val path = "/data/user/0/com.example.memeeditor/cache/picked_123.jpg"
        assertEquals(path, NavPathCodec.decode(NavPathCodec.encode(path)))
    }

    @Test
    fun encodedPathHasNoSlashes() {
        val encoded = NavPathCodec.encode("/tmp/foo/bar.jpg")
        assertEquals(false, encoded.contains('/'))
    }
}
