package ru.practicum.shoppinglist.ui.list.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun BottomSheetMenu(
    isView: Boolean = false
) {
    if (!isView) return
    ShoppingListTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.inverseSurface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ViewRowMenu(
                    onClick = {},
                    idIcon = R.drawable.ic_swap_vert,
                    title = stringResource(R.string.sort),
                    description = stringResource(R.string.sort_alpha)
                )
                ViewRowMenu(
                    onClick = {},
                    idIcon = R.drawable.ic_delete,
                    title = stringResource(R.string.delete)
                )
                ViewRowMenu(
                    onClick = {},
                    idIcon = R.drawable.ic_clear,
                    title = stringResource(R.string.clear_purchased)
                )
            }
        }
    }
}

@Composable
fun MenuSort() {
    ShoppingListTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.inverseSurface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ViewRowMenu(
                    onClick = {},
                    idIcon = R.drawable.ic_sort_by_alpha,
                    title = stringResource(R.string.sort_alpha),
                    description = "",
                    showRadioButton = true
                )
                ViewRowMenu(
                    onClick = {},
                    idIcon = R.drawable.ic_drag_pan,
                    title = stringResource(R.string.sort_custom),
                    description = "",
                    showRadioButton = true
                )
            }
        }
    }
}

@Composable
private fun ViewRowMenu(
    onClick: () -> Unit = {},
    @DrawableRes idIcon: Int = 0,
    title: String = "",
    description: String = "",
    showRadioButton: Boolean = false,
    onSelect: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = { onClick() }),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(idIcon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.inverseOnSurface,
            modifier = Modifier.padding(16.dp)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                color = MaterialTheme.colorScheme.inverseOnSurface,
            )
            if (description.isNotBlank()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        }

        if (showRadioButton) {
            RadioButton(
                selected = false,
                onClick = { onSelect() },
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.inverseOnSurface,
                    unselectedColor = MaterialTheme.colorScheme.inverseOnSurface
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BottomSheetMenuPreview() {
    BottomSheetMenu()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MenuSortPreview() {
    MenuSort()
}