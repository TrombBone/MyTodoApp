package com.example.mytodoapp.features.task.edit

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.mytodoapp.R
import com.example.mytodoapp.abstracts.BaseFragment
import com.example.mytodoapp.database.entities.Task
import com.example.mytodoapp.database.entities.TasksGroup
import com.example.mytodoapp.databinding.FragmentEditTaskBinding
import com.example.mytodoapp.extensions.setStrikeThroughEffect
import com.example.mytodoapp.features.task.group.GroupsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditTaskFragment : BaseFragment() {

    private var _binding: FragmentEditTaskBinding? = null
    private val binding get() = _binding!!

    private val editTaskViewModel: EditTaskViewModel by viewModels()

    private var taskGroups = listOf<TasksGroup>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.apply {
            takeIf { it.containsKey(Task.EXTRA_TASK) }?.apply {
                Task.fromBundle(getBundle(Task.EXTRA_TASK)!!)?.also { task ->
                    editTaskViewModel.setTask(task)
                    binding.root.transitionName = TRANSITION_ELEMENT_ROOT + task.taskID
                }
            }
            takeIf { it.containsKey(ARG_KEY_ALL_GROUPS) }?.apply {
                val groups = (
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            getParcelableArrayList(ARG_KEY_ALL_GROUPS, TasksGroup::class.java)
                        else
                            getParcelableArrayList<TasksGroup>(ARG_KEY_ALL_GROUPS)
                        )!!.toList()
                editTaskViewModel.setAllGroups(groups)
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                editTaskViewModel.groups.collect { groups ->
                    taskGroups = groups
                }
            }
        }

        // Update UI
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                editTaskViewModel.task.collect { task ->
                    with(task) {
                        binding.editToolbar.menu.getItem(0).setIcon(
                            if (isStared) R.drawable.ic_star_fill_24 else R.drawable.sl_stared_24dp
                        )

                        binding.chooseTaskGroupButton.text =
                            taskGroups.find { task.groupID == it.taskGroupID }
                                ?.groupTitle ?: "Group not found"

                        binding.editTaskTitleEditText.setText(title)
                        binding.editTaskTitleEditText.setStrikeThroughEffect(isFinished)

                        if (hasDetails()) {
                            binding.editTaskDetailsEditText.setText(details)
                            binding.editTaskTitleEditText.setStrikeThroughEffect(isFinished)
                        }

                        // FIXME: Text from res
                        binding.readyTaskFloatingActionButton.text =
                            if (isFinished) "Task no ready yet" else "Task ready"

                        // TODO: Date
//                        if (hasDueDate()) {
//                            binding.editDateTimeButton
//                        }

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
                                    // TODO: create and call confirm delete dialog
                                    editTaskViewModel.delete(this)
                                    navigateToTasksFragment()
                                    true
                                }
                                else -> false
                            }
                        }
                    }
                }
            }
        }

        binding.editToolbar.setNavigationOnClickListener {
            navigateToTasksFragment()
        }

        binding.chooseTaskGroupButton.setOnClickListener {
            showChooseGroupBottomSheet()
        }
    }

    override fun onPause() {
        super.onPause()
        saveAndUpdateData()
    }

    private fun showChooseGroupBottomSheet() {
        // TODO: create and show ChooseGroupBottomSheetFragment()
    }

    private fun navigateToTasksFragment() {
        findNavController().navigate(R.id.action_edit_task_fragment_to_tasks_fragment)
        saveAndUpdateData()
    }

    private fun saveAndUpdateData() {
        // TODO: save and update data
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        val ARG_KEY_ALL_GROUPS = "${this::class.java.name}_ARG_KEY_ALL_GROUPS"
    }
}