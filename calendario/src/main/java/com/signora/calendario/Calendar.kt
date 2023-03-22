package com.signora.calendario

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.signora.calendario.ui.theme.CalenderTheme
import com.signora.calendario.ui.theme.darkColors
import com.signora.calendario.ui.theme.lightColors
import java.time.LocalDate

@Composable
fun Calendar(
    darkTheme: Boolean = isSystemInDarkTheme(),
    onItemClick: (LocalDate) -> Unit,

) {
    val colors = if (darkTheme) darkColors() else lightColors()

    CalenderTheme(
        colors = colors
    ) {
    }
}