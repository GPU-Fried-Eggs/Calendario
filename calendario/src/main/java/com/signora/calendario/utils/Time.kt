package com.signora.calendario.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.signora.calendario.models.TaskBoardPeriod
import java.time.LocalDateTime

internal const val dayLength = 24 * 60 * 60
internal const val weekLength = dayLength * 7

internal fun LocalDateTime.getTargetScaled(target: Float, period: TaskBoardPeriod = TaskBoardPeriod.DAY): Float {
    return when (period) {
        TaskBoardPeriod.WEEK -> {
            TODO()
        }
        TaskBoardPeriod.DAY -> {
            val second = ((this.hour * 60 * 60) + (this.minute * 60) + this.second).toFloat()
            (second / dayLength) * target
        }
    }
}

internal fun getOffsetSize(
    range: Pair<LocalDateTime, LocalDateTime>,
    parentOffset: Offset,
    parentSize: Size
): Pair<Offset, Size> {
    val start = range.first.getTargetScaled(parentSize.height)
    val end = range.second.getTargetScaled(parentSize.height)

    // inherit from parent offset
    return Pair(
        first = Offset(parentOffset.x, start),
        second = Size(parentSize.width, end - start)
    )
}