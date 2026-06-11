package ru.practicum.shoppinglist.ui.list.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.ui.theme.NoActiveElement

@Composable
fun ShowFab(
    onClick: () -> Unit = { },
    isBottomSheetVisible: Boolean,
    modifier: Modifier,
    offsetY: Float = 0f,
    bottomSheetHeight: Float = 0f,
) {
    val effectiveModifier = modifier.graphicsLayer(translationY = offsetY - bottomSheetHeight)

    AnimatedContent(targetState = isBottomSheetVisible, label = "") { isVisible ->
        val imageButton = if (isVisible) R.drawable.ic_check else R.drawable.ic_add

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            FloatingActionButton(
                onClick = onClick,
                modifier = effectiveModifier,
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
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ShowFabPreview() {
    ShowFab(
        onClick = {},
        isBottomSheetVisible = false,
        modifier = Modifier,
        offsetY = 0f,
        bottomSheetHeight = 0f,
    )
}