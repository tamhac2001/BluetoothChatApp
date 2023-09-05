package com.example.bluetoothchatapp.data.chat

import android.bluetooth.BluetoothSocket
import com.example.bluetoothchatapp.domain.chat.BluetoothMessage
import com.example.bluetoothchatapp.domain.chat.TransferFailedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.IOException

class BluetoothDataTransferService(
    private val socket: BluetoothSocket
) {
    fun listenForIncomingMessages(): Flow<BluetoothMessage> {
        return flow {
            if (!socket.isConnected) {
                return@flow
            }
            val buffer = ByteArray(1024)
            while (true) {
                val byteCount = try {
                    socket.inputStream.read(buffer)
                } catch (e: IOException) {
                    e.printStackTrace()
                    throw TransferFailedException()
                }
                val jsonMessage = buffer.decodeToString(
                    endIndex = byteCount
                )
                println(jsonMessage)
                emit(
                    Json.decodeFromString<BluetoothMessage>(
                        jsonMessage
                    ).copy(isFromLocalUser = false)
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun sendMessage(bytes: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                socket.outputStream.write(bytes)
            } catch (e: IOException) {
                e.printStackTrace()
                return@withContext false
            }
            true
        }
    }
}