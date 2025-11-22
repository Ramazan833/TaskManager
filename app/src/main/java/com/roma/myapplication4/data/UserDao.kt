package com.roma.myapplication4.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun checkUser(email: String): User?

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun login(email: String, password: String): User?
}
