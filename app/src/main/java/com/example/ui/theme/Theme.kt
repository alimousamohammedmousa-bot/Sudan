package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PetrolGreen80,
    onPrimary = Color(0xFF00382E),
    primaryContainer = PetrolGreenContainerDark,
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = Amber80,
    onSecondary = Color(0xFF451A03),
    secondaryContainer = AmberContainerDark,
    onSecondaryContainer = Color(0xFFFDE68A),
    tertiary = NileBlue80,
    onTertiary = Color(0xFF00354E),
    tertiaryContainer = NileBlueContainerDark,
    onTertiaryContainer = Color(0xFFBAE6FD),
    background = DarkBg,
    onBackground = Color(0xFFE6F1ED),
    surface = DarkSurface,
    onSurface = Color(0xFFE6F1ED),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC0D5CE),
    error = StatusClosedRed
)

private val LightColorScheme = lightColorScheme(
    primary = PetrolGreen40,
    onPrimary = Color.White,
    primaryContainer = PetrolGreenContainerLight,
    onPrimaryContainer = Color(0xFF064E3B),
    secondary = Amber40,
    onSecondary = Color.White,
    secondaryContainer = AmberContainerLight,
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = NileBlue40,
    onTertiary = Color.White,
    tertiaryContainer = NileBlueContainerLight,
    onTertiaryContainer = Color(0xFF075985),
    background = LightBg,
    onBackground = Color(0xFF13221F),
    surface = LightSurface,
    onSurface = Color(0xFF13221F),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF38534C),
    error = StatusClosedRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our handcrafted Tulambati petrol green & amber theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
