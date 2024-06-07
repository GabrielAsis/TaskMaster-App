package com.example.taskmaster

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Paint
import android.os.Build
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmaster.databinding.FragmentNewTaskSheetBinding
import com.example.taskmaster.databinding.TaskItemCellBinding
import java.time.DateTimeException
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class TaskItemViewHolder(
    private val context: Context,
    private val binding: TaskItemCellBinding,
    private val clickListener: TaskItemClickListener,
    private val taskViewModel: TaskViewModel,
    private val adapter: TaskItemAdapter
): RecyclerView.ViewHolder(binding.root)
{
    @RequiresApi(Build.VERSION_CODES.O)
    val timeFormat = DateTimeFormatter.ofPattern("HH:mm")

    @RequiresApi(Build.VERSION_CODES.O)
    val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM dd")


    @RequiresApi(Build.VERSION_CODES.O)
    fun bindTaskItem(taskItem: TaskItem) {
        binding.name.text = taskItem.name

        if (taskItem.category.isNullOrEmpty() || taskItem.category == "None") {
            binding.category.visibility = View.GONE
        } else {
            binding.category.visibility = View.VISIBLE
            binding.category.text = "${taskItem.category} • "
        }

        if (taskItem.isCompleted()){
            binding.name.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.dueTime.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.dueDate.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.category.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

        }

        binding.completeButton.setImageResource(taskItem.imageResourceComplete())

        binding.completeButton.setOnClickListener{
            clickListener.completeTaskItem(taskItem)
            Toast.makeText(itemView.context, "Task Completed!", Toast.LENGTH_SHORT).show()
        }


        binding.importantButton.setImageResource(taskItem.imageResourceImportant())

        binding.importantButton.setOnClickListener{
            clickListener.importantTaskItem(taskItem)
        }

        binding.taskCellContainer.setOnClickListener {
            clickListener.editTaskItem(taskItem)
        }

        if (taskItem.isCompleted()) {
            binding.completeButton.visibility = View.GONE
            binding.importantButton.visibility = View.GONE
            binding.deleteButton.visibility = View.VISIBLE
        } else {
            binding.completeButton.visibility = View.VISIBLE
            binding.importantButton.visibility = View.VISIBLE
            binding.deleteButton.visibility = View.GONE
        }

        if(taskItem.dueTime != null) {
            binding.dueTime.text = "${timeFormat.format(taskItem.dueTime)} • "
        } else {
            binding.dueTime.text = ""
        }

        if (taskItem.dueDate != null) {
            val formattedDate = dateFormat.format(taskItem.dueDate)
            binding.dueDate.text = formattedDate
        } else {
            binding.dueDate.text = ""
        }

        binding.deleteButton.setOnClickListener {
            taskViewModel.deleteTask(taskItem)
            adapter.updateTasks(taskViewModel.getCompletedTasks())
            Toast.makeText(itemView.context, "Task deleted", Toast.LENGTH_SHORT).show()
        }

    }

}