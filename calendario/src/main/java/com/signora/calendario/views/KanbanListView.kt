package com.signora.calendario.views

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.signora.calendario.models.KanbanTask
import com.signora.calendario.utils.generateRandomColor
import java.time.LocalDateTime

@Composable
fun KanbanListView(
    modifier: Modifier = Modifier,
    taskPlanList: List<KanbanTask>,
    taskDoneList: List<KanbanTask>
) {
    Column(modifier) {

    }
}

@Preview
@Composable
private fun KanbanListViewPreview() {
    data class PreviewKanbanTask(
        override val range: Pair<LocalDateTime, LocalDateTime>,
        override val color: Color = Color(0xFFB2EBF2),
        override val payload: String
    ) : KanbanTask

    KanbanListView(
        taskPlanList = (0..23).map {
            PreviewKanbanTask(
                Pair(
                    LocalDateTime.of(2023, 1, 1, it, 0, 0),
                    LocalDateTime.of(2023, 1, 1, it, 59, 59)
                ),
                color = generateRandomColor("100"),
                payload = "Preview"
            )
        },
        taskDoneList = listOf(
            PreviewKanbanTask(
                Pair(
                    LocalDateTime.of(2023, 1, 1, 8, 30, 20),
                    LocalDateTime.of(2023, 1, 1, 9, 30, 10)
                ),
                payload = "Preview"
            )
        )
    )
}