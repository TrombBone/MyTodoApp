package com.example.mytodoapp.features.task.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytodoapp.features.database.entities.Task
import com.example.mytodoapp.features.database.entities.TasksGroup
import com.example.mytodoapp.features.database.repository.TaskRepository
import com.example.mytodoapp.features.notifications.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val notificationHelper: NotificationHelper,
//    private val preferenceManager: MySharedPreferenceManager
) : ViewModel() {

    private val _task = MutableStateFlow(Task())
    val task: StateFlow<Task> = _task.asStateFlow()

    private val _groups = MutableStateFlow(listOf<TasksGroup>())
    val groups: StateFlow<List<TasksGroup>> = _groups.asStateFlow()

    fun setTask(task: Task) {
        _task.update { task }
    }

    fun setAllGroups(groupsList: List<TasksGroup>) {
        _groups.update { groupsList }
    }

    fun setGroup(groupID: String) {
        _task.update { it.copy(groupID = groupID) }
    }

    fun setTitle(title: String?) {
        _task.update { it.copy(title = if (title?.trim().isNullOrEmpty()) null else title) }
    }

    fun setDetails(details: String?) {
        _task.update { it.copy(details = if (details?.trim().isNullOrEmpty()) null else details) }
    }

    fun setStared(isStared: Boolean = false) {
        _task.update { it.copy(isStared = isStared) }
    }

    fun setFinished(isFinished: Boolean = false) {
        _task.update { it.copy(isFinished = isFinished) }
    }

    fun setDueDate(dueDate: LocalDate?) {
        _task.update { it.copy(dueDate = dueDate) }
    }

    fun setDueTime(dueTime: LocalTime?) {
        _task.update { it.copy(dueTime = dueTime) }
    }

    fun update(task: Task) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
        repository.update(task)
    }

    fun delete(task: Task) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
        repository.delete(task)
    }

    fun dismissNotificationOnDeletedTask(task: Task) {
        notificationHelper.dismissNotification(task.taskID)
    }
}