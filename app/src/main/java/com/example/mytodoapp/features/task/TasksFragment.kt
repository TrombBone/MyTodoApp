package com.example.mytodoapp.features.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.mytodoapp.abstracts.BaseFragment
import com.example.mytodoapp.databinding.FragmentTasksBinding

class TasksFragment : BaseFragment() {

    companion object {
        fun newInstance() = TasksFragment()
    }

    private var binding: FragmentTasksBinding? = null

    private val viewModel: TasksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentTasksBinding
            .inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            floatingActionButton.setOnClickListener { createTask() }
        }
    }

    /**
     * Create new Task
     *
     * Call a Dialog
     */
    private fun createTask() {
        // TODO("Not yet implemented")
    }

    /**
     * This fragment lifecycle method is called when the view hierarchy associated with the fragment
     * is being removed. As a result, clear out the binding object.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}