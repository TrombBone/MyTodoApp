package com.example.mytodoapp.features.task.createbottomsheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytodoapp.database.entities.Task
import com.example.mytodoapp.database.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateTaskBottomSheetViewModel @Inject constructor(
    private val repository: TaskRepository,
//    private val preferenceManager: MySharedPreferenceManager
) : ViewModel() {

    fun insert(task: Task) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
        repository.insert(task)
    }

}