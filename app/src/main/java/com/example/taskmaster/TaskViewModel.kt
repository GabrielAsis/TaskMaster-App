package com.example.taskmaster

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.window.application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import android.app.Application
import android.content.SharedPreferences
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.google.gson.reflect.TypeToken
import com.google.gson.*
import java.lang.reflect.Type

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val taskItemsKey = "taskItems"
    private val completedTaskItemsKey = "completedTaskItems"
    private val sharedPreferences: SharedPreferences = application.getSharedPreferences("TaskManagerPrefs", Context.MODE_PRIVATE)
    private lateinit var taskItemViewHolder: TaskItemViewHolder

    @RequiresApi(Build.VERSION_CODES.O)
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalTime::class.java, object : JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {
            override fun serialize(src: LocalTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(src.toString())
            }

            override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalTime {
                return try {
                    LocalTime.parse(json?.asString)
                } catch (e: Exception) {
                    LocalTime.MIN // Default value or handle accordingly
                }
            }
        })
        .registerTypeAdapter(LocalDate::class.java, object : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
            override fun serialize(src: LocalDate?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(src.toString())
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDate {
                return try {
                    LocalDate.parse(json?.asString)
                } catch (e: Exception) {
                    LocalDate.MIN // Default value or handle accordingly
                }
            }
        })
        .create()

    @RequiresApi(Build.VERSION_CODES.O)
    private val _taskItems = MutableLiveData<MutableList<TaskItem>>().apply { value = loadTasks() }
    @SuppressLint("NewApi")
    val taskItems: LiveData<MutableList<TaskItem>> = _taskItems

    @SuppressLint("NewApi")
    private val _completedTaskItems = MutableLiveData<MutableList<TaskItem>>().apply { value = loadCompletedTasks() }
    val completedTaskItems: LiveData<MutableList<TaskItem>> = _completedTaskItems

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadTasks(): MutableList<TaskItem> {
        val taskItemsJson = sharedPreferences.getString(taskItemsKey, null)
        return try {
            if (!taskItemsJson.isNullOrEmpty()) {
                gson.fromJson(taskItemsJson, object : TypeToken<MutableList<TaskItem>>() {}.type)
            } else {
                mutableListOf()
            }
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            mutableListOf()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveTasks(taskItems: List<TaskItem>) {
        val taskItemsJson = gson.toJson(taskItems)
        sharedPreferences.edit().putString(taskItemsKey, taskItemsJson).apply()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadCompletedTasks(): MutableList<TaskItem> {
        val json = sharedPreferences.getString(completedTaskItemsKey, null)
        return if (json != null) {
            try {
                gson.fromJson(json, object : TypeToken<MutableList<TaskItem>>() {}.type)
            } catch (e: JsonSyntaxException) {
                mutableListOf()
            }
        } else {
            mutableListOf()
        }
    }

    @SuppressLint("NewApi")
    fun addTaskItem(newTask: TaskItem) {
        val list = _taskItems.value ?: mutableListOf()
        list.add(newTask)
        _taskItems.postValue(list)
        saveTasks(list)
        Toast.makeText(getApplication(), "Task Added!", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("NewApi")
    fun updateTaskItem(id: UUID, name: String, dueTime: LocalTime?, dueDate: LocalDate?, category: String) {
        val list = _taskItems.value ?: mutableListOf()
        val task = list.find { it.id == id }
        Toast.makeText(getApplication(), "Task Updated!", Toast.LENGTH_SHORT).show()
        if (task != null) {
            task.name = name
            task.dueTime = dueTime
            task.dueDate = dueDate
            task.category = category
            _taskItems.postValue(list)
            saveTasks(list)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun setCompleted(taskItem: TaskItem) {
        val taskList = _taskItems.value ?: mutableListOf()
        val task = taskList.find { it.id == taskItem.id }
        if (task != null) {
            taskList.remove(task)
            _taskItems.postValue(taskList)

            val completedList = _completedTaskItems.value ?: mutableListOf()
            completedList.add(task.apply { completedDate = LocalDate.now() })
            _completedTaskItems.postValue(completedList)

            saveTasks(taskList)
            saveCompletedTasks(completedList)
        }
    }


    @SuppressLint("NewApi")
    private fun saveCompletedTasks(completedTasks: MutableList<TaskItem>) {
        val json = gson.toJson(completedTasks)
        sharedPreferences.edit().putString(completedTaskItemsKey, json).apply()
    }

    @SuppressLint("NewApi")
    fun setImportant(taskItem: TaskItem) {
        val list = _taskItems.value ?: mutableListOf()
        val task = list.find { it.id == taskItem.id }
        if (task != null) {
            task.important = !task.important
            _taskItems.postValue(list)
            saveTasks(list)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTasksDueToday(): MutableList<TaskItem> {
        val today = LocalDate.now()
        return (_taskItems.value?.filter { it.dueDate == today } ?: emptyList()).toMutableList()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTasksScheduled(): MutableList<TaskItem> {
        val today = LocalDate.now()
        return (_taskItems.value?.filter { it.dueDate != today && it.dueDate != null } ?: emptyList()).toMutableList()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTasksImportant(): MutableList<TaskItem> {
        return (_taskItems.value?.filter { it.important } ?: emptyList()).toMutableList()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTasksByCategory(categoryName: String): MutableList<TaskItem> {
        return (_taskItems.value?.filter { it.category == categoryName } ?: emptyList()).toMutableList()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCompletedTasks(): MutableList<TaskItem> {
        return (_completedTaskItems.value ?: emptyList()).toMutableList()
    }

    fun deleteTask(task: TaskItem) {
        val completedTasks = _completedTaskItems.value?.toMutableList() ?: mutableListOf()
        completedTasks.remove(task)
        _completedTaskItems.value = completedTasks
        saveCompletedTasks(completedTasks)
    }

}
