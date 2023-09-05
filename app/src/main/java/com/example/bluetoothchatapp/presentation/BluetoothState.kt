package com.example.bluetoothchatapp.presentation

import com.example.bluetoothchatapp.domain.chat.BluetoothDevice

data class BluetoothState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val isEnabled: Boolean = false,
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null
)