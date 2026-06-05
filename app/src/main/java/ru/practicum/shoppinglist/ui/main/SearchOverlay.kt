package ru.practicum.shoppinglist.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun SearchOverlay(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onBack: () -> Unit,
) {
    ShoppingListTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            Surface(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(73.dp)
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = onSearchTextChange,
                    leadingIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_back),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiaryFixed
                            )
                        }
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = { onSearchTextChange("") }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_close),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiaryFixed,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                        }
                    },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_lists),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        }
    }
}

// Preview для поиска с пустым полем ввода
@Preview(name = "Search - Empty Input", showBackground = true)
@Composable
private fun PreviewSearchEmptyInput() {
    var searchText by remember { mutableStateOf("") }

    ShoppingListTheme {
        SearchOverlay(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            onBack = {},
        )
    }
}
