package com.example.memeeditor.core

/**
 * Locked store identity for first release.
 * Change [PRIVACY_POLICY_URL] after you host [docs/privacy-policy.md].
 * Apple Team ID lives in [iosApp/Configuration/Config.local.xcconfig] (gitignored).
 */
object StoreConfig {
    const val APPLICATION_ID = "com.memeeditor.app"
    const val APP_DISPLAY_NAME = "MemeEditor"
    const val VERSION_NAME = "1.0.0"
    const val VERSION_CODE = 1

    /**
     * GitHub Pages URL (repo: BashayerH/MemeEditorsMultiPlatform, source: /docs).
     * Site home is docs/index.html = privacy policy.
     */
    const val PRIVACY_POLICY_URL =
        "https://bashayerh.github.io/MemeEditorsMultiPlatform/"
}
