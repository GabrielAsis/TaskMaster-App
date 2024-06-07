package com.example.taskmaster

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmaster.databinding.TaskItemCellBinding
import java.util.UUID



class TaskItemAdapter(
    private var taskItems: MutableList<TaskItem>,
    private val clickListener: TaskItemClickListener,
    private val taskViewModel: TaskViewModel
): RecyclerView.Adapter<TaskItemViewHolder>() {
    private val animatedItems = mutableSetOf<UUID>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = TaskItemCellBinding.inflate(from, parent, false)
        return TaskItemViewHolder(parent.context, binding, clickListener, taskViewModel, this)
    }

    override fun getItemCount(): Int = taskItems.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TaskItemViewHolder, position: Int) {
        holder.bindTaskItem(taskItems[position])
    }

    fun updateTasks(newTaskItems: MutableList<TaskItem>) {
        taskItems = newTaskItems
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean {
        return taskItems.isEmpty()
    }


}

