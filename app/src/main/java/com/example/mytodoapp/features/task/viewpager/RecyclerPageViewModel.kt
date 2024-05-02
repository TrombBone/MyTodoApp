package com.example.mytodoapp.features.task.viewpager

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

    fun insert(task: Task) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
        repository.insert(task)
    }

    fun delete(task: Task) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
        repository.delete(task)
    }

    fun update(task: Task) = viewModelScope.launch(Dispatchers.IO + NonCancellable) {
        repository.update(task)
    }

    /*
    // another way to use factory
    companion object {
        fun provideFactory(fragment: Fragment): RecyclerPageViewModel {
            val factory =
                RecyclerPageViewModelFactory((fragment.requireActivity().application as MyTodoApp).taskRepository)
            return ViewModelProvider(fragment, factory)[RecyclerPageViewModel::class.java]
        }
    }
    */

    class RecyclerPageViewModelFactory(
        private val repository: TaskRepository,
//    private val preferenceManager: MySharedPreferenceManager
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecyclerPageViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RecyclerPageViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown View Model Class")
        }
    }
}