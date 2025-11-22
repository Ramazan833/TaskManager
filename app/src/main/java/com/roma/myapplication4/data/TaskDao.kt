package com.roma.myapplication4.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks_table WHERE email = :userEmail ORDER BY id DESC")
    fun getTasksForUser(userEmail: String): LiveData<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

}
