package com.example.mytodoapp.features.task.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
class GroupsViewModel @Inject constructor(
    private val repository: GroupRepository,
//    private val preferenceManager: MySharedPreferenceManager
) : ViewModel() {

    val allGroups: LiveData<List<TasksGroup>> = repository.allGroups.asLiveData()

    val groupsCount: LiveData<Int> = repository.countGroups.asLiveData()

    fun insert(group: TasksGroup) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
        repository.insert(group)
    }

    fun delete(group: TasksGroup) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
        repository.delete(group)
    }

    fun update(group: TasksGroup) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
        repository.update(group)
    }

    /*
    // another way to use factory
    companion object {
        fun provideFactory(fragment: Fragment): GroupsViewModel {
            val factory = GroupsViewModelFactory((fragment.requireActivity().application as MyTodoApp).groupRepository)
            return ViewModelProvider(fragment, factory)[GroupsViewModel::class.java]
        }
    }
    */

    class GroupsViewModelFactory(
        private val repository: GroupRepository,
//    private val preferenceManager: MySharedPreferenceManager
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GroupsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return GroupsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown View Model Class")
        }
    }
}