package com.signora.calendario.kanban.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.signora.calendario.kanban.R
import com.signora.calendario.kanban.enums.KanbanLayout

private val DefaultIconSize = 24.dp
private val DefaultPadding = 8.dp

@Composable
fun KanbanHeader(onClick: (KanbanLayout) -> Unit) {
    Row(
        modifier = Modifier
            .height(DefaultIconSize)
            .fillMaxWidth()
            .padding(horizontal = DefaultPadding),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_outline_kanban),
            contentDescription = stringResource(R.string.kanban_grid_icon_description),
            modifier = Modifier
                .padding(horizontal = DefaultPadding)
                .clickable(role = Role.Button) {
                    onClick(KanbanLayout.GRID)
                }
        )
        Box(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxHeight(0.6f)  //fill the max height
                .width(1.6.dp)
        )
        Icon(
            painter = painterResource(R.drawable.ic_outline_checklist),
            contentDescription = stringResource(R.string.kanban_list_icon_description),
            modifier = Modifier
                .padding(horizontal = DefaultPadding)
                .clickable(role = Role.Button) {
                    onClick(KanbanLayout.LIST)
                }
        )
    }
}

@Preview
@Composable
private fun KanbanHeaderPreview() {
    val layout = remember { mutableStateOf(KanbanLayout.GRID) }

    Column {
        KanbanHeader { layout.value = it }
        BasicText(
            text = "layout: ${layout.value}",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle.Default.merge(
                TextStyle(textAlign = TextAlign.Center)
            )
        )
    }
}