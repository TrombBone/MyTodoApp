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
import com.example.mytodoapp.database.entities.TasksGroup
import com.example.mytodoapp.databinding.FragmentTasksBinding
import com.example.mytodoapp.features.task.createbottomsheet.CreateTaskModalBottomSheetFragment
import com.example.mytodoapp.features.task.group.GroupsViewModel
import com.example.mytodoapp.features.task.group.createbottomsheet.CreateGroupModalBottomSheetFragment
import com.example.mytodoapp.features.task.viewpager.ItemTasksRecyclerPageFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : BaseFragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private val groupsViewModel: GroupsViewModel by viewModels()

    private var groups: List<TasksGroup> = listOf()

    private val LAST_SELECTED_TAB_KEY = "LAST_SELECTED_TAB_KEY"
    private var lastSelectedPosition = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lastSelectedPosition = savedInstanceState?.getInt(LAST_SELECTED_TAB_KEY, 1) ?: 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addTaskFloatingActionButton.transitionName = TRANSITION_ELEMENT_ROOT
        binding.addTaskFloatingActionButton.setOnClickListener { showCreateTaskBottomSheet() }

        requireActivity().window.navigationBarColor =
            binding.tasksListBottomAppbar.backgroundTint?.defaultColor
                ?: requireActivity().window.navigationBarColor

        binding.apply {
            groupsViewModel.allGroups.observe(viewLifecycleOwner) { tasksGroups ->
                tasksGroups?.let { groups = it }

                tasksListContainerViewPager.adapter = TasksListViewPagerAdapter(requireActivity())
                TabLayoutMediator(tabLayout, tasksListContainerViewPager) { tab, position ->
                    when (position) {
                        0 -> tab.setIcon(R.drawable.ic_star_fill_24)
                        else -> {
                            tab.text = groups[position - 1].groupTitle ?: "Group $position"
                            // FIXME: !Workaround for tab click without select!
                            tab.view.setOnTouchListener(null)
                            tab.view.isClickable = true
                        }
                    }
                }.attach()
                tasksListContainerViewPager.post {
                    tasksListContainerViewPager.setCurrentItem(lastSelectedPosition, false)
                }
                val addGroupTab = tabLayout.newTab().setIcon(R.drawable.ic_add_24)
                tabLayout.addTab(addGroupTab)

                // FIXME: !Workaround for tab click without select! (write my OnSelectListener() ?)
                addGroupTab.view.isClickable = false
                addGroupTab.view.setOnTouchListener { _, _ ->
                    showCreateGroupBottomSheet()
                    false
                }

            }
        }


        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onPause() {
        super.onPause()
        lastSelectedPosition = binding.tasksListContainerViewPager.currentItem
    }

    override fun onResume() {
        super.onResume()
        binding.tasksListContainerViewPager.currentItem = lastSelectedPosition
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(LAST_SELECTED_TAB_KEY, lastSelectedPosition)
    }

    private fun showCreateTaskBottomSheet() {
        val createTaskModalBottomSheet = CreateTaskModalBottomSheetFragment()
        createTaskModalBottomSheet.show(
            childFragmentManager,
            CreateTaskModalBottomSheetFragment.TAG
        )
    }

    private fun showCreateGroupBottomSheet() {
        lastSelectedPosition = binding.tasksListContainerViewPager.currentItem
        val createGroupModalBottomSheet = CreateGroupModalBottomSheetFragment()
        createGroupModalBottomSheet.show(
            childFragmentManager,
            CreateGroupModalBottomSheetFragment.TAG
        )
    }

    /**
     * This fragment lifecycle method is called when the view hierarchy associated with the fragment
     * is being removed. As a result, clear out the binding object.
     */
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private inner class TasksListViewPagerAdapter(activity: FragmentActivity) :
        FragmentStateAdapter(activity) {

        override fun getItemCount() = groups.size + 1

        override fun createFragment(position: Int): Fragment {
            val fragment = ItemTasksRecyclerPageFragment()
            fragment.arguments = Bundle().apply {
                putInt(ItemTasksRecyclerPageFragment.ARG_KEY_POSITION, position)
                putParcelableArrayList(
                    ItemTasksRecyclerPageFragment.ARG_KEY_ALL_GROUPS,
                    ArrayList<TasksGroup>(groups)
                )
            }
            return fragment
        }

    }
}