package com.signora.calendario.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.signora.calendario.models.DefaultKanbanTask
import com.signora.calendario.models.KanbanTask
import com.signora.calendario.utils.generateRandomColor
import com.signora.calendario.utils.getOffsetSize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalTextApi::class)
@Composable
fun KanbanGridView(
    modifier: Modifier = Modifier,
    timeScalar: List<LocalDateTime>,
    taskPlanList: List<KanbanTask>,
    taskDoneList: List<KanbanTask>
) {
    val textMeasurer = rememberTextMeasurer()

    Row(modifier){
        Column(
            modifier = Modifier
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
        Canvas(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            // left panel
            drawTaskPanel(
                tasks = taskPlanList,
                offset = Offset.Zero,
                size = size.copy(width = size.width / 2f),
                textMeasurer = textMeasurer,
            )

            // divide line
            drawLine(
                color = Color.Gray,
                start = center.copy(y = 10f),
                end = center.copy(y = size.height - 10f)
            )

            // right panel
            drawTaskPanel(
                tasks = taskDoneList,
                offset = center.copy(y = 0f),
                size = size.copy(width = size.width / 2f),
                textMeasurer = textMeasurer,
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawTaskPanel(
    tasks: List<KanbanTask>,
    offset: Offset,
    size: Size,
    textMeasurer: TextMeasurer
) {
    tasks.forEach {
        val (childOffset, childSize) = getOffsetSize(
            range = it.range,
            parentOffset = offset,
            parentSize = size
        )
        drawRoundRect(
            color = it.color,
            topLeft = childOffset,
            size = childSize,
            cornerRadius = CornerRadius(12f, 12f)
        )
        drawText(
            textMeasurer = textMeasurer,
            text = it.payload,
            topLeft = childOffset,
            size = childSize
        )
    }
}

@Preview
@Composable
private fun KanbanGridViewPreview() {
    KanbanGridView(
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
        taskDoneList = listOf(
            DefaultKanbanTask(
                Pair(
                    LocalDateTime.of(2023, 1, 1, 8, 30, 20),
                    LocalDateTime.of(2023, 1, 1, 9, 30, 10)
                ),
                payload = "Preview"
            )
        )
    )
}