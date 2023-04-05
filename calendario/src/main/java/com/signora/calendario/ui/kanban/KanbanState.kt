package com.signora.calendario.ui.kanban

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import com.signora.calendario.models.KanbanIntent
import com.signora.calendario.models.KanbanLayout
import com.signora.calendario.models.KanbanLayout.*
import com.signora.calendario.models.KanbanTask
import java.time.LocalDate
import java.time.LocalDateTime

@Stable
class KanbanState(initialLayout: KanbanLayout = GRID) {
    var taskPlanList = mutableStateListOf<KanbanTask>()

    var taskDoneList = mutableStateListOf<KanbanTask>()

    var selectedDate by mutableStateOf(LocalDateTime.now())
        private set

    // var selectedWeek by mutableStateOf(LocalDate.now().getWeekRange())
    //     private set

    // var period by mutableStateOf(KanbanPeriod.DAY)
    //     private set

    var layout by mutableStateOf(initialLayout)
        private set

    fun onIntent(intent: KanbanIntent) {
        when(intent) {
            is KanbanIntent.LoadTask -> {
                val targetDate = intent.date
                selectedDate = targetDate.atStartOfDay()
                // selectedWeek = targetDate.getWeekRange()
                // period = intent.period
            }
            is KanbanIntent.UseGridLayout -> layout = GRID
            is KanbanIntent.UseListLayout -> layout = LIST
        }
    }

    companion object {
        val Saver: Saver<KanbanState, *> = listSaver(
            save = { listOf<Any>(it.layout) },
            restore = { KanbanState(it[0] as KanbanLayout) }
        )
    }
}