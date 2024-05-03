package com.example.mytodoapp.features.task.createbottomsheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mytodoapp.database.entities.Task
import com.example.mytodoapp.database.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateTaskBottomSheetViewModel @Inject constructor(
    private val repository: TaskRepository,
//    private val preferenceManager: MySharedPreferenceManager
) : ViewModel() {

    val tasks: LiveData<List<Task>> = repository.allTasks.asLiveData()

    fun insert(task: Task) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
        repository.insert(task)
    }

    /*
    // another way to use factory
    companion object {
        fun provideFactory(fragment: Fragment): CreateTaskBottomSheetViewModel {
            val factory =
                CreateTaskBottomSheetViewModelFactory((fragment.requireActivity().application as MyTodoApp).taskRepository)
            return ViewModelProvider(fragment, factory)[CreateTaskBottomSheetViewModel::class.java]
        }
    }
    */

    class CreateTaskBottomSheetViewModelFactory(
        private val repository: TaskRepository,
//    private val preferenceManager: MySharedPreferenceManager
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CreateTaskBottomSheetViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CreateTaskBottomSheetViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown View Model Class")
        }
    }
}