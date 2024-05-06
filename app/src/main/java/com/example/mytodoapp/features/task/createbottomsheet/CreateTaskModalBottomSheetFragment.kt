package com.example.mytodoapp.features.task.createbottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.example.mytodoapp.abstracts.BaseBottomSheet
import com.example.mytodoapp.database.entities.Task
import com.example.mytodoapp.databinding.BottomSheetCreateTaskBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateTaskModalBottomSheet : BaseBottomSheet() {

    private var _binding: BottomSheetCreateTaskBinding? = null
    private val binding get() = _binding!!

    private val createTaskBottomSheetViewModel: CreateTaskBottomSheetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCreateTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            showDetailsFieldButton.setOnClickListener {
                addTaskDetailsEditText.isVisible = !addTaskDetailsEditText.isVisible
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
                    details = if (addTaskDetailsEditText.text.isNullOrEmpty()) null
                    else addTaskDetailsEditText.text.toString(),
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
        _binding = null
    }

    companion object {
        val TAG: String = this::class.java.name
    }
}