package com.example.mytodoapp.features.task.group.choosebottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mytodoapp.components.abstracts.BaseBottomSheet
import com.example.mytodoapp.features.database.entities.TasksGroup
import com.example.mytodoapp.databinding.BottomSheetChooseGroupBinding
import com.example.mytodoapp.features.task.edit.EditTaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChooseGroupBottomSheetFragment : BaseBottomSheet(), GroupAdapter.OnGroupClickListener {

    private var _binding: BottomSheetChooseGroupBinding? = null
    private val binding get() = _binding!!

    private val editTaskViewModel: EditTaskViewModel by viewModels({ requireParentFragment() })

    private var taskGroups = listOf<TasksGroup>()
    private lateinit var groupsAdapter: GroupAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetChooseGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: make all uniqueness color
//        requireActivity().window.navigationBarColor

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                editTaskViewModel.groups.collect { groups ->
                    taskGroups = groups
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                editTaskViewModel.task.collect { task ->
                    groupsAdapter = GroupAdapter(
                        taskGroups.find { it.taskGroupID == task.groupID },
                        this@ChooseGroupBottomSheetFragment
                    )

                    groupsAdapter.setHasStableIds(true)
                    binding.chooseGroupRecyclerView.adapter = groupsAdapter
                    groupsAdapter.submitList(taskGroups)
                }
            }
        }
    }

    override fun onGroupClick(newSelectedGroup: TasksGroup) {
        editTaskViewModel.setGroup(newSelectedGroup.taskGroupID)
        this.dismiss()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        val TAG: String = this::class.java.name

        @JvmStatic
        fun newInstance() = ChooseGroupBottomSheetFragment()
    }
}