package com.blank.bookverse.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

private val Black = Color(0xFF000000)
private val DarkGray = Color(0xFF121212)
private val Gray = Color(0xFF808080)
private val LightGray = Color(0xFFE0E0E0)
private val White = Color(0xFFFFFFFF)

private val DarkColorScheme = darkColorScheme(
    primary = White,
    onPrimary = Black,
    primaryContainer = DarkGray,
    onPrimaryContainer = White,
    secondary = LightGray,
    onSecondary = Black,
    tertiary = Gray,
    background = Black,
    surface = Black,
    onSurface = White,
    surfaceVariant = DarkGray,
    onSurfaceVariant = White
)

private val LightColorScheme = lightColorScheme(
    primary = Black,
    onPrimary = White,
    primaryContainer = LightGray,
    onPrimaryContainer = Black,
    secondary = Gray,
    onSecondary = White,
    tertiary = DarkGray,
    background = White,
    surface = White,
    onSurface = Black,
    surfaceVariant = LightGray,
    onSurfaceVariant = Black
)

@Composable
fun BookVerseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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

    CompositionLocalProvider(
        LocalDensity provides Density(
            density = LocalDensity.current.density,
            fontScale = 1f
        )
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}