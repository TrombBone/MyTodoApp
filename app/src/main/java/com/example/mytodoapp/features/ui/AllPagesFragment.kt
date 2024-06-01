package com.example.mytodoapp.features.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mytodoapp.R
import com.example.mytodoapp.components.abstracts.BaseFragment
import com.example.mytodoapp.databinding.FragmentAllPagesBinding
import com.example.mytodoapp.features.database.entities.TasksGroup
import com.example.mytodoapp.features.ui.creategroup.CreateOrEditGroupBottomSheet
import com.example.mytodoapp.features.ui.createtask.CreateTaskBottomSheet
import com.example.mytodoapp.features.ui.editgroup.GroupEditActionsBottomSheet
import com.example.mytodoapp.features.ui.page.RecyclerPageFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

private const val KEY_LAST_SELECTED_TAB = "KEY_LAST_SELECTED_TAB"

@AndroidEntryPoint
class AllPagesFragment : BaseFragment() {

    private var _binding: FragmentAllPagesBinding? = null
    private val binding get() = _binding!!

    private val groupsViewModel: GroupsViewModel by viewModels()

    private var allGroups: List<TasksGroup> = listOf()

    private var lastSelectedPosition = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lastSelectedPosition = savedInstanceState?.getInt(KEY_LAST_SELECTED_TAB, 1) ?: 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAllPagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addTaskFloatingActionButton.transitionName = TRANSITION_ELEMENT_ROOT
        binding.addTaskFloatingActionButton.setOnClickListener { showCreateTaskBottomSheet() }

        binding.tasksBottomAppbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.group_actions -> {
                    showGroupEditActionsBottomSheet()
                    true
                }
                // TODO: create sort
//                R.id.sort -> {
//                    // Handle sort icon press
//                    true
//                }
                else -> false
            }
        }

        requireActivity().window.navigationBarColor =
            binding.tasksBottomAppbar.backgroundTint?.defaultColor
                ?: requireActivity().window.navigationBarColor

        // Update UI
        binding.apply {
            groupsViewModel.allGroups.observe(viewLifecycleOwner) { tasksGroups ->
                tasksGroups?.let { allGroups = it }

                tasksListContainerViewPager.adapter = TasksListViewPagerAdapter(requireActivity())
                TabLayoutMediator(tabLayout, tasksListContainerViewPager) { tab, position ->
                    when (position) {
                        0 -> tab.setIcon(R.drawable.ic_star_fill_24)
                        else -> {
                            tab.text = allGroups[position - 1].groupTitle ?: "Group $position"
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

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    binding.tasksBottomAppbar.menu.children.find { it.itemId == R.id.group_actions }
                        ?.isVisible = tab.position > 1
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
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
        outState.putInt(KEY_LAST_SELECTED_TAB, lastSelectedPosition)
    }

    private fun showCreateTaskBottomSheet() {
        val currentItem = binding.tasksListContainerViewPager.currentItem
        val selectedGroupID =
            if (currentItem > 1) allGroups[currentItem - 1].taskGroupID else currentItem.toString()
        val createTaskModalBottomSheet =
            CreateTaskBottomSheet.newInstance(selectedGroupID)
        createTaskModalBottomSheet.show(
            childFragmentManager,
            CreateTaskBottomSheet.TAG
        )
    }

    private fun showCreateGroupBottomSheet() {
        lastSelectedPosition = binding.tasksListContainerViewPager.currentItem
        val createGroupBottomSheet =
            CreateOrEditGroupBottomSheet.newCreateInstance()
        createGroupBottomSheet.show(
            childFragmentManager,
            CreateOrEditGroupBottomSheet.TAG
        )
    }

    private fun showGroupEditActionsBottomSheet() {
        val selectedGroupID =
            allGroups[binding.tasksListContainerViewPager.currentItem - 1].taskGroupID
        val groupEditActionsBottomSheet =
            GroupEditActionsBottomSheet.newInstance(selectedGroupID)
        groupEditActionsBottomSheet.show(
            childFragmentManager,
            CreateOrEditGroupBottomSheet.TAG
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

        override fun getItemCount() = allGroups.size + 1

        override fun createFragment(position: Int): Fragment =
            RecyclerPageFragment.newInstance(position, allGroups)

    }

    companion object {
        @JvmStatic
        fun newInstance() = AllPagesFragment()
    }
}