package io.github.maximerollin.yams.feature.game.preparation.components

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import io.github.maximerollin.yams.core.designsystem.component.AppIconButton
import io.github.maximerollin.yams.core.designsystem.component.AppTopBar
import io.github.maximerollin.yams.core.designsystem.icon.ChevronLeft
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import org.jetbrains.compose.resources.stringResource
import yams.feature.game.preparation.generated.resources.*

@Composable
public fun GamePreparationTopBar(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
) {
    AppTopBar(
        modifier = modifier.statusBarsPadding(),
        start = {
            AppIconButton(
                icon = YamsIcons.ChevronLeft,
                onClick = onNavigateBack,
                contentDescription = stringResource(Res.string.prep_back_cd),
            )
        },
        center = {
            Text(
                stringResource(Res.string.prep_title),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
    )
}

@Preview
@Composable
private fun GamePreparationTopBarPreview() {
    YamsTheme {
        Scaffold(
            topBar = {
                GamePreparationTopBar(
                    onNavigateBack = {}
                )
            }
        ) {}
    }
}
