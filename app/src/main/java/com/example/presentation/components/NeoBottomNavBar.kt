package com.example.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.NeoInteractiveBox
import com.example.core.ui.neoBorder
import com.example.core.ui.neoShadow
import com.example.ui.theme.BoneCanvas
import com.example.ui.theme.InkBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextVariant
import com.example.ui.theme.VoltYellow

enum class NavDestination(val label: String, val icon: ImageVector) {
    FOCUS("FOCUS", Icons.Default.Timer),
    TASKS("TASKS", Icons.Default.ChecklistRtl),
    INSIGHTS("INSIGHTS", Icons.Default.Analytics),
    SETTINGS("SETTINGS", Icons.Default.Tune)
}

@Composable
fun NeoBottomNavBar(
    currentDestination: NavDestination,
    onNavigate: (NavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(BoneCanvas)
            .neoBorder(width = 2.dp, color = Color.Black)
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavDestination.entries.forEach { destination ->
                val isSelected = destination == currentDestination

                NeoInteractiveBox(
                    onClick = { onNavigate(destination) },
                    backgroundColor = if (isSelected) VoltYellow else PureWhite,
                    shadowOffset = 2.dp,
                    borderWidth = if (isSelected) 2.dp else 1.5.dp,
                    cornerRadius = 4.dp,
                    borderColor = Color.Black,
                    modifier = Modifier.testTag("nav_tab_${destination.name.lowercase()}")
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = destination.label,
                            tint = InkBlack,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = destination.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = InkBlack
                        )
                    }
                }
            }
        }
    }
}
