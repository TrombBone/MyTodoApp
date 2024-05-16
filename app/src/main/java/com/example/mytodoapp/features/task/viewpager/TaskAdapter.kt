package com.example.mytodoapp.features.task.viewpager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.example.mytodoapp.components.abstracts.BaseAdapter
import com.example.mytodoapp.components.abstracts.BaseFragment
import com.example.mytodoapp.components.extensions.setStrikeThroughEffect
import com.example.mytodoapp.databinding.ItemTaskBinding
import com.example.mytodoapp.features.database.entities.Task
import com.google.android.material.checkbox.MaterialCheckBox
import java.util.UUID

class TaskAdapter(private val statusListener: TaskStatusListener) :
    BaseAdapter<Task, TaskAdapter.TaskViewHolder>(TASK_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
        TaskViewHolder.create(parent, statusListener)

    override fun getItemId(position: Int): Long {
        val uuid = UUID.fromString(getItem(position).taskID)
        return uuid.mostSignificantBits
    }

    class TaskViewHolder(
        itemView: View,
        private val statusListener: TaskStatusListener
    ) :
        BaseViewHolder(itemView) {

        companion object {
            fun create(
                parent: ViewGroup,
                statusListener: TaskStatusListener
            ): TaskViewHolder {
                val binding = ItemTaskBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return TaskViewHolder(binding.root, statusListener)
            }
        }

        private val binding = ItemTaskBinding.bind(itemView)

        override fun <T> onBind(item: T) {
            if (item is Task) with(item as Task) {
                binding.root.transitionName = BaseFragment.TRANSITION_ELEMENT_ROOT + taskID

                binding.taskReadyCheckBox.isChecked = isFinished

                binding.taskTitleTextview.text = title
                binding.taskTitleTextview.setStrikeThroughEffect(isFinished)

                with(binding.taskDetailsTextview) {
                    if (hasDetails()) {
                        isVisible = true
                        text = details
                        setStrikeThroughEffect(isFinished)
                    } else {
                        isVisible = false
                    }
                }

                with(binding.taskDatetimeChip) {
                    if (hasDueDate()) {
                        isVisible = true
                        text = formatDueDateTime(binding.root.context)
                        setOnClickListener {
                            statusListener.onDueDateTimeChipClick(item)
                        }
                        setOnCloseIconClickListener {
                            statusListener.onTaskUpdated(copy(dueDate = null, dueTime = null))
                        }
                    } else {
                        isVisible = false
                    }
                }

                binding.taskStaredCheckBox.isChecked = isStared

                binding.taskReadyCheckBox.setOnClickListener {
                    with(it as MaterialCheckBox) {
                        isFinished = isChecked
                        binding.taskTitleTextview.setStrikeThroughEffect(isChecked)
                        if (hasDetails())
                            binding.taskDetailsTextview.setStrikeThroughEffect(isChecked)
                    }
                    statusListener.onTaskUpdated(this)
                }

                /*
                // not working, but it should...
                binding.taskReadyCheckBox.addOnCheckedStateChangedListener { _, state ->
                    when (state) {
                        MaterialCheckBox.STATE_CHECKED -> {
                            isFinished = true
                            binding.taskTitleTextview.setStrikeThroughEffect(true)
                            binding.taskDetailsTextview.setStrikeThroughEffect(true)
                        }
                        // STATE_INDETERMINATE || STATE_UNCHECKED
                        else -> {
                            isFinished = false
                            binding.taskTitleTextview.setStrikeThroughEffect(false)
                            binding.taskDetailsTextview.setStrikeThroughEffect(false)
                        }
                    }
                    statusListener.onTaskUpdated(this)
                }
                */

                binding.taskStaredCheckBox.setOnClickListener {
                    with(it as MaterialCheckBox) {
                        isStared = isChecked
                    }
                    statusListener.onTaskUpdated(this)
                }

                // not working, but it should...
                /*
                binding.taskStaredCheckBox.setOnCheckedChangeListener { checkBox, isChecked ->
                    if (checkBox == binding.taskStaredCheckBox) {
                        isStared = isChecked
                        statusListener.onTaskUpdated(this)
                    }
                }
                */
                /*
                binding.taskStaredCheckBox.addOnCheckedStateChangedListener { _, state ->
                    val newTask = when (state) {
                        MaterialCheckBox.STATE_CHECKED -> copy(isStared = true)
                        //MaterialCheckBox.STATE_INDETERMINATE
                        else -> copy(isStared = false)
                    }
                    statusListener.onTaskUpdated(newTask)
                }
                */

                binding.root.isClickable = true
                binding.root.setOnClickListener {
                    statusListener.onTaskClick(this)
                }
            }
        }
    }

    companion object {
        private val TASK_COMPARATOR = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
                oldItem === newItem

            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
                oldItem.taskID == newItem.taskID
        }
    }

    interface TaskStatusListener {
        fun onTaskUpdated(task: Task)

        fun onTaskClick(task: Task)

        fun onDueDateTimeChipClick(task: Task)
    }

}