package com.example.mytodoapp.features.task.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mytodoapp.features.database.entities.TasksGroup
import com.example.mytodoapp.features.database.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val repository: GroupRepository,
//    private val preferenceManager: MySharedPreferenceManager
) : ViewModel() {

    val allGroups: LiveData<List<TasksGroup>> = repository.allGroups.asLiveData()

    fun insert(group: TasksGroup) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
        repository.insert(group)
    }

    fun delete(group: TasksGroup) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
        repository.delete(group)
    }

    fun update(group: TasksGroup) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
        repository.update(group)
    }

}