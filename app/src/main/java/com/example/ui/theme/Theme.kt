package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = InkBlack,
    onPrimary = PureWhite,
    primaryContainer = VoltYellow,
    onPrimaryContainer = InkBlack,
    secondary = HeatCoralDark,
    onSecondary = PureWhite,
    secondaryContainer = HeatCoral,
    onSecondaryContainer = PureWhite,
    tertiary = HyperCyanDark,
    onTertiary = PureWhite,
    tertiaryContainer = CyanContainer,
    onTertiaryContainer = HyperCyanDark,
    background = BoneCanvas,
    onBackground = InkBlack,
    surface = BoneCanvas,
    onSurface = InkBlack,
    surfaceVariant = SurfaceContainerHighest,
    onSurfaceVariant = TextVariant,
    outline = TextMuted,
    outlineVariant = NewsprintGray,
    error = ErrorRed,
    onError = PureWhite,
    errorContainer = ErrorContainer,
    onErrorContainer = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = VoltYellow,
    onPrimary = PureBlack,
    primaryContainer = VoltYellowDark,
    onPrimaryContainer = PureWhite,
    secondary = HeatCoral,
    onSecondary = PureWhite,
    secondaryContainer = HeatCoralDark,
    onSecondaryContainer = PureWhite,
    tertiary = HyperCyan,
    onTertiary = PureBlack,
    tertiaryContainer = HyperCyanDark,
    onTertiaryContainer = CyanContainer,
    background = PureBlack,
    onBackground = PureWhite,
    surface = StandbyDarkSurface,
    onSurface = PureWhite,
    surfaceVariant = StandbyDarkContainer,
    onSurfaceVariant = NewsprintGray,
    outline = TextMuted,
    error = ErrorRed,
    onError = PureWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

