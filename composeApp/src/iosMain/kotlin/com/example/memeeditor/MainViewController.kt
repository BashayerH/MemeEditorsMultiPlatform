package com.example.memeeditor

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    val controller = ComposeUIViewController { App() }
    ComposeUiPresentationAnchor.rootViewController = controller
    return controller
}