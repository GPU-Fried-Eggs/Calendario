package com.signora.calendario.kanban.models

import androidx.compose.ui.graphics.Color
import com.signora.calendario.kanban.enums.KanbanTaskType
import java.time.LocalDateTime

data class KanbanTaskRange(
    val plan: Pair<LocalDateTime, LocalDateTime>,
    val done: Pair<LocalDateTime, LocalDateTime>? = null
) {
    operator fun get(key: KanbanTaskType): Pair<LocalDateTime, LocalDateTime>? {
        return when (key) {
            KanbanTaskType.PLAN -> this.plan
            KanbanTaskType.DONE -> this.done
        }
    }
}

interface KanbanTask {
    val range: KanbanTaskRange
    /** The color of kanban task. */
    val color: Color
    /** The payload of kanban task, should be always same */
    val payload: String

    val state: Boolean?
    /**
     * Generate a updated task based on state. Will add or remove dene date in range.
     * @return A shallow copy of target Kanban Task
     */
    fun formatTask(state: Boolean): KanbanTask
}

internal data class DefaultKanbanTask(
    override val range: KanbanTaskRange,
    override val color: Color = Color(0xFFFFCDD2),
    override val payload: String
) : KanbanTask {
    override val state: Boolean
        get() = range.done != null

    override fun formatTask(state: Boolean): DefaultKanbanTask {
        return this.copy(
            range = when (state) {
                true -> this.range.copy(done = this.range.plan)
                false -> this.range.copy(done = null)
            }
        )
    }
}

internal data class NestedKanbanTask(
    override val range: KanbanTaskRange,
    override val color: Color = Color(0xFFFFCDD2),
    override val payload: String,
    val children: List<ChildKanbanTask>
) : KanbanTask {
    override val state: Boolean?
        get() = (if (children.isNotEmpty()) {
            when {
                children.all { it.state } -> true
                children.none { it.state } -> false
                else -> null
            }
        } else range.done != null)

    internal data class ChildKanbanTask(
        override var range: KanbanTaskRange,
        override val color: Color = Color(0xFFFFCDD2),
        override val payload: String
    ) : KanbanTask {
        override val state: Boolean
            get() = range.done != null

       override fun formatTask(state: Boolean): ChildKanbanTask {
           return this.copy(
               range = when (state) {
                   true -> this.range.copy(done = this.range.plan)
                   false -> this.range.copy(done = null)
               }
           )
       }
    }

    override fun formatTask(state: Boolean): NestedKanbanTask {
        return this.copy(
            range = when (state) {
                true -> this.range.copy(done = this.range.plan)
                false -> this.range.copy(done = null)
            },
            children = this.children.map {
                it.formatTask(state)
            }
        )
    }
}