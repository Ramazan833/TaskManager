package com.roma.myapplication4.data

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao) {

    fun getTasksForUser(userEmail: String): LiveData<List<Task>> {
        return taskDao.getTasksForUser(userEmail)
    }

    suspend fun addTask(task: Task) {
        taskDao.addTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }
}
