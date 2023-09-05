package com.example.bluetoothchatapp.data.chat

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import com.example.bluetoothchatapp.domain.chat.BluetoothDeviceDomain
import com.example.bluetoothchatapp.domain.chat.BluetoothMessage
import com.example.bluetoothchatapp.domain.chat.BluetoothRepository
import com.example.bluetoothchatapp.domain.chat.ConnectionResult
import com.example.bluetoothchatapp.domain.chat.toBluetoothDeviceDomain
import com.example.bluetoothchatapp.utils.isAndroidVersion12OrHigher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class AndroidBluetoothRepository(
    private val context: Context
) : BluetoothRepository {

    companion object {
        const val SERVICE_UUID = "aa1aa8f1-33f5-4294-93b2-e02681e2f5c2"
    }

    private val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val bluetoothAdapter = bluetoothManager.adapter

    private var dataTransferService: BluetoothDataTransferService? = null

    private val _isEnabled = MutableStateFlow<Boolean>(false)
    override val isEnabled: StateFlow<Boolean>
        get() = _isEnabled.asStateFlow()

    private val _isConnected = MutableStateFlow<Boolean>(false)
    override val isConnected: StateFlow<Boolean>
        get() = _isConnected.asStateFlow()

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _pairedDevices.asStateFlow()

    private val _errors = MutableSharedFlow<String>()
    override val errors: SharedFlow<String>
        get() = _errors.asSharedFlow()

    private val _bluetoothMessages = MutableStateFlow<List<BluetoothMessage>>(emptyList())
    override val bluetoothMessages: StateFlow<List<BluetoothMessage>>
        get() = _bluetoothMessages.asStateFlow()

    private val bluetoothEnableStateReceiver = BluetoothEnableStateReceiver { isEnable ->
        _isEnabled.update { isEnable }
    }

    private val foundDeviceReceiver = FoundDeviceReceiver { device ->
        _scannedDevices.update { devices ->
            val newDevice = device.toBluetoothDeviceDomain()
            if (newDevice in devices) devices else devices + newDevice
        }
    }

    private val bluetoothConnectStateReceiver =
        BluetoothConnectStateReceiver { isConnected, bluetoothDevice ->
            if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true) {
                _isConnected.update { isConnected }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    _errors.emit("Can't connect to a non-paired devices")
                }
            }
        }

    private var currentServerSocket: BluetoothServerSocket? = null
    private var currentClientSocket: BluetoothSocket? = null

    init {
        updatePairedDevices()
        context.registerReceiver(
            bluetoothEnableStateReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
        context.registerReceiver(bluetoothConnectStateReceiver, IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        })
    }

    override fun startDiscover() {
        if (isAndroidVersion12OrHigher() && !hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }
        context.registerReceiver(foundDeviceReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))

        updatePairedDevices()

        bluetoothAdapter?.startDiscovery()
    }

    override fun stopDiscover() {
        if (isAndroidVersion12OrHigher() && !hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }
        bluetoothAdapter?.cancelDiscovery()
    }

    override fun startBluetoothServer(): Flow<ConnectionResult> {
        return flow {
            if (isAndroidVersion12OrHigher() && !hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission")
            } else if (!isAndroidVersion12OrHigher() && !hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                throw SecurityException("No ACCESS_FINE_LOCATION permission")
            }

            currentServerSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                "chat_service", UUID.fromString(SERVICE_UUID)
            )

            var shouldLoop = true
            while (shouldLoop) {
                currentClientSocket = try {
                    currentServerSocket?.accept()
                } catch (e: Exception) {
                    println(e.stackTrace)
                    shouldLoop = false
                    null
                }
                currentClientSocket?.let { socket ->
                    currentServerSocket?.close()
                    shouldLoop = false
                    emit(ConnectionResult.ConnectionEstablished)
                    val service = BluetoothDataTransferService(socket)
                    dataTransferService = service
                    emitAll(service.listenForIncomingMessages().map {
                        ConnectionResult.TransferSucceeded(it)
                    })

                }
            }
        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    override fun connectToDevice(device: BluetoothDeviceDomain): Flow<ConnectionResult> {
        return flow {
            if (isAndroidVersion12OrHigher() && !hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission")
            } else if (!isAndroidVersion12OrHigher() && !hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                throw SecurityException("No ACCESS_FINE_LOCATION permission")
            }
            stopDiscover()
            val bluetoothDevice = bluetoothAdapter?.getRemoteDevice(device.address)
            currentClientSocket = bluetoothDevice?.createRfcommSocketToServiceRecord(
                UUID.fromString(SERVICE_UUID)
            )
//            if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == false) {
//            }
            currentClientSocket?.let { socket ->
                try {
                    socket.connect()
                    emit(ConnectionResult.ConnectionEstablished)
                    val service = BluetoothDataTransferService(socket)
                    dataTransferService = service
                    emitAll(service.listenForIncomingMessages().map {
                        ConnectionResult.TransferSucceeded(it)
                    })

                } catch (e: IOException) {
                    println(e.stackTrace)
                    socket.close()
                    emit(ConnectionResult.Error("Connection was interrupted"))
                }
            }
        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun trySendMessage(message: String): BluetoothMessage? {
        if (isAndroidVersion12OrHigher() && !hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            throw SecurityException("No BLUETOOTH_CONNECT permission")
        } else if (!isAndroidVersion12OrHigher() && !hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            throw SecurityException("No ACCESS_FINE_LOCATION permission")
        }
        if (dataTransferService == null) {
            return null
        }
        val bluetoothMessage =
            BluetoothMessage(message, bluetoothAdapter.name ?: "Unknown name", true)
        dataTransferService?.sendMessage(Json.encodeToString(bluetoothMessage).toByteArray())
        return bluetoothMessage
    }

    override fun updateBluetoothMessage(message: BluetoothMessage) {
        _bluetoothMessages.update {
            it + message
        }
    }


    override fun closeConnection() {
        currentClientSocket?.close()
        currentServerSocket?.close()
        currentClientSocket = null
        currentServerSocket = null
    }

    override fun release() {
        context.unregisterReceiver(bluetoothEnableStateReceiver)
        context.unregisterReceiver(foundDeviceReceiver)
        context.unregisterReceiver(bluetoothConnectStateReceiver)
        closeConnection()
    }

    private fun updatePairedDevices() {
        bluetoothAdapter?.bondedDevices?.map {
            it.toBluetoothDeviceDomain()
        }?.let { devices ->
            _pairedDevices.update { devices }
        }
    }

    private fun hasPermission(permission: String): Boolean =
        context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED


}