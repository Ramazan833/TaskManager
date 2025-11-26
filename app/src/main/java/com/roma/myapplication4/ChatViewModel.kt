package com.roma.myapplication4

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>(emptyList())
    val messages: LiveData<List<Message>> = _messages

    init {
        // Add initial greeting message only once when the ViewModel is created
        if (_messages.value.isNullOrEmpty()) {
            _messages.value = listOf(Message("Здравствуйте! Я ваш личный ассистент. Спросите меня о чем-нибудь.", false))
        }
    }

    fun sendMessage(text: String) {
        // Add user message
        addMessage(Message(text, true))
        
        // Add typing indicator
        addMessage(Message("Печатает...", false))

        // Launch a coroutine to get the bot's response
        viewModelScope.launch {
            val responseText = CohereService.getCompletion(text)
            
            // CORE FIX: Update the list in an immutable way to guarantee LiveData updates.
            val currentMessages = _messages.value ?: emptyList()
            // Create a new list without the "Typing..." indicator
            val updatedMessages = currentMessages.dropLast(1).toMutableList()
            // Add the real bot response
            updatedMessages.add(Message(responseText, false))
            
            _messages.postValue(updatedMessages) // Use postValue from a background thread
        }
    }

    // This function now correctly updates the LiveData with a new list instance.
    private fun addMessage(message: Message) {
        val currentList = _messages.value ?: emptyList()
        val newList = currentList.toMutableList().apply {
            add(message)
        }
        _messages.value = newList
    }
}
