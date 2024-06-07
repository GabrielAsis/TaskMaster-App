package com.example.taskmaster

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmaster.databinding.FragmentScheduledBinding

class ScheduledFragment : Fragment() {

    private var _binding: FragmentScheduledBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var adapter: TaskItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScheduledBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskViewModel = ViewModelProvider(requireActivity()).get(TaskViewModel::class.java)

        setRecyclerView()

        taskViewModel.taskItems.observe(viewLifecycleOwner) {
            checkForTasks()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setRecyclerView() {
        val mainActivity = activity as MainActivity
        val tasksScheduled = taskViewModel.getTasksScheduled()
        adapter = TaskItemAdapter(tasksScheduled, mainActivity, taskViewModel)
        binding.scheduledTasksRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.scheduledTasksRecyclerView.adapter = adapter

        taskViewModel.taskItems.observe(viewLifecycleOwner) {
            val scheduledTasks = taskViewModel.getTasksScheduled()
            adapter.updateTasks(scheduledTasks)
        }
    }

    private fun checkForTasks() {
        val noTasks = adapter.isEmpty()
        binding.noTasksText.visibility = if (noTasks) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
