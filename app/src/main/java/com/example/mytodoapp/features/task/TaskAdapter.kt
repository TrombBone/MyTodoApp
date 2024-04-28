package com.example.mytodoapp.features.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.example.mytodoapp.abstracts.BaseAdapter
import com.example.mytodoapp.abstracts.BaseFragment
import com.example.mytodoapp.database.entities.Task
import com.example.mytodoapp.databinding.ItemTaskBinding
import com.example.mytodoapp.extensions.setStrikeThroughEffect
import com.google.android.material.checkbox.MaterialCheckBox

class TaskAdapter(private val statusListener: TaskStatusListener) :
    BaseAdapter<Task, TaskAdapter.TaskViewHolder>(TASK_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
        TaskViewHolder.create(parent, statusListener)

    class TaskViewHolder(itemView: View, private val statusListener: TaskStatusListener) :
        BaseViewHolder(itemView) {

        companion object {
            fun create(parent: ViewGroup, statusListener: TaskStatusListener): TaskViewHolder {
                val binding = ItemTaskBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return TaskViewHolder(binding.root, statusListener)
            }
        }

        private val binding = ItemTaskBinding.bind(itemView)

        override fun <T> onBind(item: T) {
            if (item is Task) {
                with(item as Task) {
                    binding.root.transitionName = BaseFragment.TRANSITION_ELEMENT_ROOT + taskID

                    binding.taskReadyCheckBox.isChecked = isFinished
                    binding.taskTitleTextview.text = title

                    if (details.isNullOrEmpty()) {
                        binding.taskDetailsTextview.isVisible = false
                    } else {
                        binding.taskDetailsTextview.isVisible = true
                        binding.taskDetailsTextview.text = details
                    }

                    binding.taskStaredCheckBox.isChecked = isStared

                    if (hasDueDate()) {
                        binding.taskDatetimeChip.isVisible = true
                        binding.taskDatetimeChip.text = formatDueDate(binding.root.context)
                    } else {
                        binding.taskDatetimeChip.isVisible = false
                    }

                    binding.taskReadyCheckBox.addOnCheckedStateChangedListener { _, state ->
                        when (state) {
                            MaterialCheckBox.STATE_CHECKED -> {
                                isFinished = true
                                with(binding) {
                                    taskTitleTextview.setStrikeThroughEffect(true)
                                    taskDetailsTextview.setStrikeThroughEffect(true)
                                }
                            }

                            MaterialCheckBox.STATE_UNCHECKED -> {
                                isFinished = false
                                with(binding) {
                                    taskTitleTextview.setStrikeThroughEffect(false)
                                    taskDetailsTextview.setStrikeThroughEffect(false)
                                }
                            }
                            //MaterialCheckBox.STATE_INDETERMINATE
                            else -> {
                                isFinished = false
                                with(binding) {
                                    taskTitleTextview.setStrikeThroughEffect(false)
                                    taskDetailsTextview.setStrikeThroughEffect(false)
                                }
                            }
                        }
                        binding.taskReadyCheckBox.isChecked = isFinished // FIXME: really needs it?
                        statusListener.onTaskUpdated(this)
                    }

                    binding.taskStaredCheckBox.addOnCheckedStateChangedListener { _, state ->
                        isStared = when (state) {
                            MaterialCheckBox.STATE_CHECKED -> true
                            MaterialCheckBox.STATE_UNCHECKED -> false
                            //MaterialCheckBox.STATE_INDETERMINATE
                            else -> false
                        }
                        binding.taskStaredCheckBox.isChecked = isStared // FIXME: really needs it?
                        statusListener.onTaskUpdated(this)
                    }

                }
            }
        }
    }

    companion object {
        private val TASK_COMPARATOR = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem.taskID == newItem.taskID
            }
        }
    }

    interface TaskStatusListener {

        fun onTaskCreate(task: Task)

        fun onTaskUpdated(task: Task)

    }

}