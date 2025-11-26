package com.roma.myapplication4

/**
 * Represents a single message in the chat.
 * @param text The content of the message.
 * @param isFromUser True if the message was sent by the user, false if it's from the bot.
 */
data class Message(val text: String, val isFromUser: Boolean)
