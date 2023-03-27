package com.signora.calendario.ui.theme

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

@Stable
class Colors(
    backgroundColor: Color,
    onBackgroundColor: Color,
    defaultItemBackgroundColor: Color,
    selectedItemBackgroundColor: Color,
    onSelectedItemBackgroundColor: Color,
    todayItemBackgroundColor: Color,
    onTodayItemBackgroundColor: Color,
    isLight: Boolean
) {
    var backgroundColor by mutableStateOf(backgroundColor, structuralEqualityPolicy())
        internal set

    var onBackgroundColor by mutableStateOf(onBackgroundColor, structuralEqualityPolicy())

    var defaultItemBackgroundColor by mutableStateOf(defaultItemBackgroundColor, structuralEqualityPolicy())
        internal set

    var selectedItemBackgroundColor by mutableStateOf(selectedItemBackgroundColor, structuralEqualityPolicy())
        internal set;

    var onSelectedItemBackgroundColor by mutableStateOf(onSelectedItemBackgroundColor, structuralEqualityPolicy())
        internal set

    var todayItemBackgroundColor by mutableStateOf(todayItemBackgroundColor, structuralEqualityPolicy())
        internal set

    var onTodayItemBackgroundColor by mutableStateOf(onTodayItemBackgroundColor, structuralEqualityPolicy())
        internal set

    var isLight by mutableStateOf(isLight, structuralEqualityPolicy())
        internal set

    fun copy(
        backgroundColor: Color = this.backgroundColor,
        onBackgroundColor: Color = this.onBackgroundColor,
        defaultItemBackgroundColor: Color = this.defaultItemBackgroundColor,
        selectedItemBackgroundColor: Color = this.selectedItemBackgroundColor,
        onSelectedItemBackgroundColor: Color = this.onSelectedItemBackgroundColor,
        todayItemBackgroundColor: Color = this.todayItemBackgroundColor,
        onTodayItemBackgroundColor: Color = this.onTodayItemBackgroundColor,
        isLight: Boolean = this.isLight
    ): Colors = Colors(
        backgroundColor,
        onBackgroundColor,
        defaultItemBackgroundColor,
        selectedItemBackgroundColor,
        onSelectedItemBackgroundColor,
        todayItemBackgroundColor,
        onTodayItemBackgroundColor,
        isLight
    )

    override fun toString(): String {
        return "Colors(" +
                "backgroundColor=$backgroundColor, " +
                "onBackgroundColor=$onBackgroundColor, " +
                "defaultItemBackgroundColor=$defaultItemBackgroundColor, " +
                "selectedItemBackgroundColor=$selectedItemBackgroundColor, " +
                "onSelectedItemBackgroundColor=$onSelectedItemBackgroundColor, " +
                "todayItemBackgroundColor=$todayItemBackgroundColor, " +
                "onTodayItemBackgroundColor=$onTodayItemBackgroundColor, " +
                "isLight=$isLight" +
                ")"
    }
}

fun lightColors(
    backgroundColor: Color = Color.White,
    onBackgroundColor: Color = Color.Black,
    defaultItemBackgroundColor: Color = Color.White,
    selectedItemBackgroundColor: Color = Color(0xFFE7E6E6),
    onSelectedItemBackgroundColor: Color = Color.Black,
    todayItemBackgroundColor: Color = Color(0xFF0067BF),
    onTodayItemBackgroundColor: Color = Color.Black
): Colors = Colors(
    backgroundColor,
    onBackgroundColor,
    defaultItemBackgroundColor,
    selectedItemBackgroundColor,
    onSelectedItemBackgroundColor,
    todayItemBackgroundColor,
    onTodayItemBackgroundColor,
    true
)

fun darkColors(
    backgroundColor: Color = Color(0xFF121212),
    onBackgroundColor: Color = Color.White,
    defaultItemBackgroundColor: Color = Color(0xFF121212),
    selectedItemBackgroundColor: Color = Color(0x943E3E3E),
    onSelectedItemBackgroundColor: Color = Color.White,
    todayItemBackgroundColor: Color = Color(0xFF4CC1FE),
    onTodayItemBackgroundColor: Color = Color.White
): Colors = Colors(
    backgroundColor,
    onBackgroundColor,
    defaultItemBackgroundColor,
    selectedItemBackgroundColor,
    onSelectedItemBackgroundColor,
    todayItemBackgroundColor,
    onTodayItemBackgroundColor,
    false
)

internal fun Colors.updateColorsFrom(other: Colors) {
    backgroundColor = other.backgroundColor
    onBackgroundColor = other.onBackgroundColor
    defaultItemBackgroundColor = other.defaultItemBackgroundColor
    selectedItemBackgroundColor = other.selectedItemBackgroundColor
    onSelectedItemBackgroundColor = other.onSelectedItemBackgroundColor
    todayItemBackgroundColor = other.todayItemBackgroundColor
    onTodayItemBackgroundColor = other.onTodayItemBackgroundColor
    isLight = other.isLight
}

internal val LocalColors = compositionLocalOf { lightColors() }