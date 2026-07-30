package com.example.memeeditor.meme_editor.platform

import androidx.compose.runtime.Composable

/** Returns a launcher that opens this app's system settings page. */
@Composable
expect fun rememberOpenAppSettings(): () -> Unit
