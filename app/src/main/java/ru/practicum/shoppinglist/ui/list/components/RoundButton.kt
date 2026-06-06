package ru.practicum.shoppinglist.ui.list.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.ui.theme.DarkText
import ru.practicum.shoppinglist.ui.theme.LightContainer

@Composable
fun RoundButtonScreen(
    onClick: () -> Unit,
    @DrawableRes idIcon: Int,
    enabled: Boolean
) {
    val colorButtonEnabled = if (enabled) LightContainer else Color(0x211A141F)
    IconButton(
        modifier = Modifier.size(48.dp),
        onClick = onClick,
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = LightContainer,
            contentColor = DarkText,
            disabledContainerColor = colorButtonEnabled
        )
    ) {
        Icon(
            painter = painterResource(idIcon),
            contentDescription = null
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RoundButtonScreenPreview() {
    RoundButtonScreen(
        onClick = { },
        idIcon = R.drawable.ic_add,
        enabled = true
    )
}