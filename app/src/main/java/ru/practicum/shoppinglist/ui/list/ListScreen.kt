package ru.practicum.shoppinglist.ui.list
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.ui.list.components.BottomSheetMenu
import ru.practicum.shoppinglist.ui.list.components.BottomSheetScreen
import ru.practicum.shoppinglist.ui.list.components.IllustrationScreen
import ru.practicum.shoppinglist.ui.list.components.ProductsListScreen
import ru.practicum.shoppinglist.ui.list.components.ShowFab
import ru.practicum.shoppinglist.ui.list.components.SortMenuContent
import ru.practicum.shoppinglist.ui.list.viewmodel.NewProductData
import ru.practicum.shoppinglist.ui.list.viewmodel.ProductIntent
import ru.practicum.shoppinglist.ui.list.viewmodel.ProductViewModel
import ru.practicum.shoppinglist.ui.list.viewmodel.ProductsState
import ru.practicum.shoppinglist.ui.list.viewmodel.UiEffect
import ru.practicum.shoppinglist.ui.main.DeleteDialog
import ru.practicum.shoppinglist.ui.navigation.ActionBack
import ru.practicum.shoppinglist.ui.navigation.ActionMenu
import ru.practicum.shoppinglist.ui.navigation.AppBarTop
import ru.practicum.shoppinglist.ui.theme.NoActiveElement
import ru.practicum.shoppinglist.ui.theme.ShoppingListTheme

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    listId: Long,
    listName: String = "",
    onBack: () -> Unit = {},
    viewModel: ProductViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.setShoppingListId(listId)
        viewModel.sendIntent(ProductIntent.LoadProducts)
    }

    val state by viewModel.state.collectAsState()

    var savedName by rememberSaveable { mutableStateOf("") }
    var savedQuantity by rememberSaveable { mutableStateOf("") }
    var savedUnit by rememberSaveable { mutableStateOf("") }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var showClearPurchasedDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        val currentData = (state as? ProductsState.Content)?.newProductData
        if (currentData != null) {
            savedName = currentData.name
            savedQuantity = currentData.quantity
            savedUnit = currentData.unit
        }
    }

    val shouldBeVisible = when (state) {
        is ProductsState.Content -> (state as ProductsState.Content).isBottomSheetVisible
        else -> false
    }

    val isSortMenuVisible = (state as? ProductsState.Content)?.isSortMenuVisible ?: false
    val context = LocalContext.current

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { true }
    )

    var bottomSheetHeightPx by remember { mutableStateOf(0f) }
    var forceUpdate by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is UiEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                is UiEffect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    if (effect.message == "Продукт добавлен") {
                        savedName = ""
                        savedQuantity = ""
                        savedUnit = ""
                    }
                }
            }
        }
    }

    LaunchedEffect(shouldBeVisible) {
        if (shouldBeVisible) {
            bottomSheetState.show()
        } else {
            bottomSheetState.hide()
            bottomSheetHeightPx = 0f
            forceUpdate++
        }
    }

    val scopeMenu = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }
    val sheetStateMenu = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(sheetStateMenu.isVisible) {
        if (!sheetStateMenu.isVisible) {
            showMenu = false
        }
    }

    val sortSheetState = rememberModalBottomSheetState()

    ShoppingListTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding() // ← добавляем отступ под статус бар
                .background(MaterialTheme.colorScheme.primary)
        ) {
            AppBarTop(
                title = if (listName.isNotEmpty()) listName else stringResource(id = R.string.products),
                back = ActionBack(isView = true, onClick = onBack),
                menu = ActionMenu(isView = true, onClick = { showMenu = true })
            )

            Box(modifier = Modifier.weight(1f)) {
                ContentScreen(
                    state = state,
                    viewModel = viewModel
                )

                if (!shouldBeVisible) {
                    ShowFabScreen(
                        viewModel = viewModel,
                        shouldBeVisible = shouldBeVisible,
                        bottomSheetHeight = bottomSheetHeightPx,
                        forceUpdate = forceUpdate,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp)
                    )
                }

                BottomSheetContent(
                    state = state,
                    viewModel = viewModel,
                    bottomSheetState = bottomSheetState,
                    savedName = savedName,
                    savedQuantity = savedQuantity,
                    savedUnit = savedUnit,
                    onHeightChange = { newHeight ->
                        if (bottomSheetHeightPx != newHeight) {
                            bottomSheetHeightPx = newHeight
                            forceUpdate++
                        }
                    }
                )

                MenuBottomSheet(
                    showMenu = showMenu,
                    sheetStateMenu = sheetStateMenu,
                    scopeMenu = scopeMenu,
                    onSortClick = {
                        scopeMenu.launch { sheetStateMenu.hide() }
                        viewModel.sendIntent(ProductIntent.ShowSortMenu)
                    },
                    onDeleteAllClick = {
                        scopeMenu.launch { sheetStateMenu.hide() }
                        showDeleteAllDialog = true
                    },
                    onClearPurchasedClick = {
                        scopeMenu.launch { sheetStateMenu.hide() }
                        showClearPurchasedDialog = true
                    }
                )

                SortMenuBottomSheet(
                    isSortMenuVisible = isSortMenuVisible,
                    sortSheetState = sortSheetState,
                    onSortAlphabetically = {
                        viewModel.sendIntent(ProductIntent.SortProductsAlphabetically)
                    }
                )

                if (showDeleteAllDialog) {
                    DeleteDialog(
                        title = stringResource(R.string.delete_all_products),
                        onDismiss = { showDeleteAllDialog = false },
                        onConfirm = {
                            showDeleteAllDialog = false
                            viewModel.sendIntent(ProductIntent.DeleteAllProducts)
                        }
                    )
                }

                if (showClearPurchasedDialog) {
                    DeleteDialog(
                        title = stringResource(R.string.delete_all_purchased),
                        onDismiss = { showClearPurchasedDialog = false },
                        onConfirm = {
                            showClearPurchasedDialog = false
                            viewModel.sendIntent(ProductIntent.DeletePurchasedProducts)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ShowFabScreen(
    viewModel: ProductViewModel,
    shouldBeVisible: Boolean,
    bottomSheetHeight: Float,
    forceUpdate: Int,
    modifier: Modifier
) {
    val density = LocalDensity.current
    val bottomPaddingDp = if (shouldBeVisible && bottomSheetHeight > 0) {
        val bottomSheetHeightDp = with(density) { bottomSheetHeight.toDp() }
        bottomSheetHeightDp + 32.dp
    } else {
        56.dp
    }

    ShowFab(
        key = forceUpdate,
        onClick = {
            if (shouldBeVisible) {
                viewModel.sendIntent(ProductIntent.SaveNewProduct)
            } else {
                viewModel.sendIntent(ProductIntent.ShowBottomSheet)
            }
        },
        isBottomSheetVisible = shouldBeVisible,
        modifier = modifier.padding(bottom = bottomPaddingDp),
    )
}

@Composable
private fun ContentScreen(
    state: ProductsState,
    viewModel: ProductViewModel,
) {
    val products = when (state) {
        is ProductsState.Content -> {
            state.products
        }

        is ProductsState.Loading -> {
            emptyList()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            if (products.isEmpty()) {
                IllustrationScreen(
                    image = R.drawable.ic_product_list,
                    title = R.string.lists_are_empty,
                    description = R.string.lists_are_empty_description,
                )
            } else {
                ProductsListScreen(
                    products = products,
                    onDelete = { productId ->
                        viewModel.sendIntent(ProductIntent.DeleteProduct(productId))
                    },
                    onTogglePurchased = { productId, isPurchased ->
                        viewModel.sendIntent(
                            ProductIntent.ToggleProductPurchased(
                                productId,
                                isPurchased
                            )
                        )
                    },
                    paddingValues = PaddingValues(0.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetContent(
    state: ProductsState,
    viewModel: ProductViewModel,
    bottomSheetState: SheetState,
    savedName: String,
    savedQuantity: String,
    savedUnit: String,
    onHeightChange: (Float) -> Unit
) {
    if ((state as? ProductsState.Content)?.isBottomSheetVisible == true) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.sendIntent(ProductIntent.HideBottomSheet) },
            sheetState = bottomSheetState,
            containerColor = MaterialTheme.colorScheme.inverseSurface
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    val productDataToUse = (state as? ProductsState.Content)?.newProductData
                        ?: NewProductData(savedName, savedQuantity, savedUnit)

                    BottomSheetScreen(
                        productData = productDataToUse,
                        onValueChange = { field, value ->
                            viewModel.sendIntent(
                                ProductIntent.OnInputValueChanged(
                                    fieldType = field,
                                    value = value
                                )
                            )
                        }
                    )
                }

                FloatingActionButton(
                    onClick = { viewModel.sendIntent(ProductIntent.SaveNewProduct) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 16.dp, bottom = 32.dp),
                    containerColor = NoActiveElement
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = null,
                        tint = null
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuBottomSheet(
    showMenu: Boolean,
    sheetStateMenu: SheetState,
    scopeMenu: CoroutineScope,
    onSortClick: () -> Unit,
    onDeleteAllClick: () -> Unit,
    onClearPurchasedClick: () -> Unit
) {
    if (showMenu) {
        ModalBottomSheet(
            onDismissRequest = { scopeMenu.launch { sheetStateMenu.hide() } },
            sheetState = sheetStateMenu,
            containerColor = MaterialTheme.colorScheme.inverseSurface,
        ) {
            BottomSheetMenu(
                onSortClick = onSortClick,
                onDeleteAllClick = onDeleteAllClick,
                onClearPurchasedClick = onClearPurchasedClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortMenuBottomSheet(
    isSortMenuVisible: Boolean,
    sortSheetState: SheetState,
    onSortAlphabetically: () -> Unit
) {
    if (isSortMenuVisible) {
        ModalBottomSheet(
            onDismissRequest = { onSortAlphabetically() },
            sheetState = sortSheetState,
            containerColor = MaterialTheme.colorScheme.inverseSurface,
        ) {
            SortMenuContent(
                onSortAlphabetically = onSortAlphabetically
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ListScreenPreview() {
    ListScreen(listId = 1L)
}