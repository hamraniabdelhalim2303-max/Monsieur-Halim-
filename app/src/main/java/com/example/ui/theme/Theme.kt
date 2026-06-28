package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Gold,
    onPrimary = Color.White,
    secondary = DeepSlate,
    onSecondary = Color.White,
    tertiary = WarmCream,
    onTertiary = DeepSlate,
    background = SoftGray,
    onBackground = DeepSlate,
    surface = Color.White,
    onSurface = DeepSlate,
    surfaceVariant = WarmCream,
    onSurfaceVariant = DeepSlate,
    outline = BorderGray
)

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = Color.Black,
    secondary = Color.White,
    onSecondary = DeepSlate,
    tertiary = DeepSlate,
    onTertiary = Color.White,
    background = DarkBg,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = DeepSlate,
    onSurfaceVariant = Color.White,
    outline = DarkBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
