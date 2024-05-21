package com.example.mytodoapp.features.ui.page

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mytodoapp.features.database.entities.Task
import com.example.mytodoapp.features.database.repositories.TaskRepository
import com.example.mytodoapp.features.notifications.NotificationAlarmManager
import com.example.mytodoapp.features.notifications.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecyclerPageViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val notificationHelper: NotificationHelper,
    private val notificationAlarmManager: NotificationAlarmManager
//    private val preferenceManager: MySharedPreferenceManager
) : ViewModel() {

    val tasks: LiveData<List<Task>> = repository.allTasks.asLiveData()

    fun update(task: Task) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
        repository.update(task)
    }

    fun setAlarmInFuture(task: Task) {
        notificationAlarmManager.setAlarmInFuture(task)
    }

    fun dismissNotificationOnFinishedTask(task: Task) {
        if (task.isFinished)
            notificationHelper.dismissNotification(task.taskID)
    }
}