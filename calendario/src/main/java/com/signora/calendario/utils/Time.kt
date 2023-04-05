package com.signora.calendario.utils

import com.signora.calendario.models.KanbanPeriod
import java.time.LocalDateTime

internal const val dayLength = 24 * 60 * 60
internal const val weekLength = dayLength * 7

internal fun LocalDateTime.getTargetScaled(target: Float, period: KanbanPeriod = KanbanPeriod.DAY): Float {
    return when (period) {
        KanbanPeriod.WEEK -> {
            val second = (((this.dayOfWeek.value - 1) * 24 * 60 * 60) + (this.hour * 60 * 60) + (this.minute * 60) + this.second).toFloat()
            (second / weekLength) * target
        }
        KanbanPeriod.DAY -> {
            val second = ((this.hour * 60 * 60) + (this.minute * 60) + this.second).toFloat()
            (second / dayLength) * target
        }
    }
}