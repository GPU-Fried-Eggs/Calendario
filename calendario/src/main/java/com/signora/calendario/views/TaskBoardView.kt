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
import com.signora.calendario.models.Task
import com.signora.calendario.utils.getOffsetSize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalTextApi::class)
@Composable
fun TaskBoardView(
    modifier: Modifier = Modifier,
    timeScalar: List<LocalDateTime>,
    taskPlanList: List<Task>,
    taskDoneList: List<Task>
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
private fun TaskBoardPreview() {
    // Red, Pink, Purple, Deep Purple, Indigo, Blue, Light Blue, Cyan, Teal, Green,
    // Light Green, Lime, Yellow, Amber, Orange, Deep Orange, Brown, Blue Grey
    val material100 = arrayOf(
        0xFFFFCDD2, 0xFFF8BBD0, 0xFFE1BEE7, 0xFFD1C4E9, 0xFFC5CAE9, 0xFFBBDEFB,
        0xFFB3E5FC, 0xFFB2EBF2, 0xFFB2DFDB, 0xFFC8E6C9, 0xFFDCEDC8, 0xFFF0F4C3,
        0xFFFFF9C4, 0xFFFFECB3, 0xFFFFE0B2, 0xFFFFCCBC, 0xFFD7CCC8, 0xFFCFD8DC
    )

    TaskBoardView(
        timeScalar = (0..23).map {
            LocalDateTime.of(2023, 1, 1, it, 0, 0)
        },
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