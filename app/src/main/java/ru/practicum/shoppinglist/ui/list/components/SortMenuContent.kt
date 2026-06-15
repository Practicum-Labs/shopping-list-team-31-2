package ru.practicum.shoppinglist.ui.list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.practicum.shoppinglist.R

@Composable
fun SortMenuContent(
    onSortAlphabetically: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.inverseSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            MenuItem(
                icon = R.drawable.ic_sort_by_alpha,
                title = stringResource(R.string.sort_alpha),
                onClick = onSortAlphabetically,
                showRadioButton = true
            )

            MenuItem(
                icon = R.drawable.ic_drag_pan,
                title = stringResource(R.string.sort_custom),
                description = "Не доступно",
                onClick = { },
                showRadioButton = true,
            )

        }
    }
}

@Composable
private fun MenuItem(
    icon: Int,
    title: String,
    description: String = "",
    onClick: () -> Unit,
    showRadioButton: Boolean = false,
    isEnabled: Boolean = true,
    isSelected: Boolean = false
) {
    val tintColor = if (isEnabled) {
        MaterialTheme.colorScheme.inverseOnSurface
    } else {
        MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.5f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(enabled = isEnabled) { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = title,
            tint = tintColor,
            modifier = Modifier.padding(16.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = tintColor
            )
            if (description.isNotBlank()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = tintColor.copy(alpha = 0.7f)
                )
            }
        }

        if (showRadioButton) {
            RadioButton(
                selected = isSelected,
                onClick = if (isEnabled) onClick else null,
                enabled = isEnabled,
                colors = RadioButtonDefaults.colors(
                    selectedColor = tintColor,
                    unselectedColor = tintColor.copy(alpha = 0.5f)

                )
            )
        }
    }
}