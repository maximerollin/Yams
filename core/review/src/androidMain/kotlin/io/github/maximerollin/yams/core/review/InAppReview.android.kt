package io.github.maximerollin.yams.core.review

import android.app.Activity
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.tasks.await
import java.lang.ref.WeakReference

public object InAppReviewKit {
    private var _activity: WeakReference<Activity?> = WeakReference(null)
    internal val activity: Activity
        get() = _activity.get()
            ?: throw IllegalStateException("Activity is not initialized. Call InAppReviewKit.init(activity) before using it.")

    public fun init(activity: Activity) {
        _activity = WeakReference(activity)
    }
}

public actual object InAppReview {
    public actual suspend fun requestReview() {
        try {
            val activity = InAppReviewKit.activity
            val reviewManager = ReviewManagerFactory.create(activity)
            val reviewInfo = reviewManager.requestReviewFlow().await()
            reviewManager.launchReviewFlow(activity, reviewInfo).await()
        } catch (e: Exception) {
            // In-app review is best-effort; ignore failures.
        }
    }
}
