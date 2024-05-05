package com.example.mytodoapp.features.task

import androidx.lifecycle.ViewModel
import com.example.mytodoapp.database.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val repository: TaskRepository,
//    private val preferenceManager: MySharedPreferenceManager
) : ViewModel() {

//    val tasks: LiveData<List<Task>> = repository.allTasks.asLiveData()
//
//    fun insert(task: Task) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
//        repository.insert(task)
//    }
//
//    fun delete(task: Task) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
//        repository.delete(task)
//    }
//
//    fun update(task: Task) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
//        repository.update(task)
//    }

}