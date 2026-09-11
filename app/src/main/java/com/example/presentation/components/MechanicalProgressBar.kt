package com.example.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.core.ui.neoBorder
import com.example.core.ui.neoShadow
import com.example.domain.model.PomodoroType
import com.example.ui.theme.HeatCoral
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.VoltYellow

@Composable
fun MechanicalProgressBar(
    currentCycle: Int,
    maxCycles: Int,
    currentType: PomodoroType,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val totalSegments = (maxCycles.coerceAtLeast(1)) * 2

    // Number of completed segments (capsules filled)
    val completedSegmentsCount = when (currentType) {
        PomodoroType.FOCUS -> ((currentCycle - 1).coerceAtLeast(0) * 2)
        PomodoroType.SHORT_BREAK -> ((currentCycle - 1).coerceAtLeast(0) * 2) + 1
        PomodoroType.LONG_BREAK -> totalSegments // All capsules filled during long break
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (i in 0 until totalSegments) {
            val isFocusSegment = (i % 2 == 0)
            val segmentColor = if (isFocusSegment) HeatCoral else VoltYellow
            val isCompleted = i < completedSegmentsCount
            val isActive = i == completedSegmentsCount && currentType != PomodoroType.LONG_BREAK

            val fillColor = if (isCompleted) segmentColor else SurfaceContainerHigh
            val borderThickness = if (isCompleted || isActive) 1.5.dp else 1.dp
            val borderAlpha = if (isCompleted || isActive) 1f else 0.4f

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(14.dp)
                    .neoShadow(
                        offsetX = if (isCompleted || isActive) 1.dp else 0.5.dp,
                        offsetY = if (isCompleted || isActive) 1.dp else 0.5.dp,
                        color = Color.Black.copy(alpha = if (isCompleted || isActive) 1f else 0.25f),
                        cornerRadius = 2.dp
                    )
                    .background(fillColor, RoundedCornerShape(2.dp))
                    .neoBorder(
                        width = borderThickness,
                        color = Color.Black.copy(alpha = borderAlpha),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

@Composable
fun MechanicalProgressBar(
    progress: Float,
    isBreak: Boolean = false,
    modifier: Modifier = Modifier
) {
    MechanicalProgressBar(
        currentCycle = 1,
        maxCycles = 4,
        currentType = if (isBreak) PomodoroType.SHORT_BREAK else PomodoroType.FOCUS,
        progress = progress,
        modifier = modifier
    )
}

