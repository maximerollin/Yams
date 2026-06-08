package io.github.maximerollin.yams.feature.game.creation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import io.github.maximerollin.yams.core.designsystem.component.AppIconButton
import io.github.maximerollin.yams.core.designsystem.component.AppTopBar
import io.github.maximerollin.yams.core.designsystem.icon.Home
import io.github.maximerollin.yams.core.designsystem.icon.PersonAdd
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import org.jetbrains.compose.resources.stringResource
import yams.feature.game.creation.generated.resources.*

@Composable
public fun GameCreationTopBar(
    modifier: Modifier = Modifier,
    onNavigateHome: () -> Unit = {},
    onCreateUser: () -> Unit = {},
) {
    AppTopBar(
        modifier = modifier.statusBarsPadding(),
        start = {
            AppIconButton(
                icon = YamsIcons.Home,
                onClick = onNavigateHome,
                contentDescription = stringResource(Res.string.creation_home_cd)
            )
        },
        center = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(Res.string.creation_title),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }
        },
        end = {
            AppIconButton(
                icon = YamsIcons.PersonAdd,
                onClick = onCreateUser,
                contentDescription = stringResource(Res.string.creation_add_cd)
            )
        }
    )
}

@Preview
@Composable
public fun GameCreationTopBarPreview() {
    YamsTheme {
        Scaffold(
            topBar = {
                GameCreationTopBar()
            },
            content = { }
        )
    }
}
