package com.example.taskmaster

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID


class TaskItem(
    var name: String = "",
    var dueTime: LocalTime? = null,
    var dueDate: LocalDate? = null,
    var completedDate: LocalDate? = null,
    var important: Boolean = false,
    var category: String = "",
    var id: UUID = UUID.randomUUID(),
    var isNewlyAdded: Boolean = true

) {

    fun isCompleted() = completedDate != null

    fun imageResourceComplete(): Int = if (isCompleted()) R.drawable.checked else R.drawable.unchecked

    fun imageResourceImportant(): Int = if (important) R.drawable.important else R.drawable.unimportant


}