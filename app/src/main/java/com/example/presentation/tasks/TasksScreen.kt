package com.example.presentation.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.NeoInteractiveBox
import com.example.core.ui.neoBorder
import com.example.core.ui.neoCard
import com.example.core.ui.neoShadow
import com.example.domain.model.Priority
import com.example.domain.model.Task
import com.example.presentation.components.TopHeaderBar
import com.example.ui.theme.BoneCanvas
import com.example.ui.theme.CyanContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.HeatCoral
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.HyperCyanDark
import com.example.ui.theme.InkBlack
import com.example.ui.theme.NewsprintGray
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextVariant
import com.example.ui.theme.VoltYellow
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TasksScreen(
    state: TasksUiState,
    onEvent: (TasksEvent) -> Unit,
    onOpenStandby: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTagFilter by remember { mutableStateOf<String?>(null) }
    var isCompletedExpanded by remember { mutableStateOf(true) }

    val baseTasks = state.filteredTasks
    val displayedTasks = if (selectedTagFilter == null) {
        baseTasks
    } else {
        baseTasks.filter { it.tag.equals(selectedTagFilter, ignoreCase = true) }
    }

    val activeTasks = displayedTasks.filter { !it.completed }
    val completedTasks = displayedTasks.filter { it.completed }

    // Divide active tasks into TODAY and UPCOMING
    val todayTasks = if (activeTasks.size > 3) activeTasks.take(4) else activeTasks
    val upcomingTasks = if (activeTasks.size > 3) activeTasks.drop(4) else emptyList()

    val todayTotalEstMinutes = todayTasks.sumOf { it.estimatedPomodoros * 25 }
    val todayEstHours = String.format(Locale.US, "%.1f", todayTotalEstMinutes / 60.0)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BoneCanvas)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp)
        ) {
            // 1. Top Bar
            TopHeaderBar(
                onStandbyClick = onOpenStandby
            )

            // 2. Title Header with Count Badge & Add Task Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "TASKS",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = InkBlack
                        )

                        // Cyan Pending Count Badge
                        Box(
                            modifier = Modifier
                                .neoShadow(offsetX = 2.dp, offsetY = 2.dp, color = Color.Black, cornerRadius = 3.dp)
                                .background(CyanContainer, RoundedCornerShape(3.dp))
                                .neoBorder(width = 1.5.dp, color = Color.Black, shape = RoundedCornerShape(3.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "[${state.activeCount} PENDING]",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = HyperCyanDark
                            )
                        }
                    }

                    // + ADD TASK Button
                    NeoInteractiveBox(
                        onClick = { onEvent(TasksEvent.OpenAddTask) },
                        backgroundColor = VoltYellow,
                        shadowOffset = 2.5.dp,
                        borderWidth = 2.dp,
                        cornerRadius = 4.dp,
                        modifier = Modifier.testTag("button_add_task")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Task",
                                tint = InkBlack,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "ADD TASK",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = InkBlack
                            )
                        }
                    }
                }

                // Horizontal Filter Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val allCount = state.tasks.size
                    val workCount = state.tasks.count { it.tag.equals("WORK", ignoreCase = true) }
                    val personalCount = state.tasks.count { it.tag.equals("PERSONAL", ignoreCase = true) }

                    val categoryChips = listOf(
                        null to "ALL [$allCount]",
                        "WORK" to "WORK [$workCount]",
                        "PERSONAL" to "PERSONAL [$personalCount]",
                        "SPRINT" to "SPRINT",
                        "DESIGN" to "DESIGN",
                        "BACKEND" to "BACKEND",
                        "BUG" to "BUG",
                        "PERF" to "PERF"
                    )

                    categoryChips.forEach { (tagValue, label) ->
                        val isSelected = selectedTagFilter == tagValue
                        NeoInteractiveBox(
                            onClick = { selectedTagFilter = if (isSelected) null else tagValue },
                            backgroundColor = if (isSelected) InkBlack else PureWhite,
                            shadowOffset = 2.dp,
                            borderWidth = 1.5.dp,
                            cornerRadius = 4.dp,
                            modifier = Modifier.testTag("filter_chip_${tagValue ?: "all"}")
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) PureWhite else InkBlack
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Main Tasks Content Scrollable List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (displayedTasks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "NO TASKS FOUND // QUEUE CLEAR",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextMuted
                            )
                        }
                    }
                } else {
                    // TODAY Section Header
                    if (todayTasks.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp, bottom = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "TODAY",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = InkBlack
                                    )

                                    Box(
                                        modifier = Modifier
                                            .background(VoltYellow, RoundedCornerShape(3.dp))
                                            .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(3.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "[${todayTasks.size}]",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = InkBlack
                                        )
                                    }
                                }

                                Text(
                                    text = "EST: ${todayEstHours}H",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = TextMuted
                                )
                            }
                        }

                        // TODAY Task Cards
                        items(todayTasks, key = { it.id }) { task ->
                            TaskCardItem(
                                task = task,
                                onToggleComplete = { onEvent(TasksEvent.ToggleTaskComplete(task.id)) },
                                onSelectForTimer = { onEvent(TasksEvent.SelectTaskForTimer(task.id)) },
                                onEdit = { onEvent(TasksEvent.OpenEditTask(task)) },
                                onDelete = { onEvent(TasksEvent.DeleteTask(task.id)) }
                            )
                        }
                    }

                    // UPCOMING Section Header & Cards
                    if (upcomingTasks.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp, bottom = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "UPCOMING",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = InkBlack
                                    )

                                    Box(
                                        modifier = Modifier
                                            .background(SurfaceContainerHigh, RoundedCornerShape(3.dp))
                                            .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(3.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "[${upcomingTasks.size}]",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = InkBlack
                                        )
                                    }
                                }

                                Text(
                                    text = "TOMORROW",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = TextMuted
                                )
                            }
                        }

                        items(upcomingTasks, key = { it.id }) { task ->
                            TaskCardItem(
                                task = task,
                                onToggleComplete = { onEvent(TasksEvent.ToggleTaskComplete(task.id)) },
                                onSelectForTimer = { onEvent(TasksEvent.SelectTaskForTimer(task.id)) },
                                onEdit = { onEvent(TasksEvent.OpenEditTask(task)) },
                                onDelete = { onEvent(TasksEvent.DeleteTask(task.id)) }
                            )
                        }
                    }

                    // COMPLETED Section Header & Cards
                    if (completedTasks.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isCompletedExpanded = !isCompletedExpanded }
                                    .padding(top = 14.dp, bottom = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(HeatCoral),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = PureWhite,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }

                                    Text(
                                        text = "[COMPLETED (${completedTasks.size})]",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        ),
                                        color = InkBlack
                                    )
                                }

                                Icon(
                                    imageVector = if (isCompletedExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Toggle completed",
                                    tint = InkBlack,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        if (isCompletedExpanded) {
                            items(completedTasks, key = { "completed_${it.id}" }) { task ->
                                CompletedTaskCardItem(
                                    task = task,
                                    onToggleComplete = { onEvent(TasksEvent.ToggleTaskComplete(task.id)) },
                                    onDelete = { onEvent(TasksEvent.DeleteTask(task.id)) }
                                )
                            }
                        }
                    }

                    // Helper Banner at bottom
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .neoCard(
                                    backgroundColor = PureWhite,
                                    borderWidth = 1.5.dp,
                                    shadowOffset = 2.dp,
                                    cornerRadius = 4.dp
                                )
                                .padding(vertical = 8.dp, horizontal = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = HeatCoral,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "TAP CHECKBOX TO COMPLETE // TAP CARD TO SELECT AS TARGET",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = InkBlack
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add / Edit Task Modal Dialog
        if (state.isAddDialogOpen) {
            AddEditTaskDialog(
                state = state,
                onEvent = onEvent
            )
        }
    }
}

@Composable
fun TaskCardItem(
    task: Task,
    onToggleComplete: () -> Unit,
    onSelectForTimer: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val tagBgColor = when (task.tag.uppercase()) {
        "SPRINT" -> HeatCoral
        "DESIGN" -> HyperCyan
        "BUG" -> Color(0xFFFFC0CB)
        "BACKEND" -> VoltYellow
        "PERF" -> CyanContainer
        "WORK" -> NewsprintGray
        "PERSONAL" -> Color(0xFFE8DAEF)
        else -> SurfaceContainerHigh
    }
    val tagTextColor = when (task.tag.uppercase()) {
        "SPRINT" -> PureWhite
        "DESIGN" -> InkBlack
        "BUG" -> InkBlack
        "BACKEND" -> InkBlack
        "PERF" -> HyperCyanDark
        "WORK" -> InkBlack
        "PERSONAL" -> InkBlack
        else -> InkBlack
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .neoCard(
                backgroundColor = PureWhite,
                borderWidth = 2.dp,
                shadowOffset = 3.dp,
                cornerRadius = 4.dp
            )
            .clickable { onSelectForTimer() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("task_item_${task.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Side: Handle/Checkbox + Title + Badges
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NeoInteractiveBox(
                    onClick = onToggleComplete,
                    backgroundColor = if (task.completed) VoltYellow else PureWhite,
                    shadowOffset = 2.dp,
                    borderWidth = 1.5.dp,
                    cornerRadius = 3.dp,
                    modifier = Modifier
                        .size(24.dp)
                        .testTag("task_checkbox_${task.id}")
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (task.completed) {
                            Icon(Icons.Default.Check, contentDescription = "Completed", tint = InkBlack, modifier = Modifier.size(16.dp))
                        } else {
                            Text("└", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = InkBlack)
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        color = if (task.completed) TextMuted else InkBlack,
                        maxLines = 2
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(tagBgColor, RoundedCornerShape(3.dp))
                                .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(3.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "[${task.tag}]",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                color = tagTextColor
                            )
                        }

                        val metaText = when (task.priority) {
                            Priority.CRITICAL -> "⚙️ CRITICAL"
                            Priority.HIGH -> "🌐 HIGH PRIO"
                            Priority.MEDIUM -> "🔍 ${task.estimatedPomodoros * 25}M"
                            Priority.LOW -> "🔄 SYNC"
                        }
                        Text(
                            text = metaText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = TextVariant
                        )

                        if (task.isSelected) {
                            Box(
                                modifier = Modifier
                                    .background(VoltYellow, RoundedCornerShape(3.dp))
                                    .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(3.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "TARGET",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    ),
                                    color = InkBlack
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Side: POMO Label + Session Square Blocks
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "POMO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp
                    ),
                    color = TextMuted
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val total = task.estimatedPomodoros.coerceAtLeast(1)
                    repeat(total) { index ->
                        val isDone = index < task.completedPomodoros
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    if (isDone) HeatCoral else PureWhite,
                                    RoundedCornerShape(1.dp)
                                )
                                .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(1.dp))
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    NeoInteractiveBox(
                        onClick = onEdit,
                        backgroundColor = SurfaceContainerHigh,
                        shadowOffset = 1.dp,
                        borderWidth = 1.dp,
                        cornerRadius = 2.dp
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = InkBlack,
                            modifier = Modifier.padding(2.dp).size(12.dp)
                        )
                    }
                    NeoInteractiveBox(
                        onClick = onDelete,
                        backgroundColor = PureWhite,
                        shadowOffset = 1.dp,
                        borderWidth = 1.dp,
                        cornerRadius = 2.dp
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = ErrorRed,
                            modifier = Modifier.padding(2.dp).size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompletedTaskCardItem(
    task: Task,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .neoCard(
                backgroundColor = PureWhite,
                borderWidth = 2.dp,
                shadowOffset = 2.dp,
                cornerRadius = 4.dp
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                NeoInteractiveBox(
                    onClick = onToggleComplete,
                    backgroundColor = VoltYellow,
                    shadowOffset = 1.5.dp,
                    borderWidth = 1.5.dp,
                    cornerRadius = 3.dp,
                    modifier = Modifier.size(22.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Uncomplete",
                            tint = InkBlack,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        textDecoration = TextDecoration.LineThrough
                    ),
                    color = TextMuted,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(SurfaceContainerHigh, RoundedCornerShape(3.dp))
                        .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(3.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "DONE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = TextVariant
                    )
                }

                NeoInteractiveBox(
                    onClick = onDelete,
                    backgroundColor = PureWhite,
                    shadowOffset = 1.dp,
                    borderWidth = 1.dp,
                    cornerRadius = 2.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = ErrorRed,
                        modifier = Modifier.padding(2.dp).size(12.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditTaskDialog(
    state: TasksUiState,
    onEvent: (TasksEvent) -> Unit
) {
    val isEditing = state.editingTask != null

    AlertDialog(
        onDismissRequest = { onEvent(TasksEvent.CloseDialog) },
        title = {
            Text(
                text = if (isEditing) "EDIT TASK // MODIFY" else "NEW TASK // CREATE",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = InkBlack
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Title Field
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "TITLE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextVariant
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PureWhite, RoundedCornerShape(4.dp))
                            .neoBorder(width = 1.5.dp, color = Color.Black, shape = RoundedCornerShape(4.dp))
                            .padding(10.dp)
                    ) {
                        BasicTextField(
                            value = state.newTaskTitle,
                            onValueChange = { onEvent(TasksEvent.UpdateTitle(it)) },
                            textStyle = TextStyle(
                                color = InkBlack,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            cursorBrush = SolidColor(InkBlack),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_task_title")
                        )
                        if (state.newTaskTitle.isEmpty()) {
                            Text(
                                text = "e.g. Implement refresh token handler",
                                style = TextStyle(color = TextMuted, fontSize = 14.sp)
                            )
                        }
                    }
                }

                // Notes Field
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "NOTES",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextVariant
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PureWhite, RoundedCornerShape(4.dp))
                            .neoBorder(width = 1.5.dp, color = Color.Black, shape = RoundedCornerShape(4.dp))
                            .padding(10.dp)
                    ) {
                        BasicTextField(
                            value = state.newTaskNotes,
                            onValueChange = { onEvent(TasksEvent.UpdateNotes(it)) },
                            textStyle = TextStyle(
                                color = InkBlack,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            cursorBrush = SolidColor(InkBlack),
                            maxLines = 3,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_task_notes")
                        )
                        if (state.newTaskNotes.isEmpty()) {
                            Text(
                                text = "Additional context or acceptance criteria",
                                style = TextStyle(color = TextMuted, fontSize = 13.sp)
                            )
                        }
                    }
                }

                // Priority Selection
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "PRIORITY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextVariant
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Priority.entries.forEach { p ->
                            val isSelected = state.newTaskPriority == p
                            NeoInteractiveBox(
                                onClick = { onEvent(TasksEvent.UpdatePriority(p)) },
                                backgroundColor = if (isSelected) VoltYellow else PureWhite,
                                shadowOffset = 2.dp,
                                borderWidth = 1.5.dp,
                                cornerRadius = 3.dp
                            ) {
                                Text(
                                    text = p.displayName,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = InkBlack
                                )
                            }
                        }
                    }
                }

                // Tag Selection
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "TAG",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextVariant
                    )
                    val tags = listOf("SPRINT", "DESIGN", "BUG", "BACKEND", "PERF", "WORK")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        tags.forEach { tag ->
                            val isSelected = state.newTaskTag == tag
                            NeoInteractiveBox(
                                onClick = { onEvent(TasksEvent.UpdateTag(tag)) },
                                backgroundColor = if (isSelected) CyanContainer else PureWhite,
                                shadowOffset = 2.dp,
                                borderWidth = 1.5.dp,
                                cornerRadius = 3.dp
                            ) {
                                Text(
                                    text = tag,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) HyperCyanDark else InkBlack
                                )
                            }
                        }
                    }
                }

                // Estimated Pomodoros Counter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ESTIMATED CYCLES",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextVariant
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NeoInteractiveBox(
                            onClick = { onEvent(TasksEvent.UpdateEstimated(-1)) },
                            backgroundColor = SurfaceContainerHigh,
                            shadowOffset = 2.dp,
                            borderWidth = 1.5.dp,
                            cornerRadius = 3.dp,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrement", tint = InkBlack, modifier = Modifier.size(16.dp))
                            }
                        }

                        Text(
                            text = "${state.newTaskEstimated} 🍅",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = InkBlack
                        )

                        NeoInteractiveBox(
                            onClick = { onEvent(TasksEvent.UpdateEstimated(1)) },
                            backgroundColor = SurfaceContainerHigh,
                            shadowOffset = 2.dp,
                            borderWidth = 1.5.dp,
                            cornerRadius = 3.dp,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Add, contentDescription = "Increment", tint = InkBlack, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            NeoInteractiveBox(
                onClick = { onEvent(TasksEvent.SaveTask) },
                backgroundColor = VoltYellow,
                shadowOffset = 2.dp,
                borderWidth = 2.dp,
                cornerRadius = 4.dp,
                modifier = Modifier.testTag("button_save_task")
            ) {
                Text(
                    text = if (isEditing) "UPDATE TASK" else "CREATE TASK",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = InkBlack
                )
            }
        },
        dismissButton = {
            NeoInteractiveBox(
                onClick = { onEvent(TasksEvent.CloseDialog) },
                backgroundColor = PureWhite,
                shadowOffset = 2.dp,
                borderWidth = 1.5.dp,
                cornerRadius = 4.dp
            ) {
                Text(
                    text = "CANCEL",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = InkBlack
                )
            }
        },
        containerColor = PureWhite,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .neoBorder(width = 2.5.dp, color = Color.Black, shape = RoundedCornerShape(6.dp))
            .neoShadow(offsetX = 4.dp, offsetY = 4.dp, color = Color.Black, cornerRadius = 6.dp)
    )
}
