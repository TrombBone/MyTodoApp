package com.example.mytodoapp.features.task.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mytodoapp.MyTodoApp
import com.example.mytodoapp.database.entities.Task
import com.example.mytodoapp.databinding.ItemTasksRecyclerViewBinding
import com.example.mytodoapp.features.task.TaskAdapter

class ItemTasksRecyclerPageFragment : Fragment(), TaskAdapter.TaskStatusListener {

    private var binding: ItemTasksRecyclerViewBinding? = null

    private val recyclerPageViewModel: RecyclerPageViewModel by viewModels {
        RecyclerPageViewModel.RecyclerPageViewModelFactory(
            (requireActivity().application as MyTodoApp).taskRepository
        )
    }

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
//        arguments?.takeIf { it.containsKey("ARG_POSITION") }?.apply { }
        val taskAdapter = TaskAdapter(this)
        binding!!.itemTasksRecycler.adapter = taskAdapter
        recyclerPageViewModel.tasks.observe(requireActivity()) { tasks ->
            tasks.let { taskAdapter.submitList(it) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onTaskCreate(task: Task) {
        recyclerPageViewModel.insert(task)
    }

    override fun onTaskUpdated(task: Task) {
        recyclerPageViewModel.update(task)
    }

}