package io.github.maximerollin.yams.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.component.YamsPrimaryButton
import io.github.maximerollin.yams.core.designsystem.component.YamsSecondaryButton
import io.github.maximerollin.yams.core.designsystem.component.YamsTextButton
import io.github.maximerollin.yams.core.designsystem.icon.BookOpen
import io.github.maximerollin.yams.core.designsystem.icon.ChevronLeft
import io.github.maximerollin.yams.core.designsystem.icon.Info
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.core.designsystem.util.IconInfo
import org.jetbrains.compose.resources.stringResource
import yams.feature.home.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeInformationBottomSheet(
    appVersionName: String,
    isHapticFeedbackEnabled: Boolean,
    onHapticFeedbackEnabledChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var page by remember { mutableStateOf(HomeInformationPage.Overview) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        when (page) {
            HomeInformationPage.Overview -> HomeInformationOverview(
                appVersionName = appVersionName,
                isHapticFeedbackEnabled = isHapticFeedbackEnabled,
                onHapticFeedbackEnabledChange = onHapticFeedbackEnabledChange,
                onShowRules = { page = HomeInformationPage.Rules },
                onShowLicenses = { page = HomeInformationPage.Licenses },
                modifier = modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            )

            HomeInformationPage.Rules -> HomeRulesContent(
                onBack = { page = HomeInformationPage.Overview },
                modifier = modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            )

            HomeInformationPage.Licenses -> HomeLicensesContent(
                onBack = { page = HomeInformationPage.Overview },
                modifier = modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }
    }
}

@Composable
private fun HomeInformationOverview(
    appVersionName: String,
    isHapticFeedbackEnabled: Boolean,
    onHapticFeedbackEnabledChange: (Boolean) -> Unit,
    onShowRules: () -> Unit,
    onShowLicenses: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = stringResource(Res.string.home_info_title),
                style = MaterialTheme.typography.titleLarge,
                color = YamsTheme.colors.brown,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(Res.string.home_info_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        HomeInformationSection(
            title = stringResource(Res.string.home_settings_title),
            subtitle = stringResource(Res.string.home_settings_subtitle),
        ) {
            HomeSettingsToggleRow(
                label = stringResource(Res.string.home_settings_haptic_label),
                supportingText = stringResource(Res.string.home_settings_haptic_supporting_text),
                checked = isHapticFeedbackEnabled,
                onCheckedChange = onHapticFeedbackEnabledChange,
            )
        }

        YamsPrimaryButton(
            onClick = onShowRules,
            text = stringResource(Res.string.home_info_rules_button),
            icon = IconInfo(
                vector = YamsIcons.Info,
                contentDescription = stringResource(Res.string.home_info_rules_button),
            ),
        )

        YamsSecondaryButton(
            onClick = onShowLicenses,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = YamsIcons.BookOpen,
                contentDescription = stringResource(Res.string.home_info_licenses_button),
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = stringResource(Res.string.home_info_licenses_button))
        }

        HomeVersionText(appVersionName = appVersionName)
    }
}

@Composable
private fun HomeSettingsToggleRow(
    label: String,
    supportingText: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        )
    }
}

