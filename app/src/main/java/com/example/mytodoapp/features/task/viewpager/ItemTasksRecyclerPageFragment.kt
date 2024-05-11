package com.example.mytodoapp.features.task.viewpager

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mytodoapp.R
import com.example.mytodoapp.abstracts.BaseFragment
import com.example.mytodoapp.database.entities.Task
import com.example.mytodoapp.database.entities.TasksGroup
import com.example.mytodoapp.databinding.ItemTasksRecyclerViewBinding
import com.example.mytodoapp.features.task.TaskAdapter
import com.example.mytodoapp.features.task.edit.EditTaskFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemTasksRecyclerPageFragment : BaseFragment(),
    TaskAdapter.TaskStatusListener, TaskAdapter.OnTaskClickListener {

    private var _binding: ItemTasksRecyclerViewBinding? = null
    private val binding get() = _binding!!

    private val recyclerPageViewModel: RecyclerPageViewModel by viewModels()

    private val taskAdapter = TaskAdapter(this, this)

    private var currentPosition = 1
    private var currentGroups = listOf<TasksGroup>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ItemTasksRecyclerViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.apply {
            takeIf { it.containsKey(ARG_KEY_POSITION) }?.apply {
                currentPosition = getInt(ARG_KEY_POSITION)
            }
            takeIf { it.containsKey(ARG_KEY_ALL_GROUPS) }?.apply {
                currentGroups = (
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            getParcelableArrayList(ARG_KEY_ALL_GROUPS, TasksGroup::class.java)
                        else
                            getParcelableArrayList<TasksGroup>(ARG_KEY_ALL_GROUPS)
                        )?.toList() ?: listOf()
            }
        }

        taskAdapter.setHasStableIds(true)
        binding.itemTasksRecycler.adapter = taskAdapter
    }

    override fun onStart() {
        super.onStart()

        recyclerPageViewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            tasks?.let { list ->
                taskAdapter.submitList(
                    when (currentPosition) {
                        0 -> list.filter { it.isStared }
                        1 -> list
                        else -> list.filter { it.groupID == currentGroups[currentPosition].taskGroupID }
                    }
                )
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onTaskUpdated(task: Task) {
        recyclerPageViewModel.update(task)
    }

    override fun onTaskClick(task: Task) {
        findNavController().navigate(
            R.id.action_tasks_fragment_to_edit_task_fragment,
            Bundle().apply {
                putBundle(Task.EXTRA_TASK, Task.toBundle(task))
                putParcelableArrayList(
                    EditTaskFragment.ARG_KEY_ALL_GROUPS,
                    ArrayList<TasksGroup>(currentGroups)
                )
            }
        )
    }

    companion object {
        val ARG_KEY_POSITION: String = "${this::class.java.name}_ARG_KEY_POSITION"
        val ARG_KEY_ALL_GROUPS: String = "${this::class.java.name}_ARG_KEY_ALL_GROUPS"
    }
}