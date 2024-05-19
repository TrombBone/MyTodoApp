package com.example.mytodoapp.features.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mytodoapp.features.database.entities.Task
import com.example.mytodoapp.features.database.repositories.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val repository: TaskRepository,
//    private val preferenceManager: MySharedPreferenceManager
) : ViewModel() {

//    val tasks: LiveData<List<Task>> = repository.allTasks.asLiveData()

    fun tasksSelectedGroup(groupID: String) =
        repository.fetchTasksSelectedGroup(groupID).asLiveData()

//    fun insert(task: Task) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
//        repository.insert(task)
//    }

//    fun delete(task: Task) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
//        repository.delete(task)
//    }

    fun update(task: Task) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
        repository.update(task)
    }

}