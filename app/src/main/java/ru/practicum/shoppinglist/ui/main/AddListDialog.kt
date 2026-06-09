package ru.practicum.shoppinglist.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun AddListDialog(
    listName: String,
    onListNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isRenameMode: Boolean = false,
) {
    ShoppingListTheme {
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
                TextButton(
                    onClick = onConfirm,
                    enabled = listName.isNotBlank()
                ) {
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

// Preview для диалога создания списка (пустое поле)
@Preview(name = "Add Dialog - Empty", showBackground = true)
@Composable
private fun PreviewAddDialogEmpty() {
    var listName by remember { mutableStateOf("") }

    ShoppingListTheme {
        AddListDialog(
            listName = listName,
            onListNameChange = { listName = it },
            onDismiss = {},
            onConfirm = {},
            isRenameMode = false
        )
    }
}

// Preview для диалога создания списка (с введённым текстом)
@Preview(name = "Add Dialog - With Text", showBackground = true)
@Composable
private fun PreviewAddDialogWithText() {
    var listName by remember { mutableStateOf("Продукты") }

    ShoppingListTheme {
        AddListDialog(
            listName = listName,
            onListNameChange = { listName = it },
            onDismiss = {},
            onConfirm = {},
            isRenameMode = false
        )
    }
}

// Preview для диалога переименования (пустое поле)
@Preview(name = "Rename Dialog - Empty", showBackground = true)
@Composable
private fun PreviewRenameDialogEmpty() {
    var listName by remember { mutableStateOf("") }

    ShoppingListTheme {
        AddListDialog(
            listName = listName,
            onListNameChange = { listName = it },
            onDismiss = {},
            onConfirm = {},
            isRenameMode = true
        )
    }
}

// Preview для диалога переименования (с введённым текстом)
@Preview(name = "Rename Dialog - With Text", showBackground = true)
@Composable
private fun PreviewRenameDialogWithText() {
    var listName by remember { mutableStateOf("Новое название списка") }

    ShoppingListTheme {
        AddListDialog(
            listName = listName,
            onListNameChange = { listName = it },
            onDismiss = {},
            onConfirm = {},
            isRenameMode = true
        )
    }
}

// Preview для SelectField (активное состояние)
@Preview(name = "Select Field - Active", showBackground = true)
@Composable
private fun PreviewSelectFieldActive() {
    var value by remember { mutableStateOf("Мой список") }

    ShoppingListTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onPrimary
        ) {
            SelectField(
                label = "Название списка",
                value = value,
                onValueChange = { value = it },
                placeholder = "Введите название"
            )
        }
    }
}

// Preview для SelectField (пустое состояние)
@Preview(name = "Select Field - Empty", showBackground = true)
@Composable
private fun PreviewSelectFieldEmpty() {
    var value by remember { mutableStateOf("") }

    ShoppingListTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onPrimary
        ) {
            SelectField(
                label = "Название списка",
                value = value,
                onValueChange = { value = it },
                placeholder = "Введите название"
            )
        }
    }
}