package ru.practicum.shoppinglist.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.practicum.shoppinglist.R

data class ActionBack(
    val isView: Boolean = false,
    val onClick: (() -> Unit)? = null
)

data class ActionSearch(
    val isView: Boolean = false,
    val onClick: (() -> Unit)? = null,
)

data class ActionDelete(
    val isView: Boolean = false,
    val onClick: (() -> Unit)? = null,
)

data class ActionTheme(
    val isView: Boolean = false,
    val onClick: (() -> Unit)? = null,
    val isActive: Boolean = false,
)

data class ActionMenu(
    val isView: Boolean = false,
    val onClick: (() -> Unit)? = null,
)

const val WEIGHT_COLUMN = 0.5f

@Composable
fun AppBarTop(
    title: String,
    back: ActionBack = ActionBack(),
    search: ActionSearch = ActionSearch(),
    delete: ActionDelete = ActionDelete(),
    theme: ActionTheme = ActionTheme(),
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
                search,
                delete,
                theme,
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
            .padding(vertical = 20.dp),
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
private fun AppButtonAction(
    search: ActionSearch = ActionSearch(),
    delete: ActionDelete = ActionDelete(),
    theme: ActionTheme = ActionTheme(),
    menu: ActionMenu = ActionMenu()
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        SearchIcon(search)

        DeleteIcon(delete)

        ThemeIcon(theme)

        MenuIcon(menu)
    }
}

@Composable
private fun SearchIcon(
    search: ActionSearch
) {
    if (!search.isView) return

    Icon(
        painter = painterResource(R.drawable.ic_search),
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = Modifier
            .padding(end = 20.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { search.onClick?.invoke() },
    )
}

@Composable
private fun DeleteIcon(
    delete: ActionDelete
) {
    if (!delete.isView) return

    Icon(
        painter = painterResource(R.drawable.ic_delete),
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = Modifier
            .padding(end = 20.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { delete.onClick?.invoke() },

    )
}

@Composable
private fun ThemeIcon(
    theme: ActionTheme
) {
    if (!theme.isView) return

    val iconActive = if (theme.isActive) {
        R.drawable.ic_sun_mode
    } else {
        R.drawable.ic_dark_mode
    }

    Icon(
        painter = painterResource(id = iconActive),
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = Modifier
            .padding(end = 4.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { theme.onClick?.invoke() },
    )
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