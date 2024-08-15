package com.evosticlabs.apollo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun ApolloTheme(
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun appOutlineTextFieldColors(isNotErrorState: Boolean = true): androidx.compose.material3.TextFieldColors  {
    if (isNotErrorState) {
        return androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(

            disabledPlaceholderColor = TextColorDisabled,
            focusedTextColor = TextBlue,
            unfocusedTextColor = TextBlue,
            disabledTextColor = TextColorDisabled,
            cursorColor = ActionBlue,
            focusedBorderColor = ActionBlue,
            unfocusedBorderColor = DormantBlue,
            containerColor = FieldColor,

        )
    } else {
        return androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(

            disabledPlaceholderColor = Color.Transparent,
            disabledTextColor = TextColorDisabled,
            cursorColor = ErrorRed,
            errorCursorColor = ErrorRed,
            focusedBorderColor = ErrorRed,
            unfocusedBorderColor = ErrorRed,
            containerColor = FieldColor
            )
    }
}