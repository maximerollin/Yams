package io.github.maximerollin.yams

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

import io.github.maximerollin.yams.core.review.InAppReviewKit
import io.github.maximerollin.yams.navigation.PredictiveBackGestureAnimations

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )

        super.onCreate(savedInstanceState)

        InAppReviewKit.init(this)

        setContent {
            App(
                predictiveBackGestureAnimations = PredictiveBackGestureAnimations(
                    enterTransition = {
                        // New page slides in from the right
                        slideInHorizontally(
                            initialOffsetX = { it / 4 },
                            animationSpec = tween(200)
                        ) + fadeIn(animationSpec = tween(200))
                    },
                    exitTransition = {
                        // Current page fades out
                        fadeOut(animationSpec = tween(200))
                    },
                    popEnterTransition = {
                        // No animation for the page behind - it stays in place
                        fadeIn(animationSpec = tween(200))
                    },
                    popExitTransition = {
                        // Current page slides out to the right
                        slideOutHorizontally(
                            targetOffsetX = { it / 4 },
                            animationSpec = tween(200)
                        ) + fadeOut(animationSpec = tween(200))
                    },
                )
            )
        }
    }
}