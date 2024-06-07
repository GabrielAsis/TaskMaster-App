package com.example.taskmaster

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.example.taskmaster.databinding.ActivityMainBinding
import com.example.taskmaster.databinding.FragmentNewTaskSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class NewTaskSheet(var taskItem: TaskItem?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewTaskSheetBinding
    private lateinit var taskViewModel: TaskViewModel
    private var dueTime: LocalTime? = null
    private var dueDate: LocalDate? = null

    override fun onResume() {
        super.onResume()
        val categories = resources.getStringArray(R.array.categories)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, categories)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()

        if(taskItem != null)
        {
            binding.newTaskTitle.text = "Edit Task"
            binding.saveButton.text = "Save"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(taskItem!!.name)
            if(taskItem!!.dueTime != null){
                dueTime = taskItem!!.dueTime!!
                updateTimeButtonText()
            }
        } else
        {
            binding.newTaskTitle.text = "New Task"
            binding.saveButton.text = "Add"
        }

        taskViewModel = ViewModelProvider(activity).get(TaskViewModel::class.java)
        binding.saveButton.setOnClickListener{
            saveAction()
        }

        binding.timePickerButton.setOnClickListener{
            openDateTimePicker()
        }

        binding.cancelButton.setOnClickListener{
            closePopup()
        }

        val categories = resources.getStringArray(R.array.categories)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, categories)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openDateTimePicker() {
        if (dueTime == null) dueTime = LocalTime.now()

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                dueDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)

                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    { _, selectedHour, selectedMinute ->
                        dueTime = LocalTime.of(selectedHour, selectedMinute)
                        updateTimeButtonText()
                    },
                    dueTime!!.hour,
                    dueTime!!.minute,
                    true
                )

                timePickerDialog.setTitle("Task Due")
                timePickerDialog.show()
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.setTitle("Select Due Date")
        datePickerDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateTimeButtonText() {
        val dateFormatter = DateTimeFormatter.ofPattern("MMMM dd")
        val formattedDate = dueDate?.format(dateFormatter) ?: "Select Due Date"
        val formattedTime = String.format("%02d:%02d", dueTime!!.hour, dueTime!!.minute)
        binding.timePickerButton.text = "$formattedDate | $formattedTime"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveAction() {
        val name = binding.name.text.toString()
        val category = binding.autoCompleteTextView.text.toString()
        val isValid = validateInput(name, dueDate, dueTime, category)

        if (isValid) {
            val categoryToSave = if (category == "None") "" else category

            if (taskItem == null) {
                val newTask = TaskItem(name, dueTime, dueDate, null, false, categoryToSave)
                taskViewModel.addTaskItem(newTask)
            } else {
                taskViewModel.updateTaskItem(taskItem!!.id, name, dueTime, dueDate, categoryToSave)
            }
            binding.name.setText("")
            dismiss()
        }
    }

    private fun validateInput(name: String, date: LocalDate?, time: LocalTime?, category: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.name.error = "Task name is required"
            isValid = false
        }

        if (date == null) {
            Toast.makeText(requireContext(), "Due date & time is required", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (time == null) {
            Toast.makeText(requireContext(), "Due date & time is required", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }


    private fun closePopup() {
        dismiss()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewTaskSheetBinding.inflate(inflater,container,false)
        return binding.root
    }
}