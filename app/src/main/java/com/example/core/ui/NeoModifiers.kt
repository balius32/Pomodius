package com.example.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

fun Modifier.neoShadow(
    offsetX: Dp = 3.dp,
    offsetY: Dp = 3.dp,
    color: Color = Color(0xFF000000),
    cornerRadius: Dp = 4.dp
): Modifier = this.drawBehind {
    val cornerPx = cornerRadius.toPx()
    val shadowOffsetPxX = offsetX.toPx()
    val shadowOffsetPxY = offsetY.toPx()
    drawRoundRect(
        color = color,
        topLeft = Offset(shadowOffsetPxX, shadowOffsetPxY),
        size = size,
        cornerRadius = CornerRadius(cornerPx, cornerPx)
    )
}

fun Modifier.neoBorder(
    width: Dp = 2.5.dp,
    color: Color = Color(0xFF000000),
    shape: Shape = RoundedCornerShape(4.dp)
): Modifier = this.border(width = width, color = color, shape = shape)

fun Modifier.neoCard(
    backgroundColor: Color,
    borderWidth: Dp = 2.5.dp,
    shadowOffset: Dp = 3.dp,
    cornerRadius: Dp = 4.dp,
    borderColor: Color = Color(0xFF000000)
): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    return this
        .neoShadow(offsetX = shadowOffset, offsetY = shadowOffset, color = borderColor, cornerRadius = cornerRadius)
        .background(color = backgroundColor, shape = shape)
        .neoBorder(width = borderWidth, color = borderColor, shape = shape)
        .clip(shape)
}

@Composable
fun NeoInteractiveBox(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    shadowOffset: Dp = 3.dp,
    borderWidth: Dp = 2.5.dp,
    cornerRadius: Dp = 4.dp,
    borderColor: Color = Color(0xFF000000),
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val shape = RoundedCornerShape(cornerRadius)

    val currentOffset by animateFloatAsState(
        targetValue = if (isPressed) shadowOffset.value else 0f,
        label = "pressOffset"
    )

    val currentShadow by animateFloatAsState(
        targetValue = if (isPressed) 0f else shadowOffset.value,
        label = "pressShadow"
    )

    Box(
        modifier = modifier
            .neoShadow(
                offsetX = currentShadow.dp,
                offsetY = currentShadow.dp,
                color = borderColor,
                cornerRadius = cornerRadius
            )
            .offset { IntOffset(currentOffset.dp.roundToPx(), currentOffset.dp.roundToPx()) }
            .background(color = backgroundColor, shape = shape)
            .neoBorder(width = borderWidth, color = borderColor, shape = shape)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
    ) {
        content()
    }
}
