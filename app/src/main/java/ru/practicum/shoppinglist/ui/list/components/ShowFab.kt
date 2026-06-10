package ru.practicum.shoppinglist.ui.list.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.ui.theme.NoActiveElement

@Composable
fun ShowFab(
    onClick: () -> Unit = { },
    isBottomSheetVisible: Boolean = false,
    modifier: Modifier,
    //  offsetY: Float = 0f
    bottomSheetHeight: Int
) {
    //  val effectiveModifier = modifier.graphicsLayer(translationY = -offsetY)

    val offsetY = with(LocalDensity.current) {
        if (isBottomSheetVisible) bottomSheetHeight.toDp() else 0.dp
    }

    val effectiveModifier = modifier.offset(y = -offsetY)

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

// расширение для Modifier
fun Modifier.scrimWithCutout(
    isVisible: Boolean,
    cutoutPositon: android.graphics.Rect, // позиция кнопки
    backgroundColor: Color = Color.Black.copy(alpha = 0.5f) // цвет и прозрачность затемнения
): Modifier = this.then(
    Modifier.drawWithContent {
        // рисуем весь контент, наш UI
        drawContent()
        if (isVisible) {
            // рисуем полупрозрачный фон НАД контентом
            drawRect(backgroundColor)
            // вырезаем дырку в фоне - Canvas и BlendMode.Clear, стирает все, что под ним.
            drawIntoCanvas { canvas ->
                canvas.drawRect(
                    cutoutPositon.left.toFloat(),
                    cutoutPositon.top.toFloat(),
                    cutoutPositon.right.toFloat(),
                    cutoutPositon.bottom.toFloat(),
                    Paint().apply {
                        blendMode = BlendMode.Clear // Редим стирания
                    }
                )
            }
        }
    }
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ShowFabPreview() {
    ShowFab(
        onClick = {},
        //  image = R.drawable.ic_check,
        modifier = Modifier,
        bottomSheetHeight = 0
    )
}