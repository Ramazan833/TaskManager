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
import androidx.fragment.app.activityViewModels // CORE FIX: Import activityViewModels delegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatBotFragment : Fragment() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var chatAdapter: ChatAdapter

    // CORE FIX: Scope the ViewModel to the Activity's lifecycle, not the Fragment's.
    // This ensures the same ViewModel instance is used across fragment transactions.
    private val chatViewModel: ChatViewModel by activityViewModels()

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

        // Observe the messages LiveData from the ViewModel
        chatViewModel.messages.observe(viewLifecycleOwner) {
            chatAdapter.submitList(it) {
                // Scroll to the bottom after the list is updated
                chatRecyclerView.scrollToPosition(it.size - 1)
            }
        }

        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                chatViewModel.sendMessage(messageText)
                messageInput.text.clear()
            }
        }

        return view
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        chatRecyclerView.adapter = chatAdapter
        chatRecyclerView.layoutManager = layoutManager
    }
}
