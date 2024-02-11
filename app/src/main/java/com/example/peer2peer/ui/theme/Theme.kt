package com.example.peer2peer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.material3.darkColorScheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.platform.LocalContext
import com.example.peer2peer.R
import com.example.peer2peer.ui.theme.Typography as Typography

@Composable
fun P2PTheme3(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    isDynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkColorScheme = darkColorScheme(
        primary = MaterialTheme.colorScheme.primary,
        secondary = colorResource(id = R.color.secondary),
        tertiary = colorResource(id = R.color.tertiary),
        onPrimary = colorResource(id = R.color.primary).copy(alpha = 0.1f)
    )

    val lightColorScheme = lightColorScheme(
        primary = MaterialTheme.colorScheme.primary,
        secondary = colorResource(id = R.color.secondary),
        tertiary = colorResource(id = R.color.tertiary),
        onPrimary = colorResource(id = R.color.primary).copy(alpha = 0.1f)
    )

    val dynamicColor = isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = when {
        dynamicColor && isDarkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !isDarkTheme -> dynamicLightColorScheme(LocalContext.current)
        isDarkTheme -> darkColorScheme
        else -> darkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = Shapes3,
        typography = Typography3,
        content = content
    )
}

@Composable
fun P2PTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val darkColorPalette = darkColors(
        primary = colorResource(id = R.color.primary),
        primaryVariant = colorResource(id = R.color.dark_grey_app),
        secondary = colorResource(id = R.color.light_grey_app)
    )

    val lightColorPalette = lightColors(
        primary = colorResource(id = R.color.primary),
        primaryVariant = colorResource(id = R.color.dark_grey_app),
        secondary = colorResource(id = R.color.light_grey_app)

        /* Other default colors to override
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black,
        */
    )

    val colors = if (darkTheme) { darkColorPalette } else { lightColorPalette }

    androidx.compose.material.MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}