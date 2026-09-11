package com.example.presentation.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Whatshot
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.NeoInteractiveBox
import com.example.core.ui.neoBorder
import com.example.core.ui.neoCard
import com.example.core.ui.neoShadow
import com.example.domain.model.DailyFocusMetric
import com.example.presentation.components.TopHeaderBar
import com.example.ui.theme.BoneCanvas
import com.example.ui.theme.CyanContainer
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

@Composable
fun InsightsScreen(
    state: InsightsUiState,
    onExportTelemetry: () -> Unit,
    onOpenStandby: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val data = state.data
    var selectedTimeframe by remember { mutableStateOf("WEEK") }

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
            // Top Bar
            TopHeaderBar(
                onStandbyClick = onOpenStandby
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Title Header Card with Timeframe Pills
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neoCard(
                            backgroundColor = PureWhite,
                            borderWidth = 2.dp,
                            shadowOffset = 3.dp,
                            cornerRadius = 4.dp
                        )
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(HeatCoral, RoundedCornerShape(2.dp))
                                    .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(2.dp))
                            )
                            Text(
                                text = "INSIGHTS // ANALYTICS",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = InkBlack
                            )
                        }

                        // Timeframe Tabs: WEEK / MONTH / YEAR
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceContainerHigh, RoundedCornerShape(3.dp))
                                .neoBorder(width = 1.5.dp, color = Color.Black, shape = RoundedCornerShape(3.dp))
                                .padding(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("WEEK", "MONTH", "YEAR").forEach { timeOption ->
                                val isSelected = selectedTimeframe == timeOption
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .then(
                                            if (isSelected) {
                                                Modifier
                                                    .background(VoltYellow, RoundedCornerShape(2.dp))
                                                    .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(2.dp))
                                            } else {
                                                Modifier.clickable { selectedTimeframe = timeOption }
                                            }
                                        )
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = timeOption,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            letterSpacing = 1.sp
                                        ),
                                        color = InkBlack
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. 2x2 Key Metric Cards Grid
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Row 1: FOCUS TODAY & SESSIONS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Card 1: FOCUS TODAY
                        val focusHours = data.todayFocusSeconds / 3600
                        val focusMins = (data.todayFocusSeconds % 3600) / 60
                        val focusTimeDisplay = if (focusHours > 0) "${focusHours}h ${focusMins}m" else "${focusMins}m"

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .neoCard(
                                    backgroundColor = PureWhite,
                                    borderWidth = 2.dp,
                                    shadowOffset = 3.dp,
                                    cornerRadius = 4.dp
                                )
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "FOCUS TODAY",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = TextVariant
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                Text(
                                    text = focusTimeDisplay,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
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
                                        text = "📈 LIVE METRICS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        color = InkBlack
                                    )
                                }
                            }
                        }

                        // Card 2: SESSIONS
                        val cycleVal = data.todayCompletedCycles
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .neoCard(
                                    backgroundColor = PureWhite,
                                    borderWidth = 2.dp,
                                    shadowOffset = 3.dp,
                                    cornerRadius = 4.dp
                                )
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "SESSIONS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = TextVariant
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = HeatCoral,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                Text(
                                    text = "$cycleVal CYCLES",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    ),
                                    color = InkBlack
                                )

                                Text(
                                    text = "GOAL PROGRESS $cycleVal/5 SPRINT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.5.sp
                                    ),
                                    color = TextVariant
                                )

                                // 5 segmented progress blocks
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    repeat(5) { index ->
                                        val isDone = index < cycleVal
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(6.dp)
                                                .background(
                                                    if (isDone) HeatCoral else SurfaceContainerHigh,
                                                    RoundedCornerShape(1.dp)
                                                )
                                                .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(1.dp))
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Row 2: FINISHED & STREAK
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Card 3: FINISHED
                        val finishedVal = data.finishedTasksCount
                        val highPrioVal = data.highPriorityFinishedCount

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .neoCard(
                                    backgroundColor = PureWhite,
                                    borderWidth = 2.dp,
                                    shadowOffset = 3.dp,
                                    cornerRadius = 4.dp
                                )
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "FINISHED",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = TextVariant
                                    )
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = HyperCyanDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                Text(
                                    text = "$finishedVal TASKS",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    ),
                                    color = InkBlack
                                )

                                Box(
                                    modifier = Modifier
                                        .background(CyanContainer, RoundedCornerShape(3.dp))
                                        .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(3.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "$highPrioVal HIGH PRIORITY",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        color = HyperCyanDark
                                    )
                                }
                            }
                        }

                        // Card 4: STREAK
                        val streakVal = data.currentStreakDays
                        val recordVal = data.recordStreakDays

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .neoCard(
                                    backgroundColor = PureWhite,
                                    borderWidth = 2.dp,
                                    shadowOffset = 3.dp,
                                    cornerRadius = 4.dp
                                )
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "STREAK",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = TextVariant
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Whatshot,
                                        contentDescription = null,
                                        tint = HeatCoral,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                Text(
                                    text = "$streakVal DAYS 🔥",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    ),
                                    color = InkBlack
                                )

                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFFFE4E1), RoundedCornerShape(3.dp))
                                        .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(3.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "RECORD: ${recordVal}D",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        color = HeatCoral
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. WEEKLY RHYTHM Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neoCard(
                            backgroundColor = PureWhite,
                            borderWidth = 2.dp,
                            shadowOffset = 3.dp,
                            cornerRadius = 4.dp
                        )
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Header
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
                                        .size(10.dp)
                                        .background(VoltYellow, RoundedCornerShape(2.dp))
                                        .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(2.dp))
                                )
                                Text(
                                    text = "WEEKLY RHYTHM",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = InkBlack
                                )
                            }
                        }

                        Text(
                            text = "BENCHMARK // 4.0H DAILY TARGET",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = TextMuted
                        )

                        // Hours Progress
                        val totalWeeklyHours = data.weeklyFocusHours
                        Box(
                            modifier = Modifier
                                .background(PureWhite, RoundedCornerShape(3.dp))
                                .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(3.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = String.format(Locale.US, "%.2fH / %.1fH", totalWeeklyHours, data.weeklyTargetHours),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = InkBlack
                            )
                        }

                        // Bar Chart with Red Dashed Benchmark Line
                        WeeklyRhythmBarChart(metrics = data.weeklyMetrics)

                        // Bottom Status Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceContainerHigh, RoundedCornerShape(3.dp))
                                .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(3.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = String.format(Locale.US, "⚡ Weekly Total: %.2fh", totalWeeklyHours),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = InkBlack
                            )

                            val trackPct = ((totalWeeklyHours / data.weeklyTargetHours) * 100).toInt().coerceIn(0, 100)
                            Box(
                                modifier = Modifier
                                    .background(VoltYellow, RoundedCornerShape(3.dp))
                                    .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(3.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ON TRACK ($trackPct%)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.5.sp
                                    ),
                                    color = InkBlack
                                )
                            }
                        }
                    }
                }

                // 4. FOCUS DISTRIBUTION Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neoCard(
                            backgroundColor = PureWhite,
                            borderWidth = 2.dp,
                            shadowOffset = 3.dp,
                            cornerRadius = 4.dp
                        )
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "FOCUS DISTRIBUTION",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = InkBlack
                            )

                            Text(
                                text = "100% AUDIT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = TextMuted
                            )
                        }

                        // Segmented bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(18.dp)
                                .background(PureWhite, RoundedCornerShape(3.dp))
                                .neoBorder(width = 1.5.dp, color = Color.Black, shape = RoundedCornerShape(3.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(data.workRatio.toFloat().coerceAtLeast(1f))
                                    .fillMaxHeight()
                                    .background(HeatCoral)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(data.reviewRatio.toFloat().coerceAtLeast(1f))
                                    .fillMaxHeight()
                                    .background(CyanContainer)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(data.learnRatio.toFloat().coerceAtLeast(1f))
                                    .fillMaxHeight()
                                    .background(VoltYellow)
                            )
                        }

                        // Breakdown Chips Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            DistributionChip(
                                label = "WORK",
                                percentage = "${data.workRatio}%",
                                boxColor = HeatCoral,
                                modifier = Modifier.weight(1f)
                            )
                            DistributionChip(
                                label = "REVIEW",
                                percentage = "${data.reviewRatio}%",
                                boxColor = CyanContainer,
                                modifier = Modifier.weight(1f)
                            )
                            DistributionChip(
                                label = "LEARN",
                                percentage = "${data.learnRatio}%",
                                boxColor = VoltYellow,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // 5. FLOW ANCHOR IDENTIFIED Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neoCard(
                            backgroundColor = InkBlack,
                            borderWidth = 2.dp,
                            shadowOffset = 3.dp,
                            cornerRadius = 4.dp
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(HyperCyanDark, RoundedCornerShape(4.dp))
                                .neoBorder(width = 1.5.dp, color = PureWhite, shape = RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radar,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "FLOW ANCHOR IDENTIFIED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = HyperCyan
                            )

                            Text(
                                text = data.flowAnchorTime,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = PureWhite
                            )

                            Text(
                                text = "${data.efficiencyRating}% EFFICIENCY RATING",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.5.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = HyperCyan
                            )
                        }
                    }
                }

                // 6. RECENT MILESTONES Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neoCard(
                            backgroundColor = PureWhite,
                            borderWidth = 2.dp,
                            shadowOffset = 3.dp,
                            cornerRadius = 4.dp
                        )
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = HeatCoral,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "RECENT MILESTONES",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = InkBlack
                                )
                            }

                            Text(
                                text = "LEVEL 07",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp
                                ),
                                color = TextMuted
                            )
                        }

                        // Milestone Item 1: DEEP DIVER
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PureWhite, RoundedCornerShape(3.dp))
                                .neoBorder(width = 1.5.dp, color = Color.Black, shape = RoundedCornerShape(3.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(VoltYellow, RoundedCornerShape(3.dp))
                                        .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(3.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Pool,
                                        contentDescription = null,
                                        tint = InkBlack,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "DEEP DIVER",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        ),
                                        color = InkBlack
                                    )
                                    Text(
                                        text = "LOGGED 100+ HOURS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        color = TextVariant
                                    )

                                    Box(
                                        modifier = Modifier
                                            .background(SurfaceContainerHigh, RoundedCornerShape(2.dp))
                                            .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(2.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "UNLOCKED YESTERDAY",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 8.sp
                                            ),
                                            color = TextMuted
                                        )
                                    }
                                }
                            }
                        }

                        // Milestone Item 2: CONSISTENCY MASTER
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PureWhite, RoundedCornerShape(3.dp))
                                .neoBorder(width = 1.5.dp, color = Color.Black, shape = RoundedCornerShape(3.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(HeatCoral, RoundedCornerShape(3.dp))
                                        .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(3.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = PureWhite,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "CONSISTENCY MASTER",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        ),
                                        color = InkBlack
                                    )
                                    Text(
                                        text = "5-DAY STREAK MAINTAINED",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        color = TextVariant
                                    )

                                    Box(
                                        modifier = Modifier
                                            .background(VoltYellow, RoundedCornerShape(2.dp))
                                            .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(2.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "ACTIVE STREAK",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 8.sp
                                            ),
                                            color = InkBlack
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 7. Full-width VoltYellow Export Button
                NeoInteractiveBox(
                    onClick = onExportTelemetry,
                    backgroundColor = VoltYellow,
                    shadowOffset = 3.dp,
                    borderWidth = 2.dp,
                    cornerRadius = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("button_export_telemetry")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = InkBlack,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "[ EXPORT PRODUCTIVITY AUDIT .CSV ]",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = InkBlack
                        )
                    }
                }

                if (state.exportedToastMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .neoCard(backgroundColor = VoltYellow, borderWidth = 1.5.dp, shadowOffset = 2.dp, cornerRadius = 3.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.exportedToastMessage,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = InkBlack
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyRhythmBarChart(metrics: List<DailyFocusMetric>) {
    val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")
    val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val displayMetrics = if (metrics.isEmpty()) {
        dayLabels.mapIndexed { i, label ->
            DailyFocusMetric(label, dayNames[i], 0f, isCurrentDay = (i == 3))
        }
    } else metrics

    val maxVal = 5.0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(PureWhite, RoundedCornerShape(3.dp))
            .neoBorder(width = 1.5.dp, color = Color.Black, shape = RoundedCornerShape(3.dp))
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Target Benchmark Dashed Line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp)
            ) {
                // Dashed line representation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.5.dp)
                        .background(HeatCoral)
                )

                // TARGET 4H Tag
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .background(HeatCoral, RoundedCornerShape(2.dp))
                        .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(2.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "TARGET 4H",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp
                        ),
                        color = PureWhite
                    )
                }
            }

            // Bars Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                displayMetrics.forEach { metric ->
                    val isHighlighted = metric.isCurrentDay
                    val barHeightFraction = (metric.focusHours / maxVal).coerceIn(0.08f, 0.95f)
                    val dayPct = ((metric.focusHours / 4.0f) * 100).toInt()

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.width(32.dp)
                    ) {
                        if (isHighlighted || metric.focusHours > 0f) {
                            // Yellow % Tag Callout Above
                            Box(
                                modifier = Modifier
                                    .background(VoltYellow, RoundedCornerShape(2.dp))
                                    .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(2.dp))
                                    .padding(horizontal = 3.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "${dayPct}%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp
                                    ),
                                    color = InkBlack
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                        }

                        // Bar Rect
                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .height((60 * barHeightFraction).dp)
                                .background(
                                    if (isHighlighted) HeatCoral else SurfaceContainerHigh,
                                    RoundedCornerShape(2.dp)
                                )
                                .neoBorder(width = 1.5.dp, color = Color.Black, shape = RoundedCornerShape(2.dp))
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Day Label below
                        if (isHighlighted) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(VoltYellow, RoundedCornerShape(2.dp))
                                    .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(2.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = metric.dayOfWeek,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = InkBlack
                                )
                            }
                        } else {
                            Text(
                                text = metric.dayOfWeek,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = InkBlack
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DistributionChip(
    label: String,
    percentage: String,
    boxColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(PureWhite, RoundedCornerShape(3.dp))
            .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(3.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(boxColor, RoundedCornerShape(1.dp))
                    .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(1.dp))
            )
            Text(
                text = "$label $percentage",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.5.sp
                ),
                color = InkBlack
            )
        }
    }
}

