package io.github.maximerollin.yams.feature.user.edition.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import io.github.maximerollin.yams.core.designsystem.component.AppInput
import io.github.maximerollin.yams.core.designsystem.icon.Person
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme

@Composable
internal fun UserEditionInput(
    name: String,
    onChangeName: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AppInput(
        modifier = modifier,
        value = name,
        onValueChange = onChangeName,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            capitalization = KeyboardCapitalization.Sentences,
        ),
        startIcon = {
            Icon(
                imageVector = YamsIcons.Person,
                contentDescription = "User name",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        placeholder = {
            Text(
                text = "Username",
                style = TextStyle.Default.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.outline
                )
            )
        },
    )
}

@Preview
@Composable
private fun UserEditionInputPreview() {
    YamsTheme {
        UserEditionInput(
            name = "Maxime",
            onChangeName = {},
        )
    }
}

