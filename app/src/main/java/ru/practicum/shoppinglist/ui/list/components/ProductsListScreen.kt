package ru.practicum.shoppinglist.ui.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.ui.list.TestProduct
import ru.practicum.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun ProductsListScreen(
    products: List<TestProduct>
) {
    ShoppingListTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
        ) {
            items(
                items = products,
                key = { it.id }
            ) { product ->
                ProductItem(
                    item = product,
                    onSelect = { product }
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceTint
                )
            }
        }

    }
}

@Composable
fun ProductItem(
    item: TestProduct,
    onSelect: (TestProduct) -> Unit = {},
    viewIconMenu: Boolean = false
) {
    var idIcon = R.drawable.ic_check_circle
    var colorTInt = MaterialTheme.colorScheme.secondary

    if (!item.isChecked) {
        idIcon = R.drawable.ic_uncheck
        colorTInt = MaterialTheme.colorScheme.surfaceTint
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup()
            .height(72.dp)
            .clickable(onClick = { onSelect(item) }),
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
                maxLines = 1
            )
            Text(
                text = "${item.quantity} ${item.unit}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }
        if (viewIconMenu) {
            Icon(
                painter = painterResource(R.drawable.ic_drag_handle),
                contentDescription = null,
                tint = colorTInt,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProductsListScreenPreview() {
    val lists = getLists()
    ProductsListScreen(lists)
}

fun getLists(): List<TestProduct> {
    return listOf(
        TestProduct(id = 0, name = "Яблоко", quantity = "1", unit = "кг", isChecked = true),
        TestProduct(id = 1, name = "Яйца", quantity = "10", unit = "шт", isChecked = false),
        TestProduct(id = 2, name = "Молоко", quantity = "1", unit = "л", isChecked = false),
        TestProduct(id = 3, name = "Сыр", quantity = "1", unit = "уп", isChecked = false),
    )
}