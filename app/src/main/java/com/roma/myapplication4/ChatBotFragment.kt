package com.roma.myapplication4

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ChatBotFragment : Fragment() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<Message>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chatbot, container, false)

        val rootLayout = view.findViewById<RelativeLayout>(R.id.chat_root_layout)
        val animDrawable = rootLayout.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

        chatRecyclerView = view.findViewById(R.id.chat_recycler_view)
        messageInput = view.findViewById(R.id.message_input)
        sendButton = view.findViewById(R.id.send_button)

        setupRecyclerView()

        sendButton.setOnClickListener {
            handleSendMessage()
        }

        if (messages.isEmpty()) {
            messages.add(Message("Здравствуйте! Я ваш личный ассистент на базе Cohere. Спросите меня о чем-нибудь.", false))
            chatAdapter.notifyDataSetChanged()
        }

        return view
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messages)
        chatRecyclerView.adapter = chatAdapter
        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        chatRecyclerView.layoutManager = layoutManager
    }

    private fun handleSendMessage() {
        val messageText = messageInput.text.toString().trim()
        if (messageText.isNotEmpty()) {
            // Add user's message and clear input
            val userMessage = Message(messageText, true)
            chatAdapter.addMessage(userMessage)
            chatRecyclerView.scrollToPosition(messages.size - 1)
            messageInput.text.clear()

            // Show typing indicator and get bot response
            addTypingIndicator()
            getBotResponse(messageText)
        }
    }

    private fun addTypingIndicator() {
        val typingMessage = Message("Печатает...", false)
        chatAdapter.addMessage(typingMessage)
        chatRecyclerView.scrollToPosition(messages.size - 1)
    }

    private fun getBotResponse(userMessage: String) {
        lifecycleScope.launch {
            // CORE FIX: Switched from HuggingFaceService to CohereService
            val response = CohereService.getCompletion(userMessage)
            // Remove typing indicator
            messages.removeAt(messages.size - 1)
            chatAdapter.notifyItemRemoved(messages.size)

            // Add real response
            val botMessage = Message(response, false)
            chatAdapter.addMessage(botMessage)
            chatRecyclerView.scrollToPosition(messages.size - 1)
        }
    }
}
