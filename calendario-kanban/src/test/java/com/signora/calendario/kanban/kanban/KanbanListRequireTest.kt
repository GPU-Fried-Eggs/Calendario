package com.signora.calendario.kanban.kanban

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.signora.calendario.kanban.views.KanbanTaskView
import org.junit.Assert.*
import org.junit.Test

class KanbanListRequireTest {
    data class CustomTask(
        override val range: com.signora.calendario.kanban.models.KanbanTaskRange,
        override val color: Color = Color.Gray,
        override val payload: String = "",
        override val state: Boolean?
    ) : com.signora.calendario.kanban.models.KanbanTask {
        override fun formatTask(state: Boolean) = this
    }

    private val customTaskView: KanbanTaskView = { _, _ -> Spacer(modifier = Modifier.width(1.dp)) }

    @Test
    fun `when use interface as a Key of map collection, should get data by different instance`() {
        val mapOfTask = mapOf(CustomTask::class to customTaskView)

        assertEquals(
            customTaskView,
            mapOfTask[CustomTask::class]
        )
    }
}