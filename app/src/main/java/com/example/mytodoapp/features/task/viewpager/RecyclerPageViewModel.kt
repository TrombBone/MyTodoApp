package com.example.mytodoapp.features.task.viewpager

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mytodoapp.database.entities.Task
import com.example.mytodoapp.database.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecyclerPageViewModel @Inject constructor(
    private val repository: TaskRepository,
//    private val preferenceManager: MySharedPreferenceManager
) : ViewModel() {

    val tasks: LiveData<List<Task>> = repository.allTasks.asLiveData()

    fun fetchTasksSelectedGroup(groupID: String) =
        repository.fetchTasksSelectedGroup(groupID).asLiveData()

    fun update(task: Task) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
        repository.update(task)
    }

}