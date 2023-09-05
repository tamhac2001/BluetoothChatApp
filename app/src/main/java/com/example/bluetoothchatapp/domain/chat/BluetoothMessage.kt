package com.example.bluetoothchatapp.domain.chat

import kotlinx.serialization.Serializable

@Serializable
data class BluetoothMessage(
    val message: String,
    val senderName: String,
    val isFromLocalUser: Boolean,
    val sentTime: Long = System.currentTimeMillis()
)
