package com.example.bluetoothchatapp.presentation.chat_screen

import com.example.bluetoothchatapp.domain.chat.BluetoothMessage

data class ChatUiState(
    val messages: List<BluetoothMessage> = emptyList(),
    val editingMessage: String? = null
)
