package com.example.memeeditor.meme_editor.presentaion

import androidx.compose.runtime.Composable

/**
 * System back / predictive back. No-op on platforms without a system back gesture.
 */
@Composable
expect fun BackHandlerCompat(enabled: Boolean, onBack: () -> Unit)
