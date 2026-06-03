package ru.practicum.shoppinglist.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.ui.navigation.ActionDelete
import ru.practicum.shoppinglist.ui.navigation.ActionSearch
import ru.practicum.shoppinglist.ui.navigation.ActionTheme
import ru.practicum.shoppinglist.ui.navigation.AppBarTop
import ru.practicum.shoppinglist.ui.theme.ShoppingListTheme

@Preview
@Composable
fun MainScreen(
    onList: () -> Unit = {},
    onBack: () -> Unit = {},
    onTheme: () -> Unit = {},

) {
    var onSearch by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var onDelete by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var listName by remember { mutableStateOf("") }

    ShoppingListTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                AppBarTop(
                    title = stringResource(R.string.main_lists),
                    search = ActionSearch(isView = true, onClick = { onSearch = true }),
                    delete = ActionDelete(isView = true, onClick = { onDelete = true }),
                    theme = ActionTheme(isView = true, onClick = onTheme),
                )

                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(top = 120.dp)
                        .padding(horizontal = 44.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        painter = painterResource(R.drawable.ic_shopping_lists),
                        contentDescription = null
                    )

                    Text(
                        modifier = Modifier.padding(top = 44.dp),
                        text = stringResource(R.string.shopping_no_lists),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = stringResource(R.string.shopping_no_lists_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )

                }

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 16.dp)
                        .padding(bottom = 56.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add_list_78),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { showDialog = true }
                    )
                }
            }
        }

        if (onSearch) {
            SearchOverlay(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                onBack = onBack
            )
        }

        if (onDelete) {
            DeleteAllDialog(
                onConfirm = { onDelete = false },
                onDismiss = { onDelete = false }
            )

        }

        if (showDialog) {
            AddListDialog(
                listName = listName,
                onListNameChange = { listName = it },
                onDismiss = { showDialog = false },
                onConfirm = { showDialog = false }
            )

        }

    }
}

@Composable
fun SearchOverlay(
    searchText: String = "",
    onSearchTextChange: (String) -> Unit,
    onBack: () -> Unit
) {
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

@Composable
fun DeleteAllDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_dialog),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiaryFixed,
            )
        },
        title = {
            Text(
                text = stringResource(R.string.delete_all_lists),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.delete),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.onPrimary

    )

}

@Composable
fun AddListDialog(
    listName: String,
    onListNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Image(
                painter = painterResource(R.drawable.ic_add_on),
                contentDescription = null,
            )
        },
        title = {
            Text(
                text = stringResource(R.string.add_list),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            SelectField(
                label = stringResource(R.string.name_list),
                value = listName,
                onValueChange = onListNameChange,
                placeholder = stringResource(R.string.new_list)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.create),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.onPrimary

    )

}

@Composable
fun SelectField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit = {},
    placeholder: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                focusedLabelColor = MaterialTheme.colorScheme.secondary,
                unfocusedLabelColor = MaterialTheme.colorScheme.secondary,
                disabledTextColor = MaterialTheme.colorScheme.onBackground,
                disabledBorderColor = MaterialTheme.colorScheme.secondary,
                disabledLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.secondary
            )
        )
    }

}
