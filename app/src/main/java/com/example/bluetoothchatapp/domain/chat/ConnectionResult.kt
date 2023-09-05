package com.example.bluetoothchatapp.domain.chat

sealed interface ConnectionResult {
    data object ConnectionEstablished : ConnectionResult
    data class Error(val message: String) : ConnectionResult
    data class TransferSucceeded(val message: BluetoothMessage): ConnectionResult
}