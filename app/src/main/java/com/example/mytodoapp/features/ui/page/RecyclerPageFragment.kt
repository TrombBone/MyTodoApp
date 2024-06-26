package com.example.mytodoapp.features.ui.page

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mytodoapp.R
import com.example.mytodoapp.components.abstracts.BaseFragment
import com.example.mytodoapp.databinding.ItemTasksRecyclerViewBinding
import com.example.mytodoapp.features.database.converters.DateTimeConverter
import com.example.mytodoapp.features.database.entities.Task
import com.example.mytodoapp.features.database.entities.TasksGroup
import com.example.mytodoapp.features.ui.datetime.DateTimePickerDialog
import com.example.mytodoapp.features.ui.edittask.EditTaskFragment
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalTime

private const val ARG_POSITION = "ARG_POSITION"
private const val ARG_ALL_GROUPS = "ARG_ALL_GROUPS"

@AndroidEntryPoint
class RecyclerPageFragment : BaseFragment(), TaskAdapter.TaskStatusListener {
    private var position = 1
    private var groups: List<TasksGroup> = listOf(TasksGroup("1", "My Tasks"))

    private var _binding: ItemTasksRecyclerViewBinding? = null
    private val binding get() = _binding!!

    private val recyclerPageViewModel: RecyclerPageViewModel by viewModels()

    private val taskAdapter = TaskAdapter(this)

    private var date: LocalDate? = null
    private var time: LocalTime? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            takeIf { it.containsKey(ARG_POSITION) }?.apply {
                position = getInt(ARG_POSITION)
            }
            takeIf { it.containsKey(ARG_ALL_GROUPS) }?.apply {
                groups = (
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            getParcelableArrayList(ARG_ALL_GROUPS, TasksGroup::class.java)
                        else
                            getParcelableArrayList<TasksGroup>(ARG_ALL_GROUPS)
                        )?.toList() ?: listOf()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ItemTasksRecyclerViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        taskAdapter.setHasStableIds(true)
        binding.itemTasksRecycler.adapter = taskAdapter
    }

    override fun onStart() {
        super.onStart()

        recyclerPageViewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            tasks?.let { list ->
                val sortedList = list.sortedBy { it.isFinished }
                taskAdapter.submitList(
                    when (position) {
                        0 -> sortedList.filter { it.isStared }
                        1 -> sortedList
                        else -> sortedList.filter { it.groupID == groups[position - 1].taskGroupID }
                    }
                )

                tasks.forEach { task ->
                    recyclerPageViewModel.setAlarmInFuture(task)
                    recyclerPageViewModel.dismissNotificationOnFinishedTask(task)
                }
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
                    EditTaskFragment.ARG_ALL_GROUPS,
                    ArrayList<TasksGroup>(groups)
                )
            }
        )
    }

    override fun onDueDateTimeChipClick(task: Task) {
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

            onTaskUpdated(task.copy(dueDate = date, dueTime = time))
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(position: Int, allGroups: List<TasksGroup>) =
            RecyclerPageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_POSITION, position)
                    putParcelableArrayList(ARG_ALL_GROUPS, ArrayList<TasksGroup>(allGroups))
                }
            }
    }
}