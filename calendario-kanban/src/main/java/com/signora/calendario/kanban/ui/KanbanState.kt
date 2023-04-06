package com.signora.calendario.kanban.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import com.signora.calendario.kanban.enums.KanbanLayout
import com.signora.calendario.kanban.enums.KanbanLayout.GRID
import com.signora.calendario.kanban.enums.KanbanLayout.LIST
import com.signora.calendario.kanban.models.KanbanIntent
import com.signora.calendario.kanban.models.KanbanTask
import java.time.LocalDate

@Stable
class KanbanState(initialLayout: KanbanLayout = GRID) {
    val taskMap = mutableStateMapOf<LocalDate, List<KanbanTask>>()

    var selectedDate by mutableStateOf(LocalDate.now())
        private set

    var layout by mutableStateOf(initialLayout)
        private set

    val selectedTasks: List<KanbanTask>?
        get() = taskMap[selectedDate]

    fun onIntent(intent: KanbanIntent) {
        when(intent) {
            is KanbanIntent.LoadTask -> {
                val targetDate = intent.date
                selectedDate = targetDate
                taskMap[targetDate] = intent.tasks
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