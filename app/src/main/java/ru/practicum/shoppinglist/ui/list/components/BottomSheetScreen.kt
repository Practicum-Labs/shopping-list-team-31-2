package ru.practicum.shoppinglist.ui.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.practicum.shoppinglist.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetScreen() {
    val units = stringArrayResource(R.array.units_lists) // .toList()

    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedUnit by remember { mutableStateOf("") }

    val valueIntQuantity = quantity.toIntOrNull() ?: 0
    val enabledButtonMinus = valueIntQuantity > 0

    Surface(
        modifier = Modifier.fillMaxWidth().imePadding(),
        color = MaterialTheme.colorScheme.inverseSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = {
                    Text(
                        text = stringResource(R.string.product),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.add_new_product)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Unspecified,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth() // .padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically

            ) {
                // Ввод количества
                OutlinedTextField(
                    value = quantity,
                    onValueChange = {
                        // Позволяем вводить только цифры
                        if (it.isEmpty() || it.matches(Regex("\\d*"))) {
                            quantity = it
                        }
                    },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.quantity),
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.quantity),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f),
                )
                // Список с элементами
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier
                        .weight(1f)
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedUnit,
                        onValueChange = {},
                        label = {
                            Text(
                                text = stringResource(R.string.units),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.units),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        singleLine = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor(
                            type = ExposedDropdownMenuAnchorType.PrimaryNotEditable
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        units.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(unit) },
                                onClick = {
                                    selectedUnit = unit
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Кнопки
                RoundButtonScreen(
                    onClick = { quantity = modifyIntToString(quantity, false) },
                    idIcon = R.drawable.ic_remove,
                    enabled = enabledButtonMinus
                )

                RoundButtonScreen(
                    onClick = { quantity = modifyIntToString(quantity, true) },
                    idIcon = R.drawable.ic_add,
                    enabled = true
                )

            }
        }
    }
}

fun modifyIntToString(numberAsString: String, increment: Boolean = true): String {
    val number = numberAsString.toIntOrNull() ?: 0

    val newValue = number + if (increment) 1 else -1

    return if (newValue > 0) newValue.toString() else ""
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BottomSheetScreenPreview() {
    BottomSheetScreen()
}