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
import com.example.taskmaster.databinding.FragmentCompletedBinding

class CompletedFragment : Fragment() {

    private var _binding: FragmentCompletedBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var adapter: TaskItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCompletedBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskViewModel = ViewModelProvider(requireActivity()).get(TaskViewModel::class.java)

        setRecyclerView()

        taskViewModel.completedTaskItems.observe(viewLifecycleOwner) {
            checkForTasks()
            updateRecyclerView()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setRecyclerView() {
        val mainActivity = activity as MainActivity
        val tasksCompleted = taskViewModel.getCompletedTasks()
        adapter = TaskItemAdapter(tasksCompleted, mainActivity, taskViewModel)
        binding.completedTasksRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.completedTasksRecyclerView.adapter = adapter

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateRecyclerView() {
        val completedTasks = taskViewModel.getCompletedTasks()
        adapter.updateTasks(completedTasks)
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
