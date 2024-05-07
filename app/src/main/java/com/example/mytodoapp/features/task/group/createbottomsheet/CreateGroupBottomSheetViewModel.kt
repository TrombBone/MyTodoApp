package com.example.mytodoapp.features.task.group.createbottomsheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mytodoapp.database.entities.TasksGroup
import com.example.mytodoapp.database.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateGroupBottomSheetViewModel @Inject constructor(
    private val repository: GroupRepository,
//    private val preferenceManager: MySharedPreferenceManager
) : ViewModel() {

    val groupsCount: LiveData<Int> = repository.countGroups.asLiveData()

    fun insert(group: TasksGroup) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
        repository.insert(group)
    }
}