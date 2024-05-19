package com.example.mytodoapp.features.task.group.choose

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.mytodoapp.components.abstracts.BaseAdapter
import com.example.mytodoapp.components.abstracts.BaseFragment
import com.example.mytodoapp.components.extensions.setDrawableChecked
import com.example.mytodoapp.databinding.ItemGroupBinding
import com.example.mytodoapp.features.database.entities.TasksGroup
import java.util.UUID

class GroupAdapter(
    private val selectedGroup: TasksGroup?,
    private val clickListener: OnGroupClickListener
) : BaseAdapter<TasksGroup, GroupAdapter.GroupViewHolder>(GROUP_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder =
        GroupViewHolder.create(parent, selectedGroup, clickListener)

    override fun getItemId(position: Int): Long {
        if (getItem(position).taskGroupID == "1") return 1L // FIXME: hardcode
        val uuid = UUID.fromString(getItem(position).taskGroupID)
        return uuid.mostSignificantBits
    }

    class GroupViewHolder(
        itemView: View,
        private val selectedGroup: TasksGroup?,
        private val clickListener: OnGroupClickListener
    ) :
        BaseViewHolder(itemView) {

        companion object {
            fun create(
                parent: ViewGroup,
                selectedGroup: TasksGroup?,
                clickListener: OnGroupClickListener
            ): GroupViewHolder {
                val binding = ItemGroupBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return GroupViewHolder(binding.root, selectedGroup, clickListener)
            }
        }

        private val binding = ItemGroupBinding.bind(itemView)

        override fun <T> onBind(item: T) {
            if (item is TasksGroup) with(binding.root) {
                transitionName = BaseFragment.TRANSITION_ELEMENT_ROOT + item.taskGroupID

                text = item.groupTitle
                setDrawableChecked(item.taskGroupID == selectedGroup?.taskGroupID)

                setOnClickListener {
//                    if (item.taskGroupID != selectedGroup?.taskGroupID) {
//                        binding.itemGroupTitleTextView.setDrawableChecked(true)
//                    }

                    clickListener.onGroupClick(TasksGroup(item.taskGroupID, item.groupTitle))
                }
            }
        }
    }

    companion object {
        private val GROUP_COMPARATOR = object : DiffUtil.ItemCallback<TasksGroup>() {
            override fun areItemsTheSame(oldItem: TasksGroup, newItem: TasksGroup): Boolean =
                oldItem === newItem

            override fun areContentsTheSame(oldItem: TasksGroup, newItem: TasksGroup): Boolean =
                oldItem.taskGroupID == newItem.taskGroupID

        }
    }

    interface OnGroupClickListener {
        fun onGroupClick(newSelectedGroup: TasksGroup)
    }

}