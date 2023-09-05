package com.example.bluetoothchatapp.domain.chat

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothRepository {
    val isEnabled: StateFlow<Boolean>
    val isConnected: StateFlow<Boolean>
    val scannedDevices: StateFlow<List<BluetoothDeviceDomain>>
    val pairedDevices: StateFlow<List<BluetoothDeviceDomain>>
    val errors: SharedFlow<String>
    val bluetoothMessages: StateFlow<List<BluetoothMessage>>

    fun startDiscover()
    fun stopDiscover()

    fun startBluetoothServer(): Flow<ConnectionResult>
    fun connectToDevice(device: BluetoothDevice): Flow<ConnectionResult>

    suspend fun trySendMessage(message: String): BluetoothMessage?
    fun updateBluetoothMessage(message: BluetoothMessage)

    fun closeConnection()
    fun release()

}