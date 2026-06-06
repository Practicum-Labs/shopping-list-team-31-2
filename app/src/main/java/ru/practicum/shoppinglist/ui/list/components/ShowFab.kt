package ru.practicum.shoppinglist.ui.list.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.ui.theme.NoActiveElement

@Composable
fun ShowFab(
    onClick: () -> Unit = { },
    @DrawableRes image: Int,
    modifier: Modifier
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        FloatingActionButton(
            onClick = onClick,
            modifier = modifier,
            containerColor = NoActiveElement
        ) {
            Icon(
                painter = painterResource(image),
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
        image = R.drawable.ic_check,
        modifier = Modifier
    )
}