package com.example.mytodoapp.features.task.group.createbottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.example.mytodoapp.abstracts.BaseBottomSheet
import com.example.mytodoapp.database.entities.TasksGroup
import com.example.mytodoapp.databinding.BottomSheetCreateGroupBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class CreateGroupModalBottomSheetFragment : BaseBottomSheet() {

    private var _binding: BottomSheetCreateGroupBinding? = null
    private val binding get() = _binding!!

    private val createGroupBottomSheetViewModel: CreateGroupBottomSheetViewModel by viewModels()

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

            saveGroupButton.setOnClickListener {
                val group = TasksGroup(
                    taskGroupID = UUID.randomUUID().toString(),
                    groupTitle =
                    if (addGroupTitleTextInputEditText.text?.trim().isNullOrEmpty()) null
                    else addGroupTitleTextInputEditText.text!!.trim().toString()
                )

                createGroupBottomSheetViewModel.insert(group)

                this@CreateGroupModalBottomSheetFragment.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        val TAG: String = this::class.java.name
    }
}