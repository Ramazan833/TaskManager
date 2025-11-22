package com.roma.myapplication4.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.roma.myapplication4.data.AppDatabase
import com.roma.myapplication4.data.Task
import com.roma.myapplication4.data.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
    }

    fun getTasksForUser(userEmail: String): LiveData<List<Task>> {
        return repository.getTasksForUser(userEmail)
    }

    fun addTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(task)
        }
    }
}
