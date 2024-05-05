package com.example.mytodoapp.features.task.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mytodoapp.database.entities.Task
import com.example.mytodoapp.databinding.ItemTasksRecyclerViewBinding
import com.example.mytodoapp.features.task.TaskAdapter
import com.example.mytodoapp.utils.MySharedPreferenceManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemTasksRecyclerPageFragment : Fragment(), TaskAdapter.TaskStatusListener {

    private var binding: ItemTasksRecyclerViewBinding? = null

    private val recyclerPageViewModel: RecyclerPageViewModel by viewModels()

    private val taskAdapter = TaskAdapter(this)

    private var currentGroupID = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = ItemTasksRecyclerViewBinding
            .inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(MySharedPreferenceManager.PREFERENCE_VIEWPAGER_ARG_POSITION) }
            ?.apply {
                currentGroupID = getInt(MySharedPreferenceManager.PREFERENCE_VIEWPAGER_ARG_POSITION)
            }

        taskAdapter.setHasStableIds(true)
        binding!!.itemTasksRecycler.adapter = taskAdapter

    }

    override fun onStart() {
        super.onStart()

        recyclerPageViewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            tasks?.let { list ->
                taskAdapter.submitList(
                    when (currentGroupID) {
                        0 -> list.filter { it.isStared }
                        1 -> list
                        else -> list.filter { it.groupID == currentGroupID.toString() }
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onTaskUpdated(task: Task) {
        recyclerPageViewModel.update(task)
    }

}