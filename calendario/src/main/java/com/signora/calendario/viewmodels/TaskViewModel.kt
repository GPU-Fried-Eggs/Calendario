package com.signora.calendario.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import com.signora.calendario.models.CalendarPeriod
import com.signora.calendario.models.Task

class TaskViewModel : ViewModel() {
    var taskPlanList = mutableStateListOf<Task>()

    var taskDoneList = mutableStateListOf<Task>()

    var scalarText = mutableStateListOf<String>()

    var boardSize = mutableStateOf(Size.Unspecified)

    val period by mutableStateOf(CalendarPeriod.WEEK)

    fun onIntent() {

    }
}