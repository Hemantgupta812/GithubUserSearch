package com.example.githubusersearch.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DarkBlueGray80,       // Buttons, top bars in dark mode
    onPrimary = Color.Black,
    primaryContainer = MediumGray40,
    onPrimaryContainer = Color.White,
    secondary = MediumGray80,       // Muted accents
    onSecondary = Color.Black,
    secondaryContainer = MediumGray40,
    onSecondaryContainer = Color.White,
    tertiary = AccentColor,         // Stronger accent
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkBlueGray40,
    onSurfaceVariant = TextSecondaryDark,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = DarkBlueGray40,       // Buttons, top bars in light mode
    onPrimary = Color.White,
    primaryContainer = MediumGray80,
    onPrimaryContainer = Color.Black,
    secondary = MediumGray40,       // Muted accents
    onSecondary = Color.White,
    secondaryContainer = MediumGray80,
    onSecondaryContainer = Color.Black,
    tertiary = AccentColor,         // Stronger accent
    onTertiary = Color.White,
    background = LightGray,
    onBackground = TextPrimaryLight,
    surface = Color.White,
    onSurface = TextPrimaryLight,
    surfaceVariant = MediumGray80.copy(alpha = 0.5f),
    onSurfaceVariant = TextSecondaryLight,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = ErrorRed
)
@Composable
fun GithubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Defined in Type.kt
        content = content
    )
}