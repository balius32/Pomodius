package com.example.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.NeoInteractiveBox
import com.example.core.ui.neoBorder
import com.example.core.ui.neoShadow
import com.example.domain.model.PomodoroType
import com.example.ui.theme.InkBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.TextVariant
import com.example.ui.theme.VoltYellow

@Composable
fun SegmentedModeSwitcher(
    currentType: PomodoroType,
    onSelectMode: (PomodoroType) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .neoShadow(offsetX = 2.dp, offsetY = 2.dp, color = Color.Black, cornerRadius = 4.dp)
            .background(SurfaceContainerHigh, RoundedCornerShape(4.dp))
            .neoBorder(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(4.dp))
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            PomodoroType.entries.forEach { mode ->
                val isSelected = mode == currentType
                val icon = when (mode) {
                    PomodoroType.FOCUS -> Icons.Default.Bolt
                    PomodoroType.SHORT_BREAK -> Icons.Default.Coffee
                    PomodoroType.LONG_BREAK -> Icons.Default.Bedtime
                }

                NeoInteractiveBox(
                    onClick = { onSelectMode(mode) },
                    backgroundColor = if (isSelected) VoltYellow else PureWhite,
                    shadowOffset = if (isSelected) 2.dp else 1.dp,
                    borderWidth = if (isSelected) 1.5.dp else 1.dp,
                    cornerRadius = 3.dp,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mode_${mode.name.lowercase()}")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = mode.label,
                                tint = InkBlack,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = mode.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
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
