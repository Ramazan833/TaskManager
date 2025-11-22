package com.roma.myapplication4.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// This class MUST have default values for all fields for Firebase/Room compatibility.
@Entity(tableName = "tasks_table")
data class Task(
    @PrimaryKey
    val id: String = "",
    val email: String = "",
    val title: String = "",
    val description: String = ""
)
