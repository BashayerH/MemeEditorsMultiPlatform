package com.example.memeeditor.meme_editor.presentaion.util

import com.example.memeeditor.ComposeUiPresentationAnchor
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSThread
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene

actual class PlatformShareSheet {
    actual fun shareFile(filePath: String) {
        val work = { presentShareSheet(filePath) }
        if (NSThread.isMainThread) {
            work()
        } else {
            NSOperationQueue.mainQueue.addOperationWithBlock(work)
        }
    }

    private fun presentShareSheet(filePath: String) {
        val fileUrl = NSURL.fileURLWithPath(filePath)
        val activity = UIActivityViewController(
            activityItems = listOf(fileUrl),
            applicationActivities = null,
        )
        val presenter = resolvePresenterForShareSheet() ?: return
        val top = presenter.topMostPresentedOrSelf()
        top.presentViewController(
            viewControllerToPresent = activity,
            animated = true,
            completion = null,
        )
    }

    private fun resolvePresenterForShareSheet(): UIViewController? {
        UIApplication.sharedApplication.keyWindow?.rootViewController?.let { return it }

        val anchor = ComposeUiPresentationAnchor.rootViewController
        if (anchor != null) {
            anchor.view?.window?.rootViewController?.let { return it }
            var p: UIViewController = anchor
            while (true) {
                val parent = p.parentViewController ?: break
                p = parent
            }
            return p
        }

        val app = UIApplication.sharedApplication
        for (raw in app.connectedScenes) {
            val scene = raw as? UIWindowScene ?: continue
            if (scene.activationState != UISceneActivationStateForegroundActive) continue
            scene.keyWindow?.rootViewController?.let { return it }
            val wins = scene.windows ?: continue
            val n = wins.count().toInt()
            for (i in 0 until n) {
                val w = wins[i] as? UIWindow ?: continue
                if (w.isKeyWindow()) return w.rootViewController
            }
            for (i in 0 until n) {
                val w = wins[i] as? UIWindow ?: continue
                w.rootViewController?.let { return it }
            }
        }

        @Suppress("DEPRECATION")
        val legacy = app.windows ?: return null
        val count = legacy.count().toInt()
        for (i in 0 until count) {
            val w = legacy[i] as? UIWindow ?: continue
            if (w.isKeyWindow()) return w.rootViewController
        }
        return if (count > 0) (legacy[0] as? UIWindow)?.rootViewController else null
    }

    private fun UIViewController.topMostPresentedOrSelf(): UIViewController {
        var current = this
        while (true) {
            val next = current.presentedViewController ?: break
            current = next
        }
        return current
    }
}
