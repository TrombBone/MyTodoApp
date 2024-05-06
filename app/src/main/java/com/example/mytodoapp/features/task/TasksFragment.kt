package com.example.mytodoapp.features.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mytodoapp.R
import com.example.mytodoapp.abstracts.BaseFragment
import com.example.mytodoapp.database.entities.TasksGroup
import com.example.mytodoapp.databinding.FragmentTasksBinding
import com.example.mytodoapp.features.task.createbottomsheet.CreateTaskModalBottomSheet
import com.example.mytodoapp.features.task.group.GroupsViewModel
import com.example.mytodoapp.features.task.viewpager.ItemTasksRecyclerPageFragment
import com.example.mytodoapp.utils.MySharedPreferenceManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : BaseFragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private val groupsViewModel: GroupsViewModel by viewModels()

    private var groups: List<TasksGroup> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.addTaskFloatingActionButton.transitionName = TRANSITION_ELEMENT_ROOT

        binding.apply {
            groupsViewModel.allGroups.observe(viewLifecycleOwner) { tasksGroups ->
                tasksGroups?.let { groups = it }

                tasksListContainerViewPager.adapter = TasksListViewPagerAdapter(requireActivity())
                TabLayoutMediator(tabLayout, tasksListContainerViewPager) { tab, position ->
                    when (position) {
                        0 -> tab.setIcon(R.drawable.ic_star_fill_24)
                        else -> {
                            // FIXME: how will it work after adding a new group?
                            tab.text = groups[position - 1].groupTitle ?: "Group $position"
                        }
                    }
                }.attach()
                tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_add_24))

                // FIXME: how will it work after adding a new group?
                if (groups.isNotEmpty())
                    binding.tasksListContainerViewPager.currentItem = 1
            }
        }

//        postponeEnterTransition()
//        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onResume() {
        super.onResume()
        binding.addTaskFloatingActionButton.setOnClickListener { showCreateTaskBottomSheet() }
//        binding.tasksListContainerViewPager.currentItem = 1

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
//                if (tab != null)
//                    if (tab.position == groups.size) {
//                        // TODO: call create tab dialog
//                    }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })


    }

    private fun showCreateTaskBottomSheet() {
        val createTaskModalBottomSheet = CreateTaskModalBottomSheet()
        createTaskModalBottomSheet.show(childFragmentManager, CreateTaskModalBottomSheet.TAG)
    }

    /**
     * This fragment lifecycle method is called when the view hierarchy associated with the fragment
     * is being removed. As a result, clear out the binding object.
     */
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private inner class TasksListViewPagerAdapter(activity: FragmentActivity) :
        FragmentStateAdapter(activity) {

        override fun getItemCount() = groups.size + 1

        override fun createFragment(position: Int): Fragment {
            val fragment = ItemTasksRecyclerPageFragment()
            fragment.arguments = Bundle().apply {
                putInt(ItemTasksRecyclerPageFragment.ARG_KEY_POSITION, position)
            }
            return fragment
        }

    }
}