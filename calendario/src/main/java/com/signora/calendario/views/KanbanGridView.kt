package com.signora.calendario.views

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                    text = text.format(DateTimeFormatter.ofPattern("HH:mm"))
                )
            }
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Log.d("TaskBoard", "$size, $center")
            // left panel
            taskPlanList.forEach {
                val (offset, size) = getOffsetSize(
                    range = it.range,
                    parentOffset = Offset.Zero,
                    parentSize = size.copy(width = size.width / 2f)
                )
                drawRoundRect(
                    color = it.color,
                    topLeft = offset,
                    size = size,
                    cornerRadius = CornerRadius(12f, 12f)
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = it.payload,
                    topLeft = offset,
                    size = size
                )
            }

            // divide line
            drawLine(
                color = Color.Gray,
                start = center.copy(y = 10f),
                end = center.copy(y = size.height - 10f)
            )

            // right panel
            taskDoneList.forEach {
                val (offset, size) = getOffsetSize(
                    range = it.range,
                    parentOffset = center.copy(y = 0f),
                    parentSize = size.copy(width = size.width / 2f)
                )
                drawRoundRect(
                    color = it.color,
                    topLeft = offset,
                    size = size,
                    cornerRadius = CornerRadius(12f, 12f)
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = it.payload,
                    topLeft = offset,
                    size = size
                )
            }
        }
    }
}

@Preview
@Composable
private fun KanbanGridViewPreview() {
    data class PreviewKanbanTask(
        override val range: Pair<LocalDateTime, LocalDateTime>,
        override val color: Color = Color(0xFFB2EBF2),
        override val payload: String
    ) : KanbanTask

    KanbanGridView(
        timeScalar = (0..23).map {
            LocalDateTime.of(2023, 1, 1, it, 0, 0)
        },
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