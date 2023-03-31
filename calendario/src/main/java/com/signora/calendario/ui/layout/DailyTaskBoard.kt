package com.signora.calendario.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.signora.calendario.models.Task
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
    // Red, Pink, Purple, Deep Purple, Indigo, Blue, Light Blue, Cyan, Teal, Green,
    // Light Green, Lime, Yellow, Amber, Orange, Deep Orange, Brown, Blue Grey
    val material100 = arrayOf(
        0xFFFFCDD2, 0xFFF8BBD0, 0xFFE1BEE7, 0xFFD1C4E9, 0xFFC5CAE9, 0xFFBBDEFB,
        0xFFB3E5FC, 0xFFB2EBF2, 0xFFB2DFDB, 0xFFC8E6C9, 0xFFDCEDC8, 0xFFF0F4C3,
        0xFFFFF9C4, 0xFFFFECB3, 0xFFFFE0B2, 0xFFFFCCBC, 0xFFD7CCC8, 0xFFCFD8DC
    )

    DailyTaskBoard(
        currentTime = LocalDateTime.of(2023, 1, 1, 12, 0, 0),
        taskPlanList = (0..23).map {
            Task(
                Pair(
                    LocalDateTime.of(2023, 1, 1, it, 0, 0),
                    LocalDateTime.of(2023, 1, 1, it, 59, 59)
                ),
                color = Color(material100.random()),
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