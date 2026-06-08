package ru.practicum.shoppinglist.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.domain.model.ShoppingList
import ru.practicum.shoppinglist.ui.main.viewmodel.ShoppingListIntent
import ru.practicum.shoppinglist.ui.main.viewmodel.ShoppingListViewModel
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
    viewModel: ShoppingListViewModel = hiltViewModel()
) {
    var onSearch by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var onDelete by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var listName by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedCardIndex by remember { mutableStateOf(-1L) }

    val state by viewModel.uiState.collectAsState()

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

                if (!state.shoppingLists.isNotEmpty()) {
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

                        // Для теста
//                        Button(
//                            onClick = { lists = true },
//
//                        ) {
//                            Text(
//                                "Test",
//                                color = Color.Red,
//                                style = MaterialTheme.typography.titleMedium
//                            )
//                        }

                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                    ) {
                        items(
                            items = state.shoppingLists,
                            key = { it.id }
                        ) { item ->
                            CardList(
                                iconCard = R.drawable.ic_list_alt,
                                textCard = item.name,
                                onIconClick = {
                                    showBottomSheet = true
                                    selectedCardIndex = item.id
                                },
                                onEdit = { },
                                onCopy = { },
                                onDelete = { }
                            )
                        }
                    }

                    // Для теста

//                    IconButton(
//                        onClick = onBack,
//                        modifier = Modifier
//                            .padding(all = 16.dp)
//                            .size(40.dp),
//                    ) {
//                        Icon(
//                            painter = painterResource(R.drawable.ic_arrow_back),
//                            contentDescription = null
//                        )
//                    }

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
                        modifier = Modifier
                            .clickable { showDialog = true }
                            .size(78.dp)

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
            DeleteDialog(
                title = stringResource(R.string.delete_all_lists),
                onConfirm = { onDelete = false },
                onDismiss = { onDelete = false }
            )

        }

        if (showDialog) {
            AddListDialog(
                listName = listName,
                onListNameChange = { viewModel.processIntent(ShoppingListIntent.SetAddedName(listName)) },
                onDismiss = { showDialog = false },
                onConfirm = { showDialog = false
                            viewModel.processIntent(ShoppingListIntent.AddShoppingList(ShoppingList(
                                id = state.addedId,
                                name = state.addedName,
                                icon = state.addedIcon
                            )))},
            )

        }

    }

    if (showBottomSheet && selectedCardIndex != -1L) {
        IconSelectionBottomSheet(
            onIconSelected = { selectedIcon ->
                showBottomSheet = false
                selectedCardIndex = -1
            },
            onDismiss = {
                showBottomSheet = false
                selectedCardIndex = -1
            }
        )
    }
}
