package ru.practicum.shoppinglist.ui.list

import android.annotation.SuppressLint
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.launch
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.ui.list.components.BottomSheetMenu
import ru.practicum.shoppinglist.ui.list.components.BottomSheetScreen
import ru.practicum.shoppinglist.ui.list.components.IllustrationScreen
import ru.practicum.shoppinglist.ui.list.components.ProductsListScreen
import ru.practicum.shoppinglist.ui.list.components.ShowFab
import ru.practicum.shoppinglist.ui.list.viewmodel.NewProductData
import ru.practicum.shoppinglist.ui.list.viewmodel.ProductIntent
import ru.practicum.shoppinglist.ui.list.viewmodel.ProductViewModel
import ru.practicum.shoppinglist.ui.list.viewmodel.ProductsState
import ru.practicum.shoppinglist.ui.navigation.ActionBack
import ru.practicum.shoppinglist.ui.navigation.ActionMenu
import ru.practicum.shoppinglist.ui.navigation.AppBarTop
import ru.practicum.shoppinglist.ui.theme.ShoppingListTheme

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    onBack: () -> Unit = {},
    viewModel: ProductViewModel = hiltViewModel()
) {
    // ViewModel
    val state by viewModel.state.collectAsState()

    val shouldBeVisible = (state as? ProductsState.Content)?.isBottomSheetVisible ?: false

    // отслеживание BottomSheet
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // BottomSheetMenu
    val scopeMenu = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }
    val sheetStateMenu = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var bottomSheetHeight by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(ProductIntent.LoadProducts)
    }

    LaunchedEffect(shouldBeVisible) {
        if (showBottomSheet != shouldBeVisible) {
            showBottomSheet = shouldBeVisible
        }
    }

    LaunchedEffect(sheetStateMenu.isVisible) {
        if (!sheetStateMenu.isVisible) {
            showMenu = false
        }
    }

    ShoppingListTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Scaffold(
                topBar = {
                    AppBarTop(
                        title = stringResource(id = R.string.products),
                        back = ActionBack(isView = true, onClick = onBack),
                        menu = ActionMenu(isView = true, onClick = { showMenu = true })
                    )
                },
                content = { paddingValues ->
                    ContentScreen(
                        state = state,
                        viewModel = viewModel,
                        paddingValues = paddingValues
                    )
                }
            )

            ShowFabScreen(
                state = state,
                viewModel = viewModel,
                shouldBeVisible = shouldBeVisible,
                bottomSheetHeight = bottomSheetHeight,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 56.dp)
            )

            if (shouldBeVisible) {
                ModalBottomSheet(
                    onDismissRequest = {
                        viewModel.sendIntent(ProductIntent.HideBottomSheet)
                        showBottomSheet = false
                    },
                    sheetState = sheetState,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                bottomSheetHeight = coordinates.size.height
                            }
                    ) {
                        val newProductData = (state as? ProductsState.Content)?.newProductData
                        BottomSheetScreen(
                            productData = newProductData ?: NewProductData("", "", ""),
                            onValueChange = { field, value ->
                                viewModel.sendIntent(
                                    ProductIntent.OnInputValueChanged(
                                        field,
                                        value
                                    )
                                )
                            }
                        )
                    }
                }
            }

            if (showMenu) {
                showBottomSheet = false

                ModalBottomSheet(
                    onDismissRequest = { scopeMenu.launch { sheetStateMenu.hide() } },
                    sheetState = sheetStateMenu,
                ) {
                    BottomSheetMenu()
                }
            }
        }
    }
}

@Composable
private fun ShowFabScreen(
    state: ProductsState,
    viewModel: ProductViewModel,
    shouldBeVisible: Boolean,
    bottomSheetHeight: Int,
    modifier: Modifier
) {
    ShowFab(
        onClick = {
            if ((state as? ProductsState.Content)?.isBottomSheetVisible == true) {
                viewModel.sendIntent(ProductIntent.SaveNewProduct)
            } else {
                viewModel.sendIntent(ProductIntent.ShowAddProductBottomSheet)
            }
        },
        isBottomSheetVisible = shouldBeVisible,
        modifier = modifier,
        bottomSheetHeight = bottomSheetHeight
    )
}

@Composable
private fun ContentScreen(
    state: ProductsState,
    viewModel: ProductViewModel,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.primary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        when (state) {
            is ProductsState.Empty -> {
                IllustrationScreen(
                    image = R.drawable.ic_product_list,
                    title = R.string.lists_are_empty,
                    description = R.string.lists_are_empty_description,
                )
            }

            is ProductsState.Content -> {
                val products = (state as ProductsState.Content).products
                ProductsListScreen(
                    products = products,
                    onDelete = { productId ->
                        viewModel.sendIntent(ProductIntent.DeleteProduct(productId))
                    },
                    paddingValues = paddingValues
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ListScreenPreview() {
    ListScreen()
}