package io.github.maximerollin.yams.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.maximerollin.yams.core.designsystem.icon.Search
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme

@Composable
public fun AppInput(
    value: String,
    enabled: Boolean = true,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    startIcon: @Composable (() -> Unit)? = null,
    endIcon: @Composable (() -> Unit)? = null,
    placeholder: @Composable () -> Unit = {},
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        enabled = enabled,
        textStyle = TextStyle.Default.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        ),
        modifier = modifier,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        decorationBox = { innerTextField ->
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surface,
                ),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .height(48.dp)
                    .clip(MaterialTheme.shapes.large)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    startIcon?.invoke()

                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (value.isEmpty()) {
                            placeholder()
                        }

                        innerTextField()
                    }

                    endIcon?.invoke()
                }
            }

        }
    )
}

@Preview
@Composable
private fun AppInputStartIconPreview() {
    YamsTheme {
        AppInput(
            value = "",
            onValueChange = {},
            modifier = Modifier.padding(16.dp),
            startIcon = {
                Icon(
                    imageVector = YamsIcons.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            placeholder = {
                Text(
                    text = "Rechercher",
                    style = TextStyle.Default.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.outline
                    )
                )
            },
        )
    }
}

@Preview
@Composable
private fun AppInputEndIconPreview() {
    YamsTheme {
        AppInput(
            value = "",
            onValueChange = {},
            modifier = Modifier.padding(16.dp),
            endIcon = {
                Icon(
                    imageVector = YamsIcons.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            placeholder = {
                Text(
                    text = "Rechercher",
                    style = TextStyle.Default.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.outline
                    )
                )
            },
        )
    }
}

@Preview
@Composable
private fun AppInputBothIconPreview() {
    YamsTheme {
        AppInput(
            value = "",
            onValueChange = {},
            modifier = Modifier.padding(16.dp),
            startIcon = {
                Icon(
                    imageVector = YamsIcons.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            endIcon = {
                Icon(
                    imageVector = YamsIcons.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            placeholder = {
                Text(
                    text = "Rechercher",
                    style = TextStyle.Default.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.outline
                    )
                )
            },
        )
    }
}

@Preview
@Composable
private fun AppInputNoIconPreview() {
    YamsTheme {
        AppInput(
            value = "",
            onValueChange = {},
            modifier = Modifier.padding(16.dp),
            placeholder = {
                Text(
                    text = "Rechercher",
                    style = TextStyle.Default.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.outline
                    )
                )
            },
        )
    }
}

@Preview
@Composable
private fun AppInputTextPreview() {
    YamsTheme {
        AppInput(
            value = "Maxime",
            onValueChange = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}