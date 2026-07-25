package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = IndigoSecondary,
    onPrimary = Color.White,
    secondary = AmberAccent,
    onSecondary = Color.Black,
    tertiary = TealAccent,
    background = MidnightBackgroundDark,
    onBackground = MidnightTextPrimary,
    surface = MidnightSurfaceDark,
    onSurface = MidnightTextPrimary,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = MidnightTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    secondary = AmberAccent,
    onSecondary = Color.Black,
    tertiary = TealAccent,
    background = SlateBackgroundLight,
    onBackground = SlateTextPrimary,
    surface = SlateSurfaceLight,
    onSurface = SlateTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = SlateTextSecondary
)

@Composable
fun StudySaathiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep cohesive brand palette
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
