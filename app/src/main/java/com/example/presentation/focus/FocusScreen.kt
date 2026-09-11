package com.example.presentation.focus

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.NeoInteractiveBox
import com.example.core.ui.neoBorder
import com.example.core.ui.neoCard
import com.example.core.ui.neoShadow
import com.example.domain.model.PomodoroType
import com.example.domain.model.TimerState
import com.example.presentation.components.MechanicalProgressBar
import com.example.presentation.components.SegmentedModeSwitcher
import com.example.presentation.components.TopHeaderBar
import com.example.ui.theme.BoneCanvas
import com.example.ui.theme.CyanContainer
import com.example.ui.theme.HeatCoral
import com.example.ui.theme.HyperCyanDark
import com.example.ui.theme.InkBlack
import com.example.ui.theme.NewsprintGray
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextVariant
import com.example.ui.theme.VoltYellow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FocusScreen(
    state: FocusUiState,
    onEvent: (FocusEvent) -> Unit,
    onNavigateToTasks: () -> Unit,
    onOpenStandby: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val formattedToday = SimpleDateFormat("EEEE, MMM d", Locale.US).format(Date()).uppercase()

    // Blinking animation for LIVE dot
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BoneCanvas)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 100.dp)
        ) {
            // 1. Top Header Bar
            TopHeaderBar(
                onStandbyClick = onOpenStandby
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 2. Segmented Mode Switcher (FOCUS / SHORT / LONG)
                SegmentedModeSwitcher(
                    currentType = state.currentType,
                    onSelectMode = { onEvent(FocusEvent.SelectMode(it)) }
                )

                // 4. Main Focus Timer Console Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neoCard(
                            backgroundColor = PureWhite,
                            borderWidth = 2.5.dp,
                            shadowOffset = 4.dp,
                            cornerRadius = 6.dp
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Top status strip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (state.timerState == TimerState.RUNNING) {
                                                HeatCoral.copy(alpha = pulseAlpha)
                                            } else {
                                                TextMuted
                                            }
                                        )
                                )
                                Text(
                                    text = if (state.timerState == TimerState.RUNNING) "REC // LIVE" else "READY // IDLE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = InkBlack
                                )
                            }

                            // Phase badge
                            Box(
                                modifier = Modifier
                                    .neoShadow(offsetX = 1.5.dp, offsetY = 1.5.dp, color = Color.Black, cornerRadius = 3.dp)
                                    .background(VoltYellow, RoundedCornerShape(3.dp))
                                    .neoBorder(width = 1.5.dp, color = Color.Black, shape = RoundedCornerShape(3.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = state.phaseLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = InkBlack
                                )
                            }
                        }

                        // Segmented Mechanical Progress Meter (Focus + Short Break Session Pairs)
                        MechanicalProgressBar(
                            currentCycle = state.currentCycle,
                            maxCycles = state.maxCycles,
                            currentType = state.currentType,
                            progress = state.progress
                        )

                        // Big Tabular Clock Box with Corner Cross Ticks
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceContainerHigh, RoundedCornerShape(4.dp))
                                .neoBorder(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(4.dp))
                                .padding(vertical = 20.dp, horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // 4 Corner Ticks
                            Text(
                                text = "+",
                                modifier = Modifier.align(Alignment.TopStart).padding(4.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = TextMuted
                            )
                            Text(
                                text = "+",
                                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = TextMuted
                            )
                            Text(
                                text = "+",
                                modifier = Modifier.align(Alignment.BottomStart).padding(4.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = TextMuted
                            )
                            Text(
                                text = "+",
                                modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = TextMuted
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = state.formattedTime,
                                    style = MaterialTheme.typography.displayLarge.copy(
                                        fontSize = 62.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-2).sp
                                    ),
                                    color = InkBlack,
                                    modifier = Modifier.testTag("timer_display")
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (state.currentType == PomodoroType.FOCUS) HeatCoral else VoltYellow)
                                    )
                                    Text(
                                        text = state.badgeLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            letterSpacing = 1.sp
                                        ),
                                        color = TextVariant
                                    )
                                }
                            }
                        }

                        // Tactile Big-Button Console
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Restart Button
                            NeoInteractiveBox(
                                onClick = { onEvent(FocusEvent.ResetTimer) },
                                backgroundColor = PureWhite,
                                shadowOffset = 2.dp,
                                borderWidth = 2.dp,
                                cornerRadius = 4.dp,
                                modifier = Modifier
                                    .size(54.dp)
                                    .testTag("button_restart")
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Replay,
                                        contentDescription = "Restart Timer",
                                        tint = InkBlack,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            // Primary Action Button (Start / Pause / Resume)
                            val isRunning = state.timerState == TimerState.RUNNING
                            val actionBgColor = if (isRunning) VoltYellow else HeatCoral
                            val actionTextColor = if (isRunning) InkBlack else PureWhite
                            val actionText = when (state.timerState) {
                                TimerState.IDLE -> "START SESSION"
                                TimerState.RUNNING -> "PAUSE SESSION"
                                TimerState.PAUSED -> "RESUME SESSION"
                                TimerState.COMPLETED -> "RESTART"
                            }
                            val actionIcon = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow

                            NeoInteractiveBox(
                                onClick = { onEvent(FocusEvent.ToggleTimer) },
                                backgroundColor = actionBgColor,
                                shadowOffset = 2.dp,
                                borderWidth = 2.dp,
                                cornerRadius = 4.dp,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(54.dp)
                                    .testTag("button_timer_toggle")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = actionIcon,
                                        contentDescription = actionText,
                                        tint = actionTextColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = actionText,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = actionTextColor
                                    )
                                }
                            }

                            // Skip Button
                            NeoInteractiveBox(
                                onClick = { onEvent(FocusEvent.SkipSession) },
                                backgroundColor = PureWhite,
                                shadowOffset = 2.dp,
                                borderWidth = 2.dp,
                                cornerRadius = 4.dp,
                                modifier = Modifier
                                    .size(54.dp)
                                    .testTag("button_skip")
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.SkipNext,
                                        contentDescription = "Skip Session",
                                        tint = InkBlack,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 5. Active Task Linked Card
                val task = state.activeTask
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neoCard(
                            backgroundColor = PureWhite,
                            borderWidth = 2.5.dp,
                            shadowOffset = 3.dp,
                            cornerRadius = 6.dp
                        )
                        .clickable { onEvent(FocusEvent.OpenTaskPicker) }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "CURRENT TARGET",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = TextVariant
                                )
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (task?.priority?.name == "CRITICAL") HeatCoral else VoltYellow,
                                            RoundedCornerShape(3.dp)
                                        )
                                        .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(3.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "PRIORITY: ${task?.priority?.displayName ?: "HIGH"}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        color = if (task?.priority?.name == "CRITICAL") PureWhite else InkBlack
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = task?.title ?: "Select a task target...",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = InkBlack
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = if (task != null) "${task.tag} • SESSION ${task.completedPomodoros} OF ${task.estimatedPomodoros}" else "TAP TO SELECT FOCUS TASK",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = TextMuted
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            NeoInteractiveBox(
                                onClick = { onEvent(FocusEvent.OpenTaskPicker) },
                                backgroundColor = VoltYellow,
                                shadowOffset = 2.dp,
                                borderWidth = 1.5.dp,
                                cornerRadius = 4.dp,
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("button_switch_task")
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.SwapHoriz,
                                        contentDescription = "Switch Task",
                                        tint = InkBlack,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 6. Daily Telemetry Card (Matching Image)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neoCard(
                            backgroundColor = PureWhite,
                            borderWidth = 2.dp,
                            shadowOffset = 3.dp,
                            cornerRadius = 4.dp
                        )
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Red vertical accent rectangle
                            Box(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(28.dp)
                                    .background(HeatCoral, RoundedCornerShape(2.dp))
                                    .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(2.dp))
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "DAILY TELEMETRY",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        letterSpacing = 1.sp
                                    ),
                                    color = TextVariant
                                )

                                Text(
                                    text = "${state.todayCompletedCycles} COMPLETED • ${state.formattedTodayFocusTime}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.Monospace,
                                        letterSpacing = (-0.2).sp
                                    ),
                                    color = InkBlack
                                )
                            }
                        }

                        // Yellow "ON TRACK" Badge
                        Box(
                            modifier = Modifier
                                .neoShadow(offsetX = 1.5.dp, offsetY = 1.5.dp, color = Color.Black, cornerRadius = 3.dp)
                                .background(VoltYellow, RoundedCornerShape(3.dp))
                                .neoBorder(width = 1.5.dp, color = Color.Black, shape = RoundedCornerShape(3.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "ON TRACK",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = InkBlack
                            )
                        }
                    }
                }
            }
        }

        // Session Completion Celebration Dialog
        if (state.showCelebrationDialog) {
            AlertDialog(
                onDismissRequest = { onEvent(FocusEvent.DismissCelebration) },
                confirmButton = {
                    NeoInteractiveBox(
                        onClick = { onEvent(FocusEvent.DismissCelebration) },
                        backgroundColor = VoltYellow,
                        shadowOffset = 3.dp,
                        borderWidth = 2.dp,
                        cornerRadius = 4.dp,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "PROCEED TO NEXT CYCLE",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = InkBlack
                        )
                    }
                },
                title = {
                    Text(
                        text = "CYCLE FINISHED // EXCELLENT",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = InkBlack
                    )
                },
                text = {
                    Text(
                        text = "You successfully locked in a deep focus cycle. Stats updated and task incremented. Take a deep breath or stretch before the next sprint!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextVariant
                    )
                },
                containerColor = PureWhite,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .neoBorder(width = 2.5.dp, color = Color.Black, shape = RoundedCornerShape(6.dp))
                    .neoShadow(offsetX = 5.dp, offsetY = 5.dp, color = Color.Black, cornerRadius = 6.dp)
            )
        }

        // Task Selection Dialog
        if (state.isTaskPickerOpen) {
            AlertDialog(
                onDismissRequest = { onEvent(FocusEvent.CloseTaskPicker) },
                confirmButton = {
                    NeoInteractiveBox(
                        onClick = {
                            onEvent(FocusEvent.CloseTaskPicker)
                            onNavigateToTasks()
                        },
                        backgroundColor = VoltYellow,
                        shadowOffset = 2.dp,
                        borderWidth = 1.5.dp,
                        cornerRadius = 4.dp
                    ) {
                        Text(
                            text = "+ MANAGE TASKS",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = InkBlack
                        )
                    }
                },
                dismissButton = {
                    NeoInteractiveBox(
                        onClick = { onEvent(FocusEvent.CloseTaskPicker) },
                        backgroundColor = SurfaceContainerHigh,
                        shadowOffset = 2.dp,
                        borderWidth = 1.5.dp,
                        cornerRadius = 4.dp
                    ) {
                        Text(
                            text = "CLOSE",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = InkBlack
                        )
                    }
                },
                title = {
                    Text(
                        text = "SELECT FOCUS TARGET",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = InkBlack
                    )
                },
                text = {
                    if (state.availableTasks.isEmpty()) {
                        Text(
                            text = "No active tasks found. Tap '+ MANAGE TASKS' to create your first target!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(260.dp)
                        ) {
                            items(state.availableTasks) { taskItem ->
                                val isSelected = taskItem.id == state.activeTask?.id
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .neoCard(
                                            backgroundColor = if (isSelected) SurfaceContainerHigh else PureWhite,
                                            borderWidth = if (isSelected) 2.dp else 1.dp,
                                            shadowOffset = if (isSelected) 2.dp else 1.dp,
                                            cornerRadius = 4.dp
                                        )
                                        .clickable { onEvent(FocusEvent.SelectTask(taskItem.id)) }
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = taskItem.title,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                ),
                                                color = InkBlack
                                            )
                                            Text(
                                                text = "${taskItem.tag} • SESSION ${taskItem.completedPomodoros} OF ${taskItem.estimatedPomodoros}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                color = TextMuted
                                            )
                                        }
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .background(VoltYellow, CircleShape)
                                                    .neoBorder(width = 1.dp, color = Color.Black, shape = CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = InkBlack,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                containerColor = PureWhite,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .neoBorder(width = 2.5.dp, color = Color.Black, shape = RoundedCornerShape(6.dp))
                    .neoShadow(offsetX = 5.dp, offsetY = 5.dp, color = Color.Black, cornerRadius = 6.dp)
            )
        }
    }
}
