package com.roma.myapplication4.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.roma.myapplication4.data.AppDatabase
import com.roma.myapplication4.data.User
import com.roma.myapplication4.data.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    fun addUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUser(user)
        }
    }
}
