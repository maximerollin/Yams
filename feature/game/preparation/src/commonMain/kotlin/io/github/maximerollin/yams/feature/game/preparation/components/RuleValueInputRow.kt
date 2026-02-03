package io.github.maximerollin.yams.feature.game.preparation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.component.AppInput

@Composable
internal fun RuleValueInputRow(
    label: String,
    value: Int?,
    modifier: Modifier = Modifier,
    isNullable: Boolean = true,
    onValueChange: (Int?) -> Unit,
) {
    val valueText = value?.toString().orEmpty()

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
        )
        AppInput(
            value = valueText,
            onValueChange = { newValue ->
                val sanitized = newValue.filter { it.isDigit() }
                val parsedValue = sanitized.toIntOrNull()

                when {
                    sanitized.isEmpty() && isNullable -> onValueChange(null)
                    parsedValue != null -> onValueChange(parsedValue)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(96.dp),
            placeholder = {
                Text(
                    text = if (isNullable) "-" else "0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
        )
    }
}
