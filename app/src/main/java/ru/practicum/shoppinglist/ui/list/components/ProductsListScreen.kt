package ru.practicum.shoppinglist.ui.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.domain.model.Product
import ru.practicum.shoppinglist.ui.theme.DriverColorLight
import ru.practicum.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun ProductsListScreen(
    products: List<Product>,
    paddingValues: PaddingValues,
    onDelete: (Long) -> Unit,
    onTogglePurchased: (Long, Boolean) -> Unit
) {
    ShoppingListTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
            contentPadding = paddingValues,
        ) {
            items(
                items = products,
                key = { it.id }
            ) { product ->
                ProductItem(
                    item = product,
                    onTogglePurchased = { onTogglePurchased(product.id, product.isPurchased) },
                    onDelete = { onDelete(product.id) }

                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = DriverColorLight
                )
            }
        }

    }
}

@Composable
fun ProductItem(
    item: Product,
    onTogglePurchased: () -> Unit = {},
    onDelete: () -> Unit = {},
    viewIconMenu: Boolean = false
) {
    val isChecked = item.isPurchased

    val idIcon = if (isChecked) R.drawable.ic_check_circle else R.drawable.ic_uncheck
    val colorTInt = if (isChecked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceTint
    val textColor = if (isChecked) {
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.onBackground
    }
    val textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup()
            .height(72.dp)
            .clickable(onClick = onTogglePurchased),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(idIcon),
            contentDescription = null,
            tint = colorTInt,
            modifier = Modifier.padding(16.dp)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                color = textColor,
                textDecoration = textDecoration
            )
            Text(
                text = "${item.quantity} ${item.unit}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                color = textColor
            )
        }
        if (viewIconMenu) {
            Icon(
                painter = painterResource(R.drawable.ic_drag_handle),
                contentDescription = null,
                tint = colorTInt,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable(onClick = onDelete)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProductsListScreenPreview() {
    val lists = getLists()
    ProductsListScreen(
        lists,
        paddingValues = PaddingValues(),
        onDelete = { },
        onTogglePurchased = { _, _ -> }
    )
}

fun getLists(): List<Product> {
    return listOf(
        Product(
            id = 0,
            name = "Яблоко",
            quantity = "1",
            unit = "кг",
            isPurchased = false,
            listId = 0,
            position = 0,
            isChecked = true
        ),
        Product(
            id = 1,
            name = "Яйца",
            quantity = "10",
            unit = "шт",
            isPurchased = false,
            listId = 0,
            position = 1,
            isChecked = false
        ),
        Product(
            id = 2,
            name = "Молоко",
            quantity = "1",
            unit = "л",
            isPurchased = true,
            listId = 0,
            position = 2,
            isChecked = false
        ),
        Product(
            id = 3,
            name = "Сыр",
            quantity = "1",
            unit = "уп",
            isPurchased = false,
            listId = 0,
            position = 3,
            isChecked = false
        ),
    )
}