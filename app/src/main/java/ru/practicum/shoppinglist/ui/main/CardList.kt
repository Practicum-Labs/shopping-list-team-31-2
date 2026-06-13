package ru.practicum.shoppinglist.ui.main

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.ui.main.Variables.ANIMATION_SPEC
import ru.practicum.shoppinglist.ui.main.Variables.MIN_OFFSET
import ru.practicum.shoppinglist.ui.theme.ShoppingListTheme
import kotlin.math.roundToInt

@Composable
fun CardList(
    listId: Long,
    iconCard: Int,
    textCard: String,
    onIconClick: () -> Unit,
    onEdit: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
    onCardClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val density = LocalDensity.current
    val actionButtonsWidth = 168.dp

    val closeActions: () -> Unit = {
        scope.launch {
            offsetX.animateTo(0f, animationSpec = tween(ANIMATION_SPEC))
        }
    }

    ShoppingListTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            SwipeActions(
                onEdit = onEdit,
                onCopy = onCopy,
                onDelete = onDelete,
                onClose = closeActions
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .clickable { onCardClick() }
                    .swipeToRevealActions(
                        offsetX = offsetX,
                        density = density,
                        actionWidth = actionButtonsWidth,
                        onSwipeStateChange = { },
                        coroutineScope = scope
                    ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                CardContent(
                    iconCard = iconCard,
                    textCard = textCard,
                    onIconClick = onIconClick
                )
            }
        }
    }
}

fun Modifier.swipeToRevealActions(
    offsetX: Animatable<Float, *>,
    density: Density,
    actionWidth: Dp,
    onSwipeStateChange: (Boolean) -> Unit,
    coroutineScope: CoroutineScope
): Modifier = this.pointerInput(Unit) {
    detectHorizontalDragGestures(
        onDragStart = {
            coroutineScope.launch { offsetX.stop() }
        },
        onDragEnd = {
            coroutineScope.launch {
                val maxOffset = -with(density) { actionWidth.toPx() }
                val isOpen = offsetX.value < MIN_OFFSET

                if (isOpen) {
                    offsetX.animateTo(maxOffset, animationSpec = tween(ANIMATION_SPEC))
                    onSwipeStateChange(true)
                } else {
                    offsetX.animateTo(0f, animationSpec = tween(ANIMATION_SPEC))
                    onSwipeStateChange(false)
                }
            }
        },
        onDragCancel = {
            coroutineScope.launch {
                offsetX.animateTo(0f, animationSpec = tween(ANIMATION_SPEC))
                onSwipeStateChange(false)
            }
        },
        onHorizontalDrag = { _, dragAmount ->
            coroutineScope.launch {
                val maxOffset = -with(density) { actionWidth.toPx() }
                val newOffset = offsetX.value + dragAmount
                offsetX.snapTo(newOffset.coerceIn(maxOffset, 0f))
            }
        }
    )
}
@Composable
private fun SwipeActions(
    onEdit: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp)
            ),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionButton(icon = R.drawable.ic_edit, description = "Edit", onClick = { onClose(); onEdit() })
        Spacer(modifier = Modifier.width(8.dp))
        ActionButton(icon = R.drawable.ic_copy, description = "Copy", onClick = { onClose(); onCopy() })
        Spacer(modifier = Modifier.width(8.dp))
        ActionButton(icon = R.drawable.ic_delete, description = "Delete", onClick = { onClose(); onDelete() })
        Spacer(modifier = Modifier.width(16.dp))
    }
}

@Composable
private fun CardContent(
    iconCard: Int,
    textCard: String,
    onIconClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(if (iconCard != 0 && iconCard != -1) iconCard else R.drawable.ic_set_basket),
                contentDescription = null,
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onIconClick() }
                    .size(24.dp)
                    .align(Alignment.Center),
                tint = MaterialTheme.colorScheme.tertiaryFixed
            )
        }
        Text(
            text = textCard,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun ActionButton(icon: Int, description: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                color = MaterialTheme.colorScheme.onSecondary,
                shape = CircleShape
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = description,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Preview(name = "CardList - Recipes", showBackground = true)
@Composable
private fun PreviewCardListRecipes() {
    ShoppingListTheme {
        CardList(
            listId = 0,
            iconCard = R.drawable.ic_set_cake,
            textCard = "Мой список",
            onIconClick = {},
            onEdit = {},
            onCopy = {},
            onDelete = {}
        )
    }
}
