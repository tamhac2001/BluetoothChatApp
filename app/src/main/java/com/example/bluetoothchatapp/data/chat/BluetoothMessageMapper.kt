package com.example.bluetoothchatapp.data.chat

import com.example.bluetoothchatapp.domain.chat.BluetoothMessage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun BluetoothMessage.toByteArray(): ByteArray {
    return Json.encodeToString(this).encodeToByteArray()
}