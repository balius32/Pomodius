package com.example.presentation.standby

import android.app.Activity
import android.content.res.Configuration
import android.view.WindowManager
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.NeoInteractiveBox
import com.example.core.ui.neoBorder
import com.example.core.ui.neoShadow
import com.example.domain.model.PomodoroType
import com.example.domain.model.TimerState
import com.example.presentation.components.MechanicalProgressBar
import com.example.presentation.focus.FocusEvent
import com.example.presentation.focus.FocusUiState
import com.example.ui.theme.HeatCoral
import com.example.ui.theme.OledBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StandbyDarkContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.VoltYellow

@Composable
fun StandbyScreen(
    state: FocusUiState,
    onEvent: (FocusEvent) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Keep screen awake while in Standby Desk Clock mode
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // Pulse animation for recording dot
    val infiniteTransition = rememberInfiniteTransition(label = "standbyPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "standbyPulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("screen_standby")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Bar in Standby Mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Live indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
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
                        text = "STANDBY // DESK FOCUS CLOCK",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        ),
                        color = PureWhite
                    )
                }

                // Exit Standby Button
                NeoInteractiveBox(
                    onClick = onDismiss,
                    backgroundColor = StandbyDarkContainer,
                    shadowOffset = 2.dp,
                    borderWidth = 1.5.dp,
                    cornerRadius = 4.dp,
                    borderColor = PureWhite,
                    modifier = Modifier.testTag("button_exit_standby")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Exit Standby",
                            tint = PureWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "EXIT",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PureWhite
                        )
                    }
                }
            }

            if (isLandscape) {
                // 2. Landscape Layout (Side-by-Side arrangement for Desk Mounts)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column: Timer & Progress
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Mode Pill
                        Box(
                            modifier = Modifier
                                .neoShadow(offsetX = 2.dp, offsetY = 2.dp, color = PureWhite, cornerRadius = 4.dp)
                                .background(VoltYellow, RoundedCornerShape(4.dp))
                                .neoBorder(width = 2.dp, color = PureWhite, shape = RoundedCornerShape(4.dp))
                                .padding(horizontal = 14.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${state.currentType.label} // CYCLE ${state.currentCycle} OF ${state.maxCycles}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = Color.Black
                            )
                        }

                        // Clock Display
                        Text(
                            text = state.formattedTime,
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 72.sp,
                                lineHeight = 72.sp,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-3).sp
                            ),
                            color = if (state.currentType == PomodoroType.FOCUS) HeatCoral else VoltYellow,
                            modifier = Modifier.testTag("standby_timer_display")
                        )

                        // Session Progress Meter
                        Box(modifier = Modifier.width(260.dp)) {
                            MechanicalProgressBar(
                                currentCycle = state.currentCycle,
                                maxCycles = state.maxCycles,
                                currentType = state.currentType,
                                progress = state.progress
                            )
                        }
                    }

                    // Right Column: Active Task & Playback Controls
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Linked Target
                        Text(
                            text = state.activeTask?.title ?: "Deep Work Focus Session",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = PureWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Playback Controls
                        StandbyControls(state = state, onEvent = onEvent)
                    }
                }
            } else {
                // 3. Portrait Layout (Vertical Stack with Safe Margins)
                Column(
                    modifier = Modifier.padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Mode Pill
                    Box(
                        modifier = Modifier
                            .neoShadow(offsetX = 2.dp, offsetY = 2.dp, color = PureWhite, cornerRadius = 4.dp)
                            .background(VoltYellow, RoundedCornerShape(4.dp))
                            .neoBorder(width = 2.dp, color = PureWhite, shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${state.currentType.label} // CYCLE ${state.currentCycle} OF ${state.maxCycles}",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = Color.Black
                        )
                    }

                    // Clock Display
                    Text(
                        text = state.formattedTime,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 84.sp,
                            lineHeight = 84.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-4).sp
                        ),
                        color = if (state.currentType == PomodoroType.FOCUS) HeatCoral else VoltYellow,
                        modifier = Modifier.testTag("standby_timer_display")
                    )

                    // Session Progress Meter
                    Box(modifier = Modifier.width(280.dp)) {
                        MechanicalProgressBar(
                            currentCycle = state.currentCycle,
                            maxCycles = state.maxCycles,
                            currentType = state.currentType,
                            progress = state.progress
                        )
                    }

                    // Linked Target
                    Text(
                        text = state.activeTask?.title ?: "Deep Work Focus Session",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = PureWhite,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Bottom Minimal Controls
                StandbyControls(state = state, onEvent = onEvent)
            }
        }
    }
}

@Composable
private fun StandbyControls(
    state: FocusUiState,
    onEvent: (FocusEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Restart
        NeoInteractiveBox(
            onClick = { onEvent(FocusEvent.ResetTimer) },
            backgroundColor = StandbyDarkContainer,
            shadowOffset = 2.dp,
            borderWidth = 1.5.dp,
            cornerRadius = 4.dp,
            borderColor = PureWhite,
            modifier = Modifier.size(52.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Replay,
                    contentDescription = "Restart",
                    tint = PureWhite,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Pause / Resume
        val isRunning = state.timerState == TimerState.RUNNING
        val btnColor = if (isRunning) VoltYellow else HeatCoral
        val iconColor = if (isRunning) Color.Black else PureWhite

        NeoInteractiveBox(
            onClick = { onEvent(FocusEvent.ToggleTimer) },
            backgroundColor = btnColor,
            shadowOffset = 2.dp,
            borderWidth = 2.dp,
            cornerRadius = 4.dp,
            borderColor = PureWhite,
            modifier = Modifier
                .height(52.dp)
                .padding(horizontal = 4.dp)
                .testTag("standby_button_toggle")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) "Pause" else "Resume",
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = if (isRunning) "PAUSE" else "RESUME",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = iconColor
                )
            }
        }

        // Skip
        NeoInteractiveBox(
            onClick = { onEvent(FocusEvent.SkipSession) },
            backgroundColor = StandbyDarkContainer,
            shadowOffset = 2.dp,
            borderWidth = 1.5.dp,
            cornerRadius = 4.dp,
            borderColor = PureWhite,
            modifier = Modifier.size(52.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.SkipNext,
                    contentDescription = "Skip",
                    tint = PureWhite,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

