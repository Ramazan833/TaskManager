package com.roma.myapplication4.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// This class MUST have default values for all fields for Firebase deserialization.
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = ""
)
