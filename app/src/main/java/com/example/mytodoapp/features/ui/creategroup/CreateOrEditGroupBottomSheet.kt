package com.example.mytodoapp.features.ui.creategroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.example.mytodoapp.components.abstracts.BaseBottomSheet
import com.example.mytodoapp.databinding.BottomSheetCreateGroupBinding
import com.example.mytodoapp.features.database.entities.TasksGroup
import com.example.mytodoapp.features.ui.GroupsViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_SELECTED_GROUP_ID = "ARG_SELECTED_GROUP_ID"

@AndroidEntryPoint
class CreateOrEditGroupBottomSheet : BaseBottomSheet() {
    private var selectedGroupID: String? = null
    private var isEditInsteadCreate: Boolean = false

    private var _binding: BottomSheetCreateGroupBinding? = null
    private val binding get() = _binding!!

    private val groupsViewModel: GroupsViewModel by viewModels({ requireParentFragment() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            takeIf { it.containsKey(ARG_SELECTED_GROUP_ID) }?.apply {
                selectedGroupID = getString(ARG_SELECTED_GROUP_ID)!!
                isEditInsteadCreate = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCreateGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            addGroupTitleTextInputEditText.requestFocus()

            saveGroupButton.isEnabled = false
            addGroupTitleTextInputEditText.doOnTextChanged { text, _, _, _ ->
                saveGroupButton.isEnabled = !(text?.trim().isNullOrEmpty())
            }

            if (isEditInsteadCreate) {
                groupsViewModel.allGroups.observe(viewLifecycleOwner) { groups ->
                    val selectedGroup = groups.find { it.taskGroupID == selectedGroupID }
                    addGroupTitleTextInputEditText.setText(selectedGroup?.groupTitle ?: "")
                }
            }

            saveGroupButton.setOnClickListener {
                val groupTitle =
                    if (addGroupTitleTextInputEditText.text?.trim().isNullOrEmpty()) null
                    else addGroupTitleTextInputEditText.text!!.trim().toString()

                if (isEditInsteadCreate) groupsViewModel.update(
                    TasksGroup(
                        taskGroupID = selectedGroupID!!,
                        groupTitle = groupTitle
                    )
                )
                else groupsViewModel.insert(TasksGroup(groupTitle = groupTitle))

                this@CreateOrEditGroupBottomSheet.dismiss()
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
        fun newCreateInstance() = CreateOrEditGroupBottomSheet()

        @JvmStatic
        fun newEditInstance(selectedGroupID: String) =
            CreateOrEditGroupBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_SELECTED_GROUP_ID, selectedGroupID)
                }
            }
    }
}