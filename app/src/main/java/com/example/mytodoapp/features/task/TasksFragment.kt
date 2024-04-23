package com.example.mytodoapp.features.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mytodoapp.R
import com.example.mytodoapp.abstracts.BaseFragment
import com.example.mytodoapp.database.AppDatabase
import com.example.mytodoapp.database.dao.TaskDAO
import com.example.mytodoapp.databinding.FragmentTasksBinding
import com.example.mytodoapp.features.task.viewpager.ItemTasksRecyclerPageFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : BaseFragment(), TaskAdapter.TaskStatusListener {

    private var binding: FragmentTasksBinding? = null
    private lateinit var database: AppDatabase
    private val taskDAO: TaskDAO by lazy { database.getTaskDAO() }

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

            tasksListContainerViewPager.adapter = TasksListViewPagerAdapter(requireActivity())
            TabLayoutMediator(tabLayout, tasksListContainerViewPager) { tab, position ->
                when (position) {
                    0 -> {
                        tab.setIcon(R.drawable.ic_star_fill_24)
                    }

                    1 -> {
                        tab.text = "My tasks"
                    }

                    else -> {
                        tab.setIcon(R.drawable.ic_add_24)
                    }
                }
            }.attach()

//            createTasksListTab.setOnClickListener {
//                // TODO: call create tab dialog
//            }

            addTaskFloatingActionButton.transitionName = TRANSITION_ELEMENT_ROOT
            addTaskFloatingActionButton.setOnClickListener { showCreateTaskBottomSheet() }
        }

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    /**
     * Create new Task
     *
     * Call a Dialog
     */
    private fun showCreateTaskBottomSheet() {
        val createTaskModalBottomSheet = CreateTaskModalBottomSheet()
        createTaskModalBottomSheet.show(childFragmentManager, CreateTaskModalBottomSheet.TAG)
    }

    /**
     * This fragment lifecycle method is called when the view hierarchy associated with the fragment
     * is being removed. As a result, clear out the binding object.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onTaskUpdated(task: Task) {
        TODO("Not yet implemented")
    }

    private class TasksListViewPagerAdapter(activity: FragmentActivity) :
        FragmentStateAdapter(activity) {

        override fun getItemCount() = 4 // FIXME: how can I count groups?

        override fun createFragment(position: Int): Fragment {
            val fragment = ItemTasksRecyclerPageFragment()
//            fragment.arguments = Bundle().apply { putInt("ARG_POSITION", position + 1) }
            return ItemTasksRecyclerPageFragment()
        }

    }
}