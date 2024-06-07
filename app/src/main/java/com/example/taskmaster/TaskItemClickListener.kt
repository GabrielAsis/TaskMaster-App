package com.example.taskmaster

import android.os.Build
import androidx.annotation.RequiresApi

private lateinit var taskViewModel: TaskViewModel

interface TaskItemClickListener {
    fun editTaskItem(taskItem: TaskItem)
    fun completeTaskItem(taskItem: TaskItem)
    fun importantTaskItem(taskItem: TaskItem)

}