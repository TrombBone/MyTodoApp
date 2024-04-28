package com.example.mytodoapp.features.task

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.mytodoapp.MyTodoApp
import com.example.mytodoapp.abstracts.BaseBottomSheet
import com.example.mytodoapp.database.entities.Task
import com.example.mytodoapp.databinding.BottomSheetCreateTaskBinding

class CreateTaskModalBottomSheet() : BaseBottomSheet() {

    private var binding: BottomSheetCreateTaskBinding? = null
    // FIXME: create other viewmodel
    private val tasksViewModel: TasksViewModel by viewModels {
        TasksViewModel.TasksViewModelFactory((requireActivity().application as MyTodoApp).taskRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bottomSheetBinding = BottomSheetCreateTaskBinding
            .inflate(inflater, container, false)
        binding = bottomSheetBinding
        return bottomSheetBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding!!) {
            showDetailsFieldButton.setOnClickListener {
                if (addTaskDetailsEditText.visibility == View.VISIBLE)
                    addTaskDetailsEditText.visibility = View.GONE
                else
                    addTaskDetailsEditText.visibility = View.VISIBLE
            }

            saveTaskButton.setOnClickListener {
                val task = Task(
                    title = addTaskTitleEditText.text.toString(),
                    details = addTaskDetailsEditText.text.toString()
                )

                // FIXME: create other viewmodel
                tasksViewModel.insert(task)

                this@CreateTaskModalBottomSheet.dismiss()
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        val TAG: String = this::class.java.name
    }
}