package ru.practicum.shoppinglist.ui.list.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.ui.theme.NoActiveElement

@Composable
fun ShowFab(
    key: Int = 0,
    onClick: () -> Unit = { },
    isBottomSheetVisible: Boolean,
    modifier: Modifier
) {
    key(key) {
        val imageButton = if (isBottomSheetVisible) R.drawable.ic_check else R.drawable.ic_add

        FloatingActionButton(
            onClick = {
                onClick()
            },
            modifier = modifier,
            containerColor = NoActiveElement
        ) {
            Icon(
                painter = painterResource(imageButton),
                contentDescription = null,
                tint = null
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ShowFabPreview() {
    ShowFab(
        onClick = {},
        isBottomSheetVisible = false,
        modifier = Modifier
    )
}