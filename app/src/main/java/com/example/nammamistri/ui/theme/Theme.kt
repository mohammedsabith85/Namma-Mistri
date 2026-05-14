package com.example.nammamistri.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Map our custom colors to the Material Theme
private val DarkColorScheme = darkColorScheme(
    primary = AppYellow,
    secondary = AppGreen,
    tertiary = AppRed,
    background = AppBlack,
    surface = AppDarkGray,
    onPrimary = AppBlack,
    onBackground = AppWhite,
    onSurface = AppWhite
)

// We use the same dark theme for light mode because the app is strictly dark-themed
private val LightColorScheme = darkColorScheme(
    primary = AppYellow,
    secondary = AppGreen,
    tertiary = AppRed,
    background = AppBlack,
    surface = AppDarkGray,
    onPrimary = AppBlack,
    onBackground = AppWhite,
    onSurface = AppWhite
)

@Composable
fun NammaMistriTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Turn off dynamic color so it doesn't override our Yellow/Black branding
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

    // Change the status bar color at the top of the phone to match our app
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AppBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}