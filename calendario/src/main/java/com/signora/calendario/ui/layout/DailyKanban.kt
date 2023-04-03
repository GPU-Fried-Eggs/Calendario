package com.signora.calendario.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.signora.calendario.models.KanbanTask
import com.signora.calendario.utils.generateRandomColor
import com.signora.calendario.views.KanbanGridView
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun DailyKanban(
    modifier: Modifier = Modifier,
    selectDate: LocalDate,
    onSelectDateChange: (LocalDate) -> Unit = {},
    taskPlanList: List<KanbanTask>,
    taskDoneList: List<KanbanTask>
) {
    val state = rememberScrollState()

    Box(Modifier.verticalScroll(state)) {
        KanbanGridView(
            modifier = Modifier.requiredHeight(2048.dp).then(modifier),
            timeScalar = (0..23).map {
                LocalDateTime.of(2023, 1, 1, it, 0, 0)
            },
            taskPlanList = taskPlanList,
            taskDoneList = taskDoneList
        )
    }
}

@Preview
@Composable
private fun DailyKanbanPreview() {
    data class PreviewKanbanTask(
        override val range: Pair<LocalDateTime, LocalDateTime>,
        override val color: Color = Color(0xFFB2EBF2),
        override val payload: String
    ) : KanbanTask

    DailyKanban(
        selectDate = LocalDate.of(2023, 1, 1),
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