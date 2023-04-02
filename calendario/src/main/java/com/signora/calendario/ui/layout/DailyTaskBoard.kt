package com.signora.calendario.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.signora.calendario.models.Task
import com.signora.calendario.utils.generateRandomColor
import com.signora.calendario.views.TaskBoardView
import java.time.LocalDateTime

@Composable
fun DailyTaskBoard(
    currentTime: LocalDateTime,
    taskPlanList: List<Task>,
    taskDoneList: List<Task>
) {
    val state = rememberScrollState()

    Box(Modifier.verticalScroll(state)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(2048.dp)
        ) {
            TaskBoardView(
                modifier = Modifier.fillMaxHeight(),
                timeScalar = (0..23).map {
                    LocalDateTime.of(2023, 1, 1, it, 0, 0)
                },
                taskPlanList = taskPlanList,
                taskDoneList = taskDoneList
            )
        }
    }
}

@Preview
@Composable
private fun DailyTaskBoardPreview() {
    DailyTaskBoard(
        currentTime = LocalDateTime.of(2023, 1, 1, 12, 0, 0),
        taskPlanList = (0..23).map {
            Task(
                Pair(
                    LocalDateTime.of(2023, 1, 1, it, 0, 0),
                    LocalDateTime.of(2023, 1, 1, it, 59, 59)
                ),
                color = generateRandomColor("100"),
                payload = "Preview"
            )
        },
        taskDoneList = listOf(
            Task(
                Pair(
                    LocalDateTime.of(2023, 1, 1, 8, 30, 20),
                    LocalDateTime.of(2023, 1, 1, 9, 30, 10)
                ),
                payload = "Preview"
            )
        )
    )
}