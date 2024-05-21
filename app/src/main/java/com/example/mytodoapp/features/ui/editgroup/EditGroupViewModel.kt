package com.example.mytodoapp.features.ui.editgroup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mytodoapp.features.database.entities.Task
import com.example.mytodoapp.features.database.entities.TasksGroup
import com.example.mytodoapp.features.database.repositories.GroupRepository
import com.example.mytodoapp.features.database.repositories.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditGroupViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {

    val allGroups: LiveData<List<TasksGroup>> = groupRepository.allGroups.asLiveData()

    fun deleteGroup(group: TasksGroup) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
        groupRepository.delete(group)
    }

    fun tasksSelectedGroup(groupID: String) =
        taskRepository.fetchTasksSelectedGroup(groupID).asLiveData()

    fun updateTaskGroupID(task: Task) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
        taskRepository.update(task)
    }

}