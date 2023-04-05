package com.signora.calendario.ui.kanban

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.signora.calendario.R
import com.signora.calendario.models.KanbanIntent
import com.signora.calendario.models.KanbanLayout.GRID
import com.signora.calendario.models.KanbanLayout.LIST
import java.time.LocalDateTime

@Composable
fun Kanban(state: KanbanState) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    state.onIntent(KanbanIntent.UseGridLayout)
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_outline_kanban),
                    contentDescription = stringResource(R.string.kanban_grid_icon_description)
                )
            }
            Box(
                modifier = Modifier
                    .background(Color.Black)
                    .fillMaxHeight(0.6f)  //fill the max height
                    .width(1.dp),
            )
            IconButton(
                onClick = {
                    state.onIntent(KanbanIntent.UseListLayout)
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_outline_checklist),
                    contentDescription = stringResource(R.string.kanban_list_icon_description)
                )
            }
        }

        when (state.layout) {
            LIST -> {
            }
            GRID -> {
                KanbanGrid(
                    timeScalar = (0..23).map {
                        LocalDateTime.of(2023, 1, 1, it, 0, 0)
                    },
                    taskPlanList = state.taskPlanList,
                    taskDoneList = state.taskDoneList
                )
            }
        }
    }
}

@Preview
@Composable
private fun KanbanPreview() {
    val kanbanState = rememberSaveable(saver = KanbanState.Saver) { KanbanState() }

    Kanban(state = kanbanState)
}