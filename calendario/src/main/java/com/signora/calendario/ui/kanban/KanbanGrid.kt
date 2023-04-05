package com.signora.calendario.ui.kanban

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.signora.calendario.R
import com.signora.calendario.models.DefaultKanbanTask
import com.signora.calendario.models.KanbanTask
import com.signora.calendario.utils.generateRandomColor
import com.signora.calendario.views.KanbanFlowView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
internal fun KanbanGrid(
    modifier: Modifier = Modifier,
    timeScalar: List<LocalDateTime>,
    timeScalarWidth: Dp = 52.dp,
    taskPlanList: List<KanbanTask>,
    taskDoneList: List<KanbanTask>
) {
    val state = rememberScrollState()

    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(25.dp)
                .padding(start = timeScalarWidth),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.kanban_plan),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = stringResource(R.string.kanban_done),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1024.dp)
            ) {
                Column(
                    modifier = Modifier
                        .width(timeScalarWidth)
                        .fillMaxHeight()
                        .padding(horizontal = 6.dp),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    timeScalar.forEach { text ->
                        Text(
                            text = text.format(DateTimeFormatter.ofPattern("HH:mm")),
                            maxLines = 1
                        )
                    }
                }
                KanbanFlowView(taskPlanList, taskDoneList)
            }
        }
    }
}

@Preview
@Composable
private fun KanbanGridPreview() {
    KanbanGrid(
        timeScalar = (0..23).map {
            LocalDateTime.of(2023, 1, 1, it, 0, 0)
        },
        taskPlanList = (0..23).map {
            DefaultKanbanTask(
                Pair(
                    LocalDateTime.of(2023, 1, 1, it, 0, 0),
                    LocalDateTime.of(2023, 1, 1, it, 59, 59)
                ),
                color = generateRandomColor("100"),
                payload = "Preview"
            )
        },
        taskDoneList = (8..16).map {
            if (it % 2 == 0) {
                DefaultKanbanTask(
                    Pair(
                        LocalDateTime.of(2023, 1, 1, it, 0, 0),
                        LocalDateTime.of(2023, 1, 1, it + 2, 0, 0)
                    ),
                    color = generateRandomColor("100"),
                    payload = "Preview(${it}:00-${it + 2}:00)"
                )
            } else {
                DefaultKanbanTask(
                    Pair(
                        LocalDateTime.of(2023, 1, 1, it, 30, 0),
                        LocalDateTime.of(2023, 1, 1, it + 2, 30, 0)
                    ),
                    color = generateRandomColor("100"),
                    payload = "Preview($it:30-${it + 2}:30)"
                )
            }
        }
    )
}