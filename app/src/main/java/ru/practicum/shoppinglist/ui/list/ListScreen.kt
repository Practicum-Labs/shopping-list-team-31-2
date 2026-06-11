package ru.practicum.shoppinglist.ui.list

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
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
    val state by viewModel.state.collectAsState()

    val shouldBeVisible = (state as? ProductsState.Content)?.isBottomSheetVisible ?: false

    val scopeMenu = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }
    val sheetStateMenu = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = if (shouldBeVisible) SheetValue.PartiallyExpanded else SheetValue.Hidden,
            skipHiddenState = false
        )
    )

    val density = LocalDensity.current

    // Высота кнопки. Стандартный FAB = 56.dp
    // Мы делим пополам, чтобы кнопка висела ровно на границе (наполовину на шторке, наполовину над ней).
    // Если хочешь, чтобы кнопка была полностью НАД шторкой, используй полное значение высоты + отступ.
    val fabHalfHeightPx = with(density) { 56.dp.toPx() }

    // Отслеживаем Y-координату верхнего края BottomSheet
    val sheetOffsetY by remember {
        derivedStateOf {
            try {
                scaffoldState.bottomSheetState.requireOffset()
            } catch (_: IllegalStateException) {
                // Во время первой отрисовки offset может быть еще не вычислен,
                // поэтому возвращаем NaN, чтобы пока не рисовать или не двигать FAB.
                Float.NaN
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(ProductIntent.LoadProducts)
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
            BottomSheetScaffold(
                scaffoldState = rememberBottomSheetScaffoldState(
                    bottomSheetState = rememberStandardBottomSheetState(
                        initialValue = if (shouldBeVisible) SheetValue.PartiallyExpanded else SheetValue.Hidden,
                        skipHiddenState = false
                    )
                ),
                sheetContainerColor = MaterialTheme.colorScheme.primary,
                sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                sheetPeekHeight = 0.dp, // Высота шторки в свернутом виде
                sheetContent = {
                    // Содержимое BottomSheet
                    val newProductData = (state as? ProductsState.Content)?.newProductData
                    BottomSheetScreen(
                        productData = newProductData ?: NewProductData("", "", ""),
                        onValueChange = { field, value ->
                            viewModel.sendIntent(
                                ProductIntent.OnInputValueChanged(fieldType = field, value = value)
                            )
                        }
                    )
                }
            ) { paddingValues ->
                // Основное содержимое экрана, которое находится ПОД шторкой
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
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
                }
            }

            ShowFabScreen(
                viewModel = viewModel,
                shouldBeVisible = shouldBeVisible,
                bottomSheetHeight = fabHalfHeightPx,
                sheetOffsetY = sheetOffsetY,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp)

            )

            if (showMenu) {
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
    viewModel: ProductViewModel,
    shouldBeVisible: Boolean,
    bottomSheetHeight: Float,
    sheetOffsetY: Float,
    modifier: Modifier
) {
    val offSetY = if (!sheetOffsetY.isNaN()) sheetOffsetY else 0f

    val intentToSend = if (shouldBeVisible) {
        ProductIntent.SaveNewProduct
    } else {
        ProductIntent.ShowBottomSheet
    }

    ShowFab(
        onClick = {
            viewModel.sendIntent(intentToSend)
        },
        isBottomSheetVisible = shouldBeVisible,
        modifier = modifier,
        bottomSheetHeight = bottomSheetHeight,
        offsetY = offSetY
    )
}

@Composable
private fun ContentScreen(
    state: ProductsState,
    viewModel: ProductViewModel,
    paddingValues: PaddingValues
) {
    val products = (state as ProductsState.Content).products

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.primary),
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
                paddingValues = paddingValues
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ListScreenPreview() {
    ListScreen()
}