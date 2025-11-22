package com.roma.myapplication4.data

class UserRepository(private val userDao: UserDao) {

    suspend fun addUser(user: User) {
        userDao.addUser(user)
    }
}
