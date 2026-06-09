package io.github.maximerollin.yams.core.review

import platform.StoreKit.SKStoreReviewController
import platform.UIKit.UIApplication
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIWindowScene

public actual object InAppReview {
    public actual suspend fun requestReview() {
        val windowScene = UIApplication.sharedApplication.connectedScenes
            .filterIsInstance<UIWindowScene>()
            .firstOrNull { it.activationState == UISceneActivationStateForegroundActive }
            ?: return

        SKStoreReviewController.requestReviewInScene(windowScene)
    }
}