@Composable
private fun HomeLicensesContent(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        YamsTextButton(onClick = onBack) {
            Icon(
                imageVector = YamsIcons.ChevronLeft,
                contentDescription = stringResource(Res.string.home_info_back_cd),
                modifier = Modifier.size(18.dp),
            )
            Text(text = stringResource(Res.string.home_info_back))
        }

        HomeInformationSection(
            title = stringResource(Res.string.home_info_licenses_title),
            subtitle = stringResource(Res.string.home_info_licenses_subtitle),
        ) {
            LibraryNotices.forEachIndexed { index, notice ->
                LibraryNoticeRow(notice = notice)
                if (index < LibraryNotices.lastIndex) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeRulesContent(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rules = listOf(
        RuleNotice(
            title = stringResource(Res.string.home_rules_common_title),
            body = stringResource(Res.string.home_rules_common_body),
        ),
        RuleNotice(
            title = stringResource(Res.string.home_rules_yahtzee_title),
            body = stringResource(Res.string.home_rules_yahtzee_body),
        ),
        RuleNotice(
            title = stringResource(Res.string.home_rules_yams_title),
            body = stringResource(Res.string.home_rules_yams_body),
        ),
        RuleNotice(
            title = stringResource(Res.string.home_rules_end_title),
            body = stringResource(Res.string.home_rules_end_body),
        ),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        YamsTextButton(onClick = onBack) {
            Icon(
                imageVector = YamsIcons.ChevronLeft,
                contentDescription = stringResource(Res.string.home_info_back_cd),
                modifier = Modifier.size(18.dp),
            )
            Text(text = stringResource(Res.string.home_info_back))
        }

        HomeInformationSection(title = stringResource(Res.string.home_rules_title)) {
            rules.forEachIndexed { index, rule ->
                RuleNoticeRow(rule = rule)
                if (index < rules.lastIndex) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeInformationSection(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            content()
        }
    }
}

@Composable
private fun LibraryNoticeRow(
    notice: LibraryNotice,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = notice.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = stringResource(
                    Res.string.home_info_license_version,
                    notice.version,
                    notice.license,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End,
            )
        }
        Text(
            text = stringResource(Res.string.home_info_license_link, notice.url),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun RuleNoticeRow(
    rule: RuleNotice,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = rule.title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = rule.body,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun HomeVersionText(
    appVersionName: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(Res.string.home_info_version, displayVersion(appVersionName)),
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}

private fun displayVersion(versionName: String): String {
    val parts = versionName.split(".")
    return if (parts.size == 2) {
        "$versionName.0"
    } else {
        versionName
    }
}

private enum class HomeInformationPage {
    Overview,
    Rules,
    Licenses,
}

private data class LibraryNotice(
    val name: String,
    val version: String,
    val license: String,
    val url: String,
)

private data class RuleNotice(
    val title: String,
    val body: String,
)

private val LibraryNotices = listOf(
    LibraryNotice(
        name = "Kotlin",
        version = "2.2.21",
        license = "Apache 2.0",
        url = "https://kotlinlang.org",
    ),
    LibraryNotice(
        name = "Kotlinx Coroutines",
        version = "1.10.2",
        license = "Apache 2.0",
        url = "https://github.com/Kotlin/kotlinx.coroutines",
    ),
    LibraryNotice(
        name = "Kotlinx Serialization",
        version = "1.9.0",
        license = "Apache 2.0",
        url = "https://github.com/Kotlin/kotlinx.serialization",
    ),
    LibraryNotice(
        name = "Compose Multiplatform",
        version = "1.10.1",
        license = "Apache 2.0",
        url = "https://github.com/JetBrains/compose-multiplatform",
    ),
    LibraryNotice(
        name = "Compose Material 3",
        version = "1.10.0-alpha05",
        license = "Apache 2.0",
        url = "https://github.com/JetBrains/compose-multiplatform",
    ),
    LibraryNotice(
        name = "AndroidX Activity Compose",
        version = "1.12.0",
        license = "Apache 2.0",
        url = "https://developer.android.com/jetpack/androidx",
    ),
    LibraryNotice(
        name = "AndroidX DataStore",
        version = "1.2.0",
        license = "Apache 2.0",
        url = "https://developer.android.com/jetpack/androidx/releases/datastore",
    ),
    LibraryNotice(
        name = "AndroidX Lifecycle",
        version = "2.10.0-alpha05",
        license = "Apache 2.0",
        url = "https://developer.android.com/jetpack/androidx/releases/lifecycle",
    ),
    LibraryNotice(
        name = "AndroidX Navigation Compose",
        version = "2.9.1",
        license = "Apache 2.0",
        url = "https://developer.android.com/jetpack/androidx/releases/navigation",
    ),
    LibraryNotice(
        name = "AndroidX Room",
        version = "2.8.4",
        license = "Apache 2.0",
        url = "https://developer.android.com/jetpack/androidx/releases/room",
    ),
    LibraryNotice(
        name = "AndroidX SQLite",
        version = "2.6.2",
        license = "Apache 2.0",
        url = "https://developer.android.com/jetpack/androidx/releases/sqlite",
    ),
    LibraryNotice(
        name = "AndroidX Startup",
        version = "1.2.0",
        license = "Apache 2.0",
        url = "https://developer.android.com/jetpack/androidx/releases/startup",
    ),
    LibraryNotice(
        name = "Coil",
        version = "3.3.0",
        license = "Apache 2.0",
        url = "https://github.com/coil-kt/coil",
    ),
    LibraryNotice(
        name = "ConfettiKit",
        version = "0.8.0",
        license = "MIT",
        url = "https://github.com/vinceglb/ConfettiKit",
    ),
    LibraryNotice(
        name = "FileKit",
        version = "0.14.1",
        license = "MIT",
        url = "https://github.com/vinceglb/FileKit",
    ),
    LibraryNotice(
        name = "Koin",
        version = "4.1.1",
        license = "Apache 2.0",
        url = "https://insert-koin.io",
    ),
    LibraryNotice(
        name = "PostHog Android",
        version = "3.x",
        license = "MIT",
        url = "https://github.com/PostHog/posthog-android",
    ),
    LibraryNotice(
        name = "RevenueCat Purchases KMP",
        version = "3.0.5",
        license = "MIT",
        url = "https://github.com/RevenueCat/purchases-kmp",
    ),
    LibraryNotice(
        name = "Reorderable",
        version = "3.0.0",
        license = "Apache 2.0",
        url = "https://github.com/Calvin-LL/Reorderable",
    ),
    LibraryNotice(
        name = "Google Play In-App Review",
        version = "2.0.2",
        license = "Google Play SDK Terms",
        url = "https://developer.android.com/guide/playcore/in-app-review",
    ),
)
