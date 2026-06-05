package ru.practicum.shoppinglist.ui.main

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.ui.theme.ShoppingListTheme
import kotlin.math.roundToInt

@Composable
fun CardList(
    iconCard: Int,
    textCard: Int,
    onIconClick: () -> Unit,
    onEdit: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    var isActionsVisible by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    var actionButtonsWidthPx by remember { mutableStateOf(0f) }
    val ACTION_BUTTONS_WIDTH = 168.dp

    ShoppingListTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .onGloballyPositioned { layoutCoordinates ->
                            actionButtonsWidthPx = layoutCoordinates.size.width.toFloat()
                        },
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                            ) {
                                scope.launch {
                                    offsetX.animateTo(0f, animationSpec = tween(300))
                                    isActionsVisible = false
                                }
                                onEdit()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = "Edit",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

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
                            ) {
                                scope.launch {
                                    offsetX.animateTo(0f, animationSpec = tween(300))
                                    isActionsVisible = false
                                }
                                onCopy()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_copy),
                            contentDescription = "Copy",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Иконка Delete
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
                            ) {
                                scope.launch {
                                    offsetX.animateTo(0f, animationSpec = tween(300))
                                    isActionsVisible = false
                                }
                                onDelete()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = "Delete",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragStart = {
                                    scope.launch {
                                        offsetX.stop()
                                    }
                                },
                                onDragEnd = {
                                    scope.launch {
                                        val maxOffset = -with(density) { ACTION_BUTTONS_WIDTH.toPx() }
                                        if (offsetX.value < -100f) {
                                            offsetX.animateTo(maxOffset, animationSpec = tween(300))
                                            isActionsVisible = true
                                        } else {
                                            offsetX.animateTo(0f, animationSpec = tween(300))
                                            isActionsVisible = false
                                        }
                                    }
                                },
                                onDragCancel = {
                                    scope.launch {
                                        offsetX.animateTo(0f, animationSpec = tween(300))
                                        isActionsVisible = false
                                    }
                                },
                                onHorizontalDrag = { _, dragAmount ->
                                    scope.launch {
                                        val maxOffset = -with(density) { ACTION_BUTTONS_WIDTH.toPx() }
                                        val newOffset = offsetX.value + dragAmount
                                        val clampedOffset = newOffset.coerceIn(maxOffset, 0f)
                                        offsetX.snapTo(clampedOffset)
                                    }
                                }
                            )
                        }
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(12.dp),
                            clip = false,
                            ambientColor = Color.Black.copy(alpha = 0.3f),
                            spotColor = Color.Black.copy(alpha = 0.4f)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(vertical = 8.dp),
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
                                painter = painterResource(iconCard),
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
                            text = stringResource(textCard),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .padding(vertical = 8.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "CardList - Recipes", showBackground = true)
@Composable
private fun PreviewCardListRecipes() {
    ShoppingListTheme {
        CardList(
            iconCard = R.drawable.ic_set_cake,
            textCard = R.string.main_lists,
            onIconClick = {},
            onEdit = {},
            onCopy = {},
            onDelete = {}
        )
    }
}
