package ru.practicum.shoppinglist.ui.main

import android.content.Context
import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.domain.model.ShoppingList
import ru.practicum.shoppinglist.ui.main.viewmodel.ShoppingListIntent
import ru.practicum.shoppinglist.ui.main.viewmodel.ShoppingListState
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
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedCardIndex by remember { mutableStateOf(-1L) }
    var pendingIcon by remember { mutableStateOf<Int?>(null) }

    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    HandleMainScreenEffects(
        viewModel = viewModel,
        context = context,
        onClearErrors = { viewModel.processIntent(ShoppingListIntent.ClearErrors) },
        onUpdateIcon = { id, icon ->
            viewModel.processIntent(ShoppingListIntent.UpdateListIcon(id, icon))
        },
        onClearNewListState = { viewModel.processIntent(ShoppingListIntent.ClearNewListState) },
        onSetPendingIcon = { pendingIcon = it },
        onSetShowBottomSheet = { showBottomSheet = it },
        onSetSelectedCardIndex = { selectedCardIndex = it }
    )

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

                if (state.shoppingLists.isEmpty()) {
                    EmptyListsScreen()
                } else {
                    ShoppingListsContent(
                        modifier = Modifier.weight(1f),
                        lists = state.shoppingLists,
                        onIconClick = { id ->
                            showBottomSheet = true
                            selectedCardIndex = id
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 56.dp),
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

            MainScreenDialogs(
                state = state,
                viewModel = viewModel,
                onSearch = onSearch,
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                onBack = onBack,
                onDelete = onDelete,
                onDeleteConfirm = { viewModel.processIntent(ShoppingListIntent.Delete) },
                onDeleteDismiss = { onDelete = false },
                showDialog = showDialog,
                onDialogDismiss = { showDialog = false },
                onDialogConfirm = {
                    showDialog = false
                    pendingIcon = null
                    showBottomSheet = true
                    viewModel.processIntent(ShoppingListIntent.AddShoppingList)
                },
                showBottomSheet = showBottomSheet,
                onBottomSheetDismiss = {
                    showBottomSheet = false
                    selectedCardIndex = -1
                    pendingIcon = null
                    viewModel.processIntent(ShoppingListIntent.ClearNewListState)
                },
                onIconSelected = { selectedIcon ->
                    val idToUpdate = if (selectedCardIndex != -1L) selectedCardIndex else state.addedId
                    if (idToUpdate != 0L) {
                        viewModel.processIntent(
                            ShoppingListIntent.UpdateListIcon(id = idToUpdate, iconResId = selectedIcon)
                        )
                        showBottomSheet = false
                        selectedCardIndex = -1
                        viewModel.processIntent(ShoppingListIntent.ClearNewListState)
                    } else {
                        pendingIcon = selectedIcon
                        showBottomSheet = false
                    }
                }
            )
        }
    }
}

@Composable
private fun EmptyListsScreen() {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(top = 120.dp)
            .padding(horizontal = 44.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.fillMaxWidth().height(300.dp),
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
}

@Composable
private fun HandleMainScreenEffects(
    viewModel: ShoppingListViewModel,
    context: Context,
    onClearErrors: () -> Unit,
    onUpdateIcon: (Long, Int) -> Unit,
    onClearNewListState: () -> Unit,
    onSetPendingIcon: (Int?) -> Unit,
    onSetShowBottomSheet: (Boolean) -> Unit,
    onSetSelectedCardIndex: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            val displayMessage = try {
                context.getString(message.toInt())
            } catch (_: Exception) {
                message
            }
            Toast.makeText(context, displayMessage, Toast.LENGTH_SHORT).show()
            onSetShowBottomSheet(false)
            onSetPendingIcon(null)
            onClearErrors()
        }
    }

    LaunchedEffect(state.addedId) {
        if (state.addedId != 0L) {
            val currentPendingIcon = state.addedId
            onSetSelectedCardIndex(state.addedId)
            onSetShowBottomSheet(true)
            onClearNewListState()
        }
    }
}

@Composable
private fun ShoppingListsContent(
    modifier: Modifier = Modifier,
    lists: List<ShoppingList>,
    onIconClick: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier

            .padding(bottom = 20.dp)
    ) {
        items(items = lists, key = { it.id }) { item ->
            CardList(
                iconCard = item.icon,
                textCard = item.name,
                onIconClick = { onIconClick(item.id) },
                onEdit = { },
                onCopy = { },
                onDelete = { }
            )
        }
    }
}

@Composable
private fun MainScreenDialogs(
    state: ShoppingListState,
    viewModel: ShoppingListViewModel,
    onSearch: Boolean,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onBack: () -> Unit,
    onDelete: Boolean,
    onDeleteConfirm: () -> Unit,
    onDeleteDismiss: () -> Unit,
    showDialog: Boolean,
    onDialogDismiss: () -> Unit,
    onDialogConfirm: () -> Unit,
    showBottomSheet: Boolean,
    onBottomSheetDismiss: () -> Unit,
    onIconSelected: (Int) -> Unit
) {
    if (onSearch) {
        SearchOverlay(
            searchText = searchText,
            onSearchTextChange = onSearchTextChange,
            onBack = onBack
        )
    }

    if (onDelete) {
        DeleteDialog(
            title = stringResource(R.string.delete_all_lists),
            onConfirm = {
                onDeleteDismiss()
                onDeleteConfirm()
            },
            onDismiss = onDeleteDismiss
        )
    }

    if (showDialog) {
        AddListDialog(
            listName = state.addedName,
            onListNameChange = { newText ->
                viewModel.processIntent(ShoppingListIntent.SetAddedName(newText))
            },
            onDismiss = onDialogDismiss,
            onConfirm = {
                onDialogConfirm()
            }
        )
    }

    if (showBottomSheet) {
        IconSelectionBottomSheet(
            onIconSelected = onIconSelected,
            onDismiss = onBottomSheetDismiss
        )
    }
}