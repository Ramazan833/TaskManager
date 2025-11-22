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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
            val messageText = messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                val userMessage = Message(messageText, true)
                chatAdapter.addMessage(userMessage)
                chatRecyclerView.scrollToPosition(messages.size - 1)

                // Simulate bot response
                val botResponse = Message("Это автоматический ответ.", false)
                chatAdapter.addMessage(botResponse)
                chatRecyclerView.scrollToPosition(messages.size - 1)

                messageInput.text.clear()
            }
        }

        if (messages.isEmpty()) {
            messages.add(Message("Здравствуйте! Чем я могу вам помочь?", false))
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
}
