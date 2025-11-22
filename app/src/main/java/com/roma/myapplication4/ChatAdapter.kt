package com.roma.myapplication4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Message(
    val text: String = "",
    val isUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false // To show a loading indicator
)

class ChatAdapter(private val messages: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_USER = 1
        private const val TYPE_BOT = 2
        private const val TYPE_LOADING = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            messages[position].isLoading -> TYPE_LOADING
            messages[position].isUser -> TYPE_USER
            else -> TYPE_BOT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_USER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_user, parent, false)
                UserViewHolder(view)
            }
            TYPE_BOT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_bot, parent, false)
                BotViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_loading, parent, false)
                LoadingViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is UserViewHolder -> holder.tvUserMessage.text = message.text
            is BotViewHolder -> holder.tvBotMessage.text = message.text
            is LoadingViewHolder -> { /* No data to bind */ }
        }
    }

    override fun getItemCount(): Int = messages.size

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUserMessage: TextView = view.findViewById(R.id.tvUserMessage)
    }

    class BotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvBotMessage: TextView = view.findViewById(R.id.tvBotMessage)
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
