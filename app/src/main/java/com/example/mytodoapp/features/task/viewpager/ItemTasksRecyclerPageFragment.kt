package com.example.mytodoapp.features.task.viewpager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytodoapp.MyTodoApp
import com.example.mytodoapp.database.entities.Task
import com.example.mytodoapp.databinding.ItemTasksRecyclerViewBinding
import com.example.mytodoapp.features.task.TaskAdapter
import com.example.mytodoapp.utils.MySharedPreferenceManager

class ItemTasksRecyclerPageFragment : Fragment(), TaskAdapter.TaskStatusListener {

    private var binding: ItemTasksRecyclerViewBinding? = null

    private val recyclerPageViewModel: RecyclerPageViewModel by viewModels {
        RecyclerPageViewModel.RecyclerPageViewModelFactory(
            (requireActivity().application as MyTodoApp).taskRepository
        )
    }

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

        Log.d("MYTAG", "I'm in onViewCreated() in ItemTasksRecyclerPageFragment #$currentGroupID")

        binding!!.itemTasksRecycler.adapter = taskAdapter
    }

    override fun onStart() {
        super.onStart()
        Log.d("MYTAG", "I'm in onStart() in ItemTasksRecyclerPageFragment #$currentGroupID")

        recyclerPageViewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            Log.d("MYTAG", "I'm in recyclerPageViewModel.tasks.observe #$currentGroupID")
            tasks?.let { list ->
                Log.d("MYTAG", "tasks: $list")
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

    override fun onResume() {
        super.onResume()
        Log.d("MYTAG", "I'm in onResume() in ItemTasksRecyclerPageFragment #$currentGroupID")

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onTaskUpdated(task: Task) {
        recyclerPageViewModel.update(task)
    }

}