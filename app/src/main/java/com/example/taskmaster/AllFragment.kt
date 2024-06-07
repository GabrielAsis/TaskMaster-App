package com.example.taskmaster

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmaster.databinding.FragmentAllBinding
import com.example.taskmaster.databinding.ActivityMainBinding

class AllFragment : Fragment() {

    private var _binding: FragmentAllBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivityMainBinding: ActivityMainBinding
    private lateinit var taskViewModel: TaskViewModel

    //adapters
    private lateinit var todayAdapter: TaskItemAdapter
    private lateinit var importantAdapter: TaskItemAdapter
    private lateinit var scheduledAdapter: TaskItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskViewModel = ViewModelProvider(requireActivity()).get(TaskViewModel::class.java)

        setTodayRecyclerView()
        setImportantRecyclerView()
        setScheduledRecyclerView()

        taskViewModel.taskItems.observe(viewLifecycleOwner) {
            checkForTasks()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setTodayRecyclerView() { //today tasks
        val mainActivity = activity as MainActivity
        val tasksDueToday = taskViewModel.getTasksDueToday()
        todayAdapter = TaskItemAdapter(tasksDueToday, mainActivity, taskViewModel)
        binding.todayTasksRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.todayTasksRecyclerView.adapter = todayAdapter

        taskViewModel.taskItems.observe(viewLifecycleOwner) {
            val todayTasks = taskViewModel.getTasksDueToday()
            todayAdapter.updateTasks(todayTasks)
            binding.todayText.visibility = if (todayTasks.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setImportantRecyclerView() {  //important tasks
        val mainActivity = activity as MainActivity
        val tasksImportant = taskViewModel.getTasksImportant()
        importantAdapter = TaskItemAdapter(tasksImportant, mainActivity, taskViewModel)
        binding.importantTasksRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.importantTasksRecyclerView.adapter = importantAdapter

        // Observe task items and update RecyclerView
        taskViewModel.taskItems.observe(viewLifecycleOwner) {
            val importantTasks = taskViewModel.getTasksImportant()
            importantAdapter.updateTasks(importantTasks)
            binding.importantText.visibility = if (importantTasks.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setScheduledRecyclerView() { //scheduled tasks
        val mainActivity = activity as MainActivity
        val tasksScheduled = taskViewModel.getTasksScheduled()
        scheduledAdapter = TaskItemAdapter(tasksScheduled, mainActivity, taskViewModel)
        binding.scheduledTasksRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.scheduledTasksRecyclerView.adapter = scheduledAdapter

        // Observe task items and update RecyclerView
        taskViewModel.taskItems.observe(viewLifecycleOwner) {
            val scheduledTasks = taskViewModel.getTasksScheduled()
            scheduledAdapter.updateTasks(scheduledTasks)
            binding.scheduledText.visibility = if (scheduledTasks.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun checkForTasks() {
        val noTasks = taskViewModel.taskItems.value?.isEmpty() ?: true
        binding.noTasksText.visibility = if (noTasks) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
