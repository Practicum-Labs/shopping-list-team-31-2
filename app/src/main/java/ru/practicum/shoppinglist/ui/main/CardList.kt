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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.ui.main.Variables.ACTION_BUTTON_WIDTH
import ru.practicum.shoppinglist.ui.main.Variables.ANIMATION_SPEC
import ru.practicum.shoppinglist.ui.main.Variables.MIN_OFFSET
import ru.practicum.shoppinglist.ui.main.Variables.SWIPE_ACTION_THRESHOLD
import ru.practicum.shoppinglist.ui.main.Variables.VISIBLE_PART_WIDTH
import ru.practicum.shoppinglist.ui.theme.ShoppingListTheme
import kotlin.math.roundToInt

const val SWIPE_NONE = "none"
const val SWIPE_SHORT_LEFT = "short_left"
const val SWIPE_FULL_LEFT = "full_left"
const val SWIPE_FULL_RIGHT = "full_right"

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
    var fullScreenWidthPx by remember { mutableStateOf(0) }

    var swipeState by remember { mutableStateOf("none") }

    val closeActions: () -> Unit = {
        scope.launch {
            offsetX.animateTo(0f, animationSpec = tween(ANIMATION_SPEC))
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { size ->
                    fullScreenWidthPx = size.width
                }
        )
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
                onClose = closeActions,
                swipeState = swipeState
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .clickable { onCardClick() }
                    .swipeToRevealActions(
                        config = SwipeConfig(
                            offsetX = offsetX,
                            density = density,
                            onSwipeState = { newState -> swipeState = newState },
                            scope = scope,
                            onSwipeLeft = { },
                            onSwipeRight = onEdit,
                            fullWidthPx = fullScreenWidthPx
                        )
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
    config: SwipeConfig
): Modifier = this.pointerInput(Unit) {
    val fullSwipeThresholdPx =
        if (config.fullWidthPx > 0) config.fullWidthPx / 2f else SWIPE_ACTION_THRESHOLD
    val maxOffset = with(density) { ACTION_BUTTON_WIDTH.dp.toPx() }
    val visiblePartWidthPx = with(density) { VISIBLE_PART_WIDTH.dp.toPx() }

    fun animateToClosedState() {
        val finalStatus = getFinalState(config, fullSwipeThresholdPx)
        config.onSwipeState(finalStatus)
    }

    suspend fun handleDragEnd(currentValue: Float) {
        var finalStatus = SWIPE_NONE
        if (currentValue <= -fullSwipeThresholdPx) {
            config.onSwipeLeft()
            val targetPosition = -config.fullWidthPx + visiblePartWidthPx
            config.offsetX.animateTo(targetPosition, animationSpec = tween(ANIMATION_SPEC))
            finalStatus = SWIPE_FULL_LEFT
        } else if (currentValue >= fullSwipeThresholdPx) {
            config.onSwipeRight()
            val targetPosition = config.fullWidthPx - visiblePartWidthPx
            config.offsetX.animateTo(targetPosition, animationSpec = tween(ANIMATION_SPEC))
            finalStatus = SWIPE_FULL_RIGHT
        } else {
            if (currentValue < MIN_OFFSET) {
                config.offsetX.animateTo(-maxOffset, animationSpec = tween(ANIMATION_SPEC))
                finalStatus = SWIPE_SHORT_LEFT
            } else {
                config.offsetX.animateTo(0f, animationSpec = tween(ANIMATION_SPEC))
            }
        }
        config.onSwipeState(finalStatus)
    }

    detectHorizontalDragGestures(
        onDragStart = {
            config.scope.launch { config.offsetX.stop() }
        },
        onDragEnd = {
            config.scope.launch {
                handleDragEnd(config.offsetX.value)
            }
        },
        onDragCancel = {
            config.scope.launch {
                config.offsetX.animateTo(0f, animationSpec = tween(ANIMATION_SPEC))
                animateToClosedState()
            }
        },
        onHorizontalDrag = { _, dragAmount ->
            config.scope.launch {
                val newOffset = config.offsetX.value + dragAmount
                val clampedValue = newOffset.coerceIn(-fullSwipeThresholdPx, fullSwipeThresholdPx)
                config.offsetX.snapTo(clampedValue)
                animateToClosedState()
            }
        }
    )
}

@Composable
private fun SwipeActions(
    onEdit: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit,
    swipeState: String
) {
    var arrangementCenter = Arrangement.End

    if (swipeState == SWIPE_FULL_LEFT) {
        arrangementCenter = Arrangement.Center
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp)
            ),
        horizontalArrangement = arrangementCenter,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (swipeState) {
            SWIPE_SHORT_LEFT -> {
                ActionButton(
                    icon = R.drawable.ic_edit,
                    description = "Edit",
                    onClick = { onClose(); onEdit() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                ActionButton(
                    icon = R.drawable.ic_copy,
                    description = "Copy",
                    onClick = { onClose(); onCopy() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                ActionButton(
                    icon = R.drawable.ic_delete,
                    description = "Delete",
                    onClick = { onClose(); onDelete() }
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            SWIPE_FULL_LEFT -> {
                ActionButton(
                    icon = R.drawable.ic_delete,
                    description = "Delete",
                    onClick = { onDelete() },
                    isSwipeFullLeft = true
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            SWIPE_FULL_RIGHT, SWIPE_NONE -> {
            }
        }
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
private fun ActionButton(
    icon: Int,
    description: String,
    onClick: () -> Unit,
    isSwipeFullLeft: Boolean = false
) {
    var colorBackgroung = MaterialTheme.colorScheme.onSecondary
    var colorTInt = MaterialTheme.colorScheme.secondary

    if (isSwipeFullLeft) {
        colorBackgroung = MaterialTheme.colorScheme.secondary
        colorTInt = MaterialTheme.colorScheme.surfaceVariant
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                color = colorBackgroung,
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
            tint = colorTInt
        )
    }
}

private fun getFinalState(config: SwipeConfig, fullSwipeThresholdPx: Float): String {
    return when {
        config.offsetX.value <= -fullSwipeThresholdPx -> SWIPE_FULL_LEFT
        config.offsetX.value >= fullSwipeThresholdPx -> SWIPE_FULL_RIGHT
        config.offsetX.value < 0 -> SWIPE_SHORT_LEFT
        else -> SWIPE_NONE
    }
}

data class SwipeConfig(
    val offsetX: Animatable<Float, *>,
    val density: Density,
    val onSwipeState: (String) -> Unit,
    val scope: CoroutineScope,
    val onSwipeLeft: () -> Unit,
    val onSwipeRight: () -> Unit,
    val fullWidthPx: Int
)

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
