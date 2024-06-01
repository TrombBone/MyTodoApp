package com.example.mytodoapp.features.ui.edittask

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.mytodoapp.R
import com.example.mytodoapp.components.abstracts.BaseFragment
import com.example.mytodoapp.components.extensions.setStrikeThroughEffect
import com.example.mytodoapp.databinding.FragmentEditTaskBinding
import com.example.mytodoapp.features.database.converters.DateTimeConverter
import com.example.mytodoapp.features.database.entities.Task
import com.example.mytodoapp.features.database.entities.TasksGroup
import com.example.mytodoapp.features.ui.datetime.DateTimePickerDialog
import com.example.mytodoapp.features.ui.edittask.choosegroup.ChooseGroupBottomSheet
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@AndroidEntryPoint
class EditTaskFragment : BaseFragment() {
    private var allGroups = listOf(TasksGroup("1", "My Tasks"))

    private var _binding: FragmentEditTaskBinding? = null
    private val binding get() = _binding!!

    private val editTaskViewModel: EditTaskViewModel by viewModels()

    private var date: LocalDate? = null
    private var time: LocalTime? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            takeIf { it.containsKey(Task.EXTRA_TASK) }?.apply {
                Task.fromBundle(getBundle(Task.EXTRA_TASK)!!)?.also { task ->
                    editTaskViewModel.setTask(task)
                }
            }
            takeIf { it.containsKey(ARG_ALL_GROUPS) }?.apply {
                val groups = (
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            getParcelableArrayList(ARG_ALL_GROUPS, TasksGroup::class.java)
                        else
                            getParcelableArrayList<TasksGroup>(ARG_ALL_GROUPS)
                        )!!.toList()
                editTaskViewModel.setAllGroups(groups)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.navigationBarColor =
            binding.editTaskBottomAppbar.backgroundTint?.defaultColor
                ?: requireActivity().window.navigationBarColor

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                editTaskViewModel.groups.collect { groups ->
                    allGroups = groups
                }
            }
        }

        // Update UI
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                editTaskViewModel.task.collect { task ->
                    binding.root.transitionName = TRANSITION_ELEMENT_ROOT + task.taskID

                    with(task) {
                        binding.editToolbar.menu.getItem(0).setIcon(
                            if (isStared) R.drawable.ic_star_fill_24 else R.drawable.sl_stared_24dp
                        )

                        binding.chooseTaskGroupButton.text =
                            allGroups.find { groupID == it.taskGroupID }
                                ?.groupTitle ?: "Group not found"

                        if (binding.editTaskTitleEditText.text.toString() != title)
                            binding.editTaskTitleEditText.setText(title)
                        binding.editTaskTitleEditText.setStrikeThroughEffect(isFinished)

                        if (hasDetails()) {
                            if (binding.editTaskDetailsEditText.text.toString() != details)
                                binding.editTaskDetailsEditText.setText(details)
                            binding.editTaskDetailsEditText.setStrikeThroughEffect(isFinished)
                        }

                        binding.readyTaskFloatingActionButton.text = resources.getString(
                            if (isFinished) R.string.mark_task_not_ready_yet
                            else R.string.mark_task_ready
                        )

                        binding.readyTaskFloatingActionButton.setOnClickListener {
                            editTaskViewModel.setFinished(!isFinished)
                            binding.readyTaskFloatingActionButton.text = resources.getString(
                                if (!isFinished) R.string.mark_task_ready
                                else R.string.mark_task_not_ready_yet
                            )
                            binding.editTaskTitleEditText.setStrikeThroughEffect(!isFinished)
                            if (hasDetails())
                                binding.editTaskDetailsEditText.setStrikeThroughEffect(!isFinished)
                            if (!isFinished)
                                navigateUpToTasksFragment()
                        }

                        // TODO: Make Chip, no just button text
                        with(binding.editDateTimeButton) {
                            if (hasDueDate()) text = task.formatDueDateTime(requireContext())
                            setOnClickListener { showDateTimePickerDialog(task) }
                        }

                        binding.editToolbar.setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {
                                R.id.stared -> {
                                    editTaskViewModel.setStared(!isStared)
                                    menuItem.setIcon(
                                        if (!isStared) R.drawable.ic_star_fill_24
                                        else R.drawable.sl_stared_24dp
                                    )
                                    true
                                }

                                R.id.delete -> {
                                    showConfirmDeleteDialog(this)
                                    true
                                }

                                else -> false
                            }
                        }
                    }
                }
            }
        }

        with(binding) {
            editTaskTitleEditText.doOnTextChanged { text, _, _, _ ->
                editTaskViewModel.setTitle(text?.toString())
            }

            editTaskDetailsEditText.doOnTextChanged { text, _, _, _ ->
                editTaskViewModel.setDetails(text?.toString())
            }

            editToolbar.setNavigationOnClickListener {
                navigateUpToTasksFragment()
            }

            chooseTaskGroupButton.setOnClickListener {
                showChooseGroupBottomSheet()
            }
        }
    }

    private fun showConfirmDeleteDialog(toDeleteTask: Task) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.alert_delete_task_title))
            .setMessage(resources.getString(R.string.alert_delete_task_message))
            .setNeutralButton(resources.getString(R.string.alert_cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.alert_confirm_button)) { dialog, _ ->
                editTaskViewModel.delete(toDeleteTask)
                editTaskViewModel.dismissNotificationOnDeletedTask(toDeleteTask)
                navigateUpToTasksFragment()
                dialog.dismiss()
            }
            .show()
    }

    private fun showChooseGroupBottomSheet() {
        val chooseGroupBottomSheet =
            ChooseGroupBottomSheet.newInstance()
        chooseGroupBottomSheet.show(
            childFragmentManager,
            ChooseGroupBottomSheet.TAG
        )
    }

    private fun showDateTimePickerDialog(task: Task) {
        DateTimePickerDialog.newInstance(task.dueDate, task.dueTime).show(
            childFragmentManager,
            DateTimePickerDialog.TAG
        )

        childFragmentManager.setFragmentResultListener(
            DateTimePickerDialog.KEY_RESULT_FROM_DATETIME,
            this
        ) { _, bundle ->
            date =
                DateTimeConverter.toLocalDate(bundle.getString(DateTimePickerDialog.KEY_DATE))
            time =
                DateTimeConverter.toLocalTime(bundle.getString(DateTimePickerDialog.KEY_TIME))

            editTaskViewModel.setDueDate(date)
            editTaskViewModel.setDueTime(time)
        }
    }

    private fun navigateUpToTasksFragment() {
        findNavController().navigateUp()
    }

    private fun updateData() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                editTaskViewModel.task.collect { task ->
                    editTaskViewModel.update(task)
                }
            }
        }
    }

    override fun onPause() {
        updateData()
        super.onPause()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        const val ARG_ALL_GROUPS = "ARG_ALL_GROUPS"

        @JvmStatic
        fun newInstance(task: Task, allGroups: List<TasksGroup>) =
            EditTaskFragment().apply {
                arguments = Bundle().apply {
                    putBundle(Task.EXTRA_TASK, Task.toBundle(task))
                    putParcelableArrayList(ARG_ALL_GROUPS, ArrayList<TasksGroup>(allGroups))
                }
            }
    }
}