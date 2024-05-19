package com.example.mytodoapp.features.task.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mytodoapp.components.abstracts.BaseBottomSheet
import com.example.mytodoapp.databinding.BottomSheetCreateTaskBinding
import com.example.mytodoapp.features.database.converters.DateTimeConverter
import com.example.mytodoapp.features.database.entities.Task
import com.example.mytodoapp.features.datetime.DateTimePickerDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

private const val ARG_SELECTED_GROUP_ID = "ARG_SELECTED_GROUP_ID"

@AndroidEntryPoint
class CreateTaskBottomSheetFragment : BaseBottomSheet() {
    private var selectedGroupID: String? = null

    private var _binding: BottomSheetCreateTaskBinding? = null
    private val binding get() = _binding!!

    private val createTaskBottomSheetViewModel: CreateTaskBottomSheetViewModel by viewModels()

    private var date: LocalDate? = null
    private var time: LocalTime? = null

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

            saveTaskButton.isEnabled = false
            addTaskTitleEditText.doOnTextChanged { text, _, _, _ ->
                createTaskBottomSheetViewModel.setTitle(text?.toString())
                saveTaskButton.isEnabled = !(text?.trim().isNullOrEmpty())
            }

            addTaskDetailsEditText.doOnTextChanged { text, _, _, _ ->
                createTaskBottomSheetViewModel.setDetails(text?.toString())
            }

            createTaskBottomSheetViewModel.setGroup(selectedGroupID ?: "1")
            createTaskBottomSheetViewModel.setStared(setTaskStaredCheckBox.isChecked)

            showDetailsFieldButton.setOnClickListener {
                addTaskDetailsEditText.isVisible = !addTaskDetailsEditText.isVisible
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                createTaskBottomSheetViewModel.task.collect { task ->

                    with(binding.setDatetimeButton) {
                        text = task.formatDueDateTime(requireContext())
                        setOnClickListener { showDateTimePickerDialog(task) }
                    }

                    binding.saveTaskButton.setOnClickListener {
                        createTaskBottomSheetViewModel.insert(task)
                        this@CreateTaskBottomSheetFragment.dismiss()
                    }
                }
            }


        }
    }

    private fun showDateTimePickerDialog(task: Task) {
        DateTimePickerDialogFragment.newInstance(task.dueDate, task.dueTime).show(
            childFragmentManager,
            DateTimePickerDialogFragment.TAG
        )

        childFragmentManager.setFragmentResultListener(
            DateTimePickerDialogFragment.KEY_RESULT_FROM_DATETIME,
            this
        ) { _, bundle ->
            date =
                DateTimeConverter.toLocalDate(bundle.getString(DateTimePickerDialogFragment.KEY_DATE))
            time =
                DateTimeConverter.toLocalTime(bundle.getString(DateTimePickerDialogFragment.KEY_TIME))

            createTaskBottomSheetViewModel.setDueDate(date)
            createTaskBottomSheetViewModel.setDueTime(time)
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