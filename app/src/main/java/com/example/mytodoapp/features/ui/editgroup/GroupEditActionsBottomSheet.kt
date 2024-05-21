package com.example.mytodoapp.features.ui.editgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.mytodoapp.components.abstracts.BaseBottomSheet
import com.example.mytodoapp.databinding.BottomSheetGroupEditActionsBinding
import com.example.mytodoapp.features.database.entities.Task
import com.example.mytodoapp.features.database.entities.TasksGroup
import com.example.mytodoapp.features.ui.creategroup.CreateOrEditGroupBottomSheet
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_SELECTED_GROUP_ID = "ARG_SELECTED_GROUP_ID"

@AndroidEntryPoint
class GroupEditActionsBottomSheet : BaseBottomSheet() {
    private var selectedGroupID: String? = null

    private var _binding: BottomSheetGroupEditActionsBinding? = null
    private val binding get() = _binding!!

    private val deleteGroupViewModel: EditGroupViewModel by viewModels()

    private var tasksSelectedGroup = listOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            takeIf { it.containsKey(ARG_SELECTED_GROUP_ID) }?.apply {
                selectedGroupID = getString(ARG_SELECTED_GROUP_ID)!!
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetGroupEditActionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            selectedGroupID?.let { id ->
                editGroupTitleButton.setOnClickListener {
                    showEditGroupTitleBottomSheet(id)
                    this@GroupEditActionsBottomSheet.dismiss()
                }

                deleteGroupButton.setOnClickListener {
                    deleteGroupViewModel.allGroups.observe(this@GroupEditActionsBottomSheet) { groups ->
                        groups.find { it.taskGroupID == id }
                            ?.let(::showConfirmDeleteDialog)
                        // TODO: if null - show toast "Current group doesn't exist anymore"
                    }
                    this@GroupEditActionsBottomSheet.dismiss()
                }

                deleteGroupViewModel.tasksSelectedGroup(id)
                    .observe(this@GroupEditActionsBottomSheet) { tasks ->
                        tasksSelectedGroup = tasks
                    }
            } // TODO: if null - show toast "Current group doesn't exist anymore"
        }

    }

    private fun showEditGroupTitleBottomSheet(selectedGroupID: String) {
        val editGroupModalBottomSheet =
            CreateOrEditGroupBottomSheet.newEditInstance(selectedGroupID)
        editGroupModalBottomSheet.show(
            parentFragmentManager,
            CreateOrEditGroupBottomSheet.TAG
        )
    }

    private fun showConfirmDeleteDialog(toDeleteGroup: TasksGroup) {
        MaterialAlertDialogBuilder(requireContext())
            // FIXME: Text from res
            .setTitle(/*resources.getString(R.string.title)*/"Are you sure you want to delete this group?")
            .setNeutralButton(/*resources.getString(R.string.cancel)*/"Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(/*resources.getString(R.string.accept)*/"Accept") { dialog, _ ->
                tasksSelectedGroup.forEach {
                    deleteGroupViewModel.updateTaskGroupID(it.copy(groupID = "1"))
                }
                deleteGroupViewModel.deleteGroup(toDeleteGroup)
                dialog.dismiss()
            }
            .show()
    }

    companion object {

        @JvmStatic
        fun newInstance(selectedGroupID: String) =
            GroupEditActionsBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_SELECTED_GROUP_ID, selectedGroupID)
                }
            }
    }
}