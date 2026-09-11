package com.example.presentation.settings

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.presentation.components.TopHeaderBar
import com.example.ui.theme.BoneCanvas
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.HeatCoral
import com.example.ui.theme.InkBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextVariant
import com.example.ui.theme.VoltYellow

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onEvent: (SettingsEvent) -> Unit,
    onOpenStandby: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val prefs = state.preferences

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
            // 1. Top Bar
            TopHeaderBar(
                subtitle = "CONFIGURATION",
                onStandbyClick = onOpenStandby
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Title
                Column {
                    Text(
                        text = "SYSTEM DEFAULTS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = TextVariant
                    )
                    Text(
                        text = "SETTINGS",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        color = InkBlack
                    )
                }

                // Section 1: Timer Intervals
                SettingsSectionCard(title = "TIMER INTERVALS // DURATION") {
                    NumberSettingRow(
                        label = "FOCUS INTERVAL",
                        value = "${prefs.focusDurationMinutes} MIN",
                        onDecrement = { onEvent(SettingsEvent.ChangeFocusDuration(-1)) },
                        onIncrement = { onEvent(SettingsEvent.ChangeFocusDuration(1)) }
                    )
                    NumberSettingRow(
                        label = "SHORT BREAK",
                        value = "${prefs.shortBreakDurationMinutes} MIN",
                        onDecrement = { onEvent(SettingsEvent.ChangeShortBreakDuration(-1)) },
                        onIncrement = { onEvent(SettingsEvent.ChangeShortBreakDuration(1)) }
                    )
                    NumberSettingRow(
                        label = "LONG BREAK",
                        value = "${prefs.longBreakDurationMinutes} MIN",
                        onDecrement = { onEvent(SettingsEvent.ChangeLongBreakDuration(-1)) },
                        onIncrement = { onEvent(SettingsEvent.ChangeLongBreakDuration(1)) }
                    )
                    NumberSettingRow(
                        label = "SESSIONS BEFORE LONG BREAK",
                        value = "${prefs.sessionsBeforeLongBreak} SESSIONS",
                        onDecrement = { onEvent(SettingsEvent.ChangeSessionsBeforeLongBreak(-1)) },
                        onIncrement = { onEvent(SettingsEvent.ChangeSessionsBeforeLongBreak(1)) }
                    )
                }

                // Section 2: Automation & Flow
                SettingsSectionCard(title = "AUTOMATION & FLOW") {
                    ToggleSettingRow(
                        label = "AUTO-START BREAKS",
                        description = "Automatically trigger rest interval on session complete",
                        checked = prefs.autoStartBreaks,
                        onCheckedChange = { onEvent(SettingsEvent.ToggleAutoStartBreaks(it)) }
                    )
                    ToggleSettingRow(
                        label = "AUTO-START FOCUS",
                        description = "Begin next focus sprint immediately when break ends",
                        checked = prefs.autoStartFocus,
                        onCheckedChange = { onEvent(SettingsEvent.ToggleAutoStartFocus(it)) }
                    )
                }

                // Section 3: Tactility & Sound
                SettingsSectionCard(title = "TACTILITY & AUDIO") {
                    ToggleSettingRow(
                        label = "AUDIO SIGNALS",
                        description = "Resonant tone alert on timer transitions",
                        checked = prefs.soundEnabled,
                        onCheckedChange = { onEvent(SettingsEvent.ToggleSound(it)) }
                    )
                    ToggleSettingRow(
                        label = "MECHANICAL HAPTICS",
                        description = "Micro-vibrations for tactile button presses",
                        checked = prefs.vibrationEnabled,
                        onCheckedChange = { onEvent(SettingsEvent.ToggleVibration(it)) }
                    )
                }

                // Section 4: Display & Standby
                SettingsSectionCard(title = "DISPLAY & STANDBY") {
                    ToggleSettingRow(
                        label = "AUTO-STANDBY ON LANDSCAPE",
                        description = "Switch directly to Desk Clock when phone is rotated",
                        checked = prefs.standbyOnLandscape,
                        onCheckedChange = { onEvent(SettingsEvent.ToggleStandbyOnLandscape(it)) }
                    )
                    ToggleSettingRow(
                        label = "KEEP SCREEN AWAKE",
                        description = "Prevent phone sleep during active focus sprints",
                        checked = prefs.keepScreenAwake,
                        onCheckedChange = { onEvent(SettingsEvent.ToggleKeepScreenAwake(it)) }
                    )
                    ToggleSettingRow(
                        label = "OLED PURE BLACK",
                        description = "Absolute #000000 background for power saving",
                        checked = prefs.oledPureBlack,
                        onCheckedChange = { onEvent(SettingsEvent.ToggleOledPureBlack(it)) }
                    )
                }

                // Section 5: Reset & Version Info
                SettingsSectionCard(title = "SYSTEM INFO // FACTORY RESET") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "POMODIUS // MAD ENGINE",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = InkBlack
                            )
                            Text(
                                text = "v2.4.0 • Neo-Brutalist Kinetic Edition",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = TextMuted
                            )
                        }

                        NeoInteractiveBox(
                            onClick = { onEvent(SettingsEvent.ResetDefaults) },
                            backgroundColor = PureWhite,
                            shadowOffset = 2.dp,
                            borderWidth = 1.5.dp,
                            cornerRadius = 4.dp,
                            modifier = Modifier.testTag("button_reset_defaults")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.RestartAlt, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "RESET",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = ErrorRed
                                )
                            }
                        }
                    }
                }

                if (state.toastMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .neoCard(backgroundColor = VoltYellow, borderWidth = 1.5.dp, shadowOffset = 2.dp, cornerRadius = 3.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.toastMessage,
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
fun SettingsSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .neoCard(
                backgroundColor = PureWhite,
                borderWidth = 2.5.dp,
                shadowOffset = 3.dp,
                cornerRadius = 6.dp
            )
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = TextVariant
            )

            content()
        }
    }
}

@Composable
fun NumberSettingRow(
    label: String,
    value: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = InkBlack
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NeoInteractiveBox(
                onClick = onDecrement,
                backgroundColor = SurfaceContainerHigh,
                shadowOffset = 1.dp,
                borderWidth = 1.dp,
                cornerRadius = 3.dp,
                modifier = Modifier.size(28.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrement", tint = InkBlack, modifier = Modifier.size(16.dp))
                }
            }

            Box(
                modifier = Modifier
                    .width(90.dp)
                    .background(SurfaceContainerHigh, RoundedCornerShape(3.dp))
                    .neoBorder(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(3.dp))
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = InkBlack
                )
            }

            NeoInteractiveBox(
                onClick = onIncrement,
                backgroundColor = SurfaceContainerHigh,
                shadowOffset = 1.dp,
                borderWidth = 1.dp,
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

@Composable
fun ToggleSettingRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = InkBlack
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                color = TextMuted
            )
        }

        NeoSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun NeoSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 24.dp else 2.dp,
        label = "switchThumb"
    )

    Box(
        modifier = modifier
            .width(52.dp)
            .height(28.dp)
            .neoShadow(offsetX = 2.dp, offsetY = 2.dp, color = Color.Black, cornerRadius = 14.dp)
            .background(
                if (checked) VoltYellow else SurfaceContainerHigh,
                RoundedCornerShape(14.dp)
            )
            .neoBorder(width = 1.5.dp, color = Color.Black, shape = RoundedCornerShape(14.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(22.dp)
                .neoShadow(offsetX = 1.dp, offsetY = 1.dp, color = Color.Black, cornerRadius = 11.dp)
                .background(InkBlack, CircleShape)
                .neoBorder(width = 1.dp, color = Color.Black, shape = CircleShape)
        )
    }
}
