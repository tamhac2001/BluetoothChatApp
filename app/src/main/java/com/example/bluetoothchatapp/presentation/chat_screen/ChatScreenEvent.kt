package com.example.bluetoothchatapp.presentation.chat_screen

sealed interface ChatScreenEvent {
    data object DisconnectClick : ChatScreenEvent
    data class EditNewMessage(val message: String?) : ChatScreenEvent
    data object SendMessageClick : ChatScreenEvent
    data object NavigateUp : ChatScreenEvent
}