package com.example.mytodoapp.features.task.createbottomsheet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.example.mytodoapp.MyTodoApp
import com.example.mytodoapp.abstracts.BaseBottomSheet
import com.example.mytodoapp.database.entities.Task
import com.example.mytodoapp.databinding.BottomSheetCreateTaskBinding
import com.google.android.material.checkbox.MaterialCheckBox

class CreateTaskModalBottomSheet : BaseBottomSheet() {

    private var binding: BottomSheetCreateTaskBinding? = null

    private val createTaskBottomSheetViewModel: CreateTaskBottomSheetViewModel by viewModels {
        CreateTaskBottomSheetViewModel.CreateTaskBottomSheetViewModelFactory(
            (requireActivity().application as MyTodoApp).taskRepository
        )
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

            setDatetimeButton.setOnClickListener {
                // TODO
            }

            saveTaskButton.isEnabled = false
            addTaskTitleEditText.doOnTextChanged { text, _, _, _ ->
                saveTaskButton.isEnabled = text.toString().trim().isNotEmpty()
            }

            saveTaskButton.setOnClickListener {
                val task = Task(
                    title = addTaskTitleEditText.text.toString(),
                    details = addTaskDetailsEditText.text.toString(),
                    //TODO
//                    groupID =
                    isStared = setTaskStaredCheckBox.isChecked,
                    //TODO
//                    dueDate =
                )

                createTaskBottomSheetViewModel.insert(task)

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