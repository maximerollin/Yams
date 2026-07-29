package io.github.maximerollin.yams.screenshots

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import io.github.maximerollin.yams.feature.game.creation.GameCreationEmptyStoreScreenshotContent
import io.github.maximerollin.yams.feature.game.creation.GameCreationSelectedStoreScreenshotContent
import io.github.maximerollin.yams.feature.game.play.GamePlaySingleColumnStoreScreenshotContent
import io.github.maximerollin.yams.feature.game.play.GamePlayTwoColumnsStoreScreenshotContent
import io.github.maximerollin.yams.feature.game.play.components.DiceAssistantStoreScreenshotContent
import io.github.maximerollin.yams.feature.game.preparation.GamePreparationManualStoreScreenshotContent
import io.github.maximerollin.yams.feature.game.preparation.GamePreparationRandomStoreScreenshotContent
import io.github.maximerollin.yams.feature.game.result.GameResultStoreScreenshotContent
import io.github.maximerollin.yams.feature.home.HomeStoreScreenshotContent
import io.github.maximerollin.yams.feature.user.edition.UserEditionStoreScreenshotContent
import io.github.maximerollin.yams.feature.user.history.UserHistoryStoreScreenshotContent
import io.github.maximerollin.yams.feature.user.profile.UserProfileStoreScreenshotContent
import io.github.maximerollin.yams.feature.user.profile.UserProfileAdvancedStatsStoreScreenshotContent
import io.github.maximerollin.yams.feature.user.profile.UserProfileProgressStoreScreenshotContent
import io.github.maximerollin.yams.feature.user.users.UsersStoreScreenshotContent
import io.github.maximerollin.yams.feature.welcome.WelcomeStoreScreenshotContent

private const val PhoneDevice = "spec:width=411dp,height=891dp,dpi=420"
private const val TabletDevice = "spec:width=800dp,height=1280dp,dpi=240"

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(name = "phone-fr", group = "phone/fr", locale = "fr", device = PhoneDevice, showSystemUi = true)
@Preview(name = "phone-en", group = "phone/en", locale = "en", device = PhoneDevice, showSystemUi = true)
@Preview(name = "phone-es", group = "phone/es", locale = "es", device = PhoneDevice, showSystemUi = true)
@Preview(name = "phone-de", group = "phone/de", locale = "de", device = PhoneDevice, showSystemUi = true)
@Preview(name = "phone-it", group = "phone/it", locale = "it", device = PhoneDevice, showSystemUi = true)
private annotation class PhoneStorePreviews

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(name = "tablet-fr", group = "tablet/fr", locale = "fr", device = TabletDevice, showSystemUi = true)
@Preview(name = "tablet-en", group = "tablet/en", locale = "en", device = TabletDevice, showSystemUi = true)
@Preview(name = "tablet-es", group = "tablet/es", locale = "es", device = TabletDevice, showSystemUi = true)
@Preview(name = "tablet-de", group = "tablet/de", locale = "de", device = TabletDevice, showSystemUi = true)
@Preview(name = "tablet-it", group = "tablet/it", locale = "it", device = TabletDevice, showSystemUi = true)
private annotation class TabletStorePreviews

@PhoneStorePreviews
@TabletStorePreviews
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
private annotation class StorePreviews

@Composable
private fun StoreScreenshot(content: @Composable () -> Unit) {
    content()
}

@PreviewTest
@StorePreviews
@Composable
public fun Welcome() {
    StoreScreenshot {
        WelcomeStoreScreenshotContent()
    }
}

@PreviewTest
@StorePreviews
@Composable
public fun Home() {
    StoreScreenshot {
        HomeStoreScreenshotContent()
    }
}

@PreviewTest
@StorePreviews
@Composable
public fun GameCreationEmpty() {
    StoreScreenshot {
        GameCreationEmptyStoreScreenshotContent()
    }
}

@PreviewTest
@StorePreviews
@Composable
public fun GameCreationSelected() {
    StoreScreenshot {
        GameCreationSelectedStoreScreenshotContent()
    }
}

@PreviewTest
@StorePreviews
@Composable
public fun GamePreparationRandom() {
    StoreScreenshot {
        GamePreparationRandomStoreScreenshotContent()
    }
}

@PreviewTest
@StorePreviews
@Composable
public fun GamePreparationManual() {
    StoreScreenshot {
        GamePreparationManualStoreScreenshotContent()
    }
}

@PreviewTest
@PhoneStorePreviews
@Composable
public fun GamePlaySingleColumn() {
    StoreScreenshot {
        GamePlaySingleColumnStoreScreenshotContent()
    }
}

@PreviewTest
@StorePreviews
@Composable
public fun GamePlayTwoColumns() {
    StoreScreenshot {
        GamePlayTwoColumnsStoreScreenshotContent()
    }
}

@PreviewTest
@PhoneStorePreviews
@Composable
public fun DiceAssistant() {
    StoreScreenshot {
        DiceAssistantStoreScreenshotContent()
    }
}

@PreviewTest
@StorePreviews
@Composable
public fun GameResult() {
    StoreScreenshot {
        GameResultStoreScreenshotContent()
    }
}

@PreviewTest
@StorePreviews
@Composable
public fun UserEdition() {
    StoreScreenshot {
        UserEditionStoreScreenshotContent()
    }
}

@PreviewTest
@StorePreviews
@Composable
public fun Users() {
    StoreScreenshot {
        UsersStoreScreenshotContent()
    }
}

@PreviewTest
@StorePreviews
@Composable
public fun UserProfile() {
    StoreScreenshot {
        UserProfileStoreScreenshotContent()
    }
}

@PreviewTest
@PhoneStorePreviews
@Composable
public fun UserProfileProgress() {
    StoreScreenshot {
        UserProfileProgressStoreScreenshotContent()
    }
}

@PreviewTest
@PhoneStorePreviews
@Composable
public fun UserProfileAdvancedStats() {
    StoreScreenshot {
        UserProfileAdvancedStatsStoreScreenshotContent()
    }
}

@PreviewTest
@StorePreviews
@Composable
public fun UserHistory() {
    StoreScreenshot {
        UserHistoryStoreScreenshotContent()
    }
}
