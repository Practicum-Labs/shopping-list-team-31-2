package ru.practicum.shoppinglist.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.ui.list.components.BottomSheetScreen
import ru.practicum.shoppinglist.ui.list.components.IllustrationScreen
import ru.practicum.shoppinglist.ui.list.components.ProductsListScreen
import ru.practicum.shoppinglist.ui.list.components.ShowFab
import ru.practicum.shoppinglist.ui.list.components.getLists
import ru.practicum.shoppinglist.ui.theme.ShoppingListTheme

data class ActionBack(
    val isView: Boolean = false,
    val onClick: (() -> Unit)? = null
)

data class ActionMenu(
    val isView: Boolean = false,
    val onClick: (() -> Unit)? = null,
)

const val WEIGHT_COLUMN = 0.5f

data class TestProduct(
    val id: Int,
    val name: String,
    val quantity: String,
    val unit: String,
    val isChecked: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    onBack: () -> Unit = {},
    onMenu: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var showDialogAdd by remember { mutableStateOf(false) }
    val lists = getLists()

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  //  var fabOffsetY by remember { mutableStateOf(0) }

    LaunchedEffect(sheetState.isVisible) {
        if (!sheetState.isVisible) {
            showBottomSheet = false
        }
    }

    ShoppingListTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    AppBarTop(
                        title = stringResource(id = R.string.products),
                        back = ActionBack(isView = true, onClick = onBack),
                        menu = ActionMenu(isView = true, onClick = onMenu)
                    )
                },
                content = { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        if (lists.isEmpty()) {
                            IllustrationScreen(
                                image = R.drawable.ic_product_list,
                                title = R.string.lists_are_empty,
                                description = R.string.lists_are_empty_description,
                            )
                        } else {
                            ProductsListScreen(lists, paddingValues)
                        }
                    }
                }
            )

            ShowFab(
                onClick = { showBottomSheet = true },
                image = if (showDialogAdd) {
                    R.drawable.ic_check
                } else {
                    R.drawable.ic_add
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 56.dp)
            //        .offset { IntOffset(x = 0, y = fabOffsetY) }
            )

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { scope.launch { sheetState.hide() } },
                    sheetState = sheetState,
                ) {
                    BottomSheetScreen(
                    )
                }
            }

        }
    }
}

@Composable
private fun AppBarTop(
    title: String,
    back: ActionBack = ActionBack(),
    menu: ActionMenu = ActionMenu()
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppButtonBack(
            isViewIcon = back.isView,
            onClick = back.onClick
        )

        AppTitle(title = title)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(WEIGHT_COLUMN)
                .padding(end = 8.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            AppButtonAction(
                menu
            )
        }
    }
}

@Composable
private fun AppButtonBack(
    isViewIcon: Boolean,
    onClick: (() -> Unit)?
) {
    if (isViewIcon) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_back),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(start = 16.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onClick?.invoke() },
        )
    }
}

@Composable
private fun AppTitle(
    title: String
) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxHeight()
            .padding(start = 16.dp)
            .padding(vertical = 16.dp),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun AppButtonAction(
    menu: ActionMenu = ActionMenu()
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.background(MaterialTheme.colorScheme.primary)
    ) {
        MenuIcon(menu)
    }
}

@Composable
private fun MenuIcon(
    menu: ActionMenu
) {
    if (!menu.isView) return

    Icon(
        painter = painterResource(R.drawable.ic_more_vert),
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = Modifier
            .padding(end = 20.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { menu.onClick?.invoke() },
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ListScreenPreview() {
    ListScreen()
}
