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

private const val ARG_SELECTED_GROUP_ID = "ARG_SELECTED_GROUP_ID"

@AndroidEntryPoint
class CreateTaskBottomSheetFragment : BaseBottomSheet() {
    private var selectedGroupID: String? = null

    private var _binding: BottomSheetCreateTaskBinding? = null
    private val binding get() = _binding!!

    private val createTaskBottomSheetViewModel: CreateTaskBottomSheetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            takeIf { it.containsKey(ARG_SELECTED_GROUP_ID) }?.apply {
                selectedGroupID = getString(ARG_SELECTED_GROUP_ID)!!
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCreateTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            addTaskTitleEditText.requestFocus()

            showDetailsFieldButton.setOnClickListener {
                addTaskDetailsEditText.isVisible = !addTaskDetailsEditText.isVisible
            }

            setDatetimeButton.setOnClickListener {
                // TODO
            }

            saveTaskButton.isEnabled = false
            addTaskTitleEditText.doOnTextChanged { text, _, _, _ ->
                saveTaskButton.isEnabled = !(text?.trim().isNullOrEmpty())
            }

            saveTaskButton.setOnClickListener {
                val task = Task(
                    title = if (addTaskTitleEditText.text?.trim().isNullOrEmpty()) null
                    else addTaskTitleEditText.text!!.trim().toString(),
                    details = if (addTaskDetailsEditText.text?.trim().isNullOrEmpty()) null
                    else addTaskDetailsEditText.text!!.trim().toString(),
                    groupID = selectedGroupID ?: "1",
                    isStared = setTaskStaredCheckBox.isChecked,
                    //TODO
//                    dueDate =
                )

                createTaskBottomSheetViewModel.insert(task)

                this@CreateTaskBottomSheetFragment.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        val TAG: String = this::class.java.name

        @JvmStatic
        fun newInstance(selectedGroupID: String) =
            CreateTaskBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SELECTED_GROUP_ID, selectedGroupID)
                }
            }
    }
}