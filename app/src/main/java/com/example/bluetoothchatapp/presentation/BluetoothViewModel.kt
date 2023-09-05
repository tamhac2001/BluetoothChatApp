package com.example.bluetoothchatapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetoothchatapp.domain.chat.BluetoothDevice
import com.example.bluetoothchatapp.domain.chat.BluetoothRepository
import com.example.bluetoothchatapp.domain.chat.ConnectionResult
import com.example.bluetoothchatapp.presentation.device_screen.DeviceScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothRepository: BluetoothRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BluetoothState())
    val state = combine(
        bluetoothRepository.pairedDevices, bluetoothRepository.scannedDevices, _state
    ) { pairedDevices, scannedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices, pairedDevices = pairedDevices
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BluetoothState())

    private var deviceConnectionJob: Job? = null

    init {
        bluetoothRepository.isEnabled.onEach { isEnabled ->
            _state.update { it.copy(isEnabled = isEnabled) }
        }.launchIn(viewModelScope)
        bluetoothRepository.isConnected.onEach { isConnected ->
            _state.update {
                it.copy(isConnecting = false, isConnected = isConnected)
            }
        }.launchIn(viewModelScope)

        bluetoothRepository.errors.onEach { error ->
            _state.update {
                it.copy(errorMessage = error)
            }
        }.launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        deviceConnectionJob?.cancel()
        deviceConnectionJob = null
    }

    private fun connectToDevice(device: BluetoothDevice) {
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothRepository.connectToDevice(device).listen()
    }

    fun disconnectFromDevice() {
        deviceConnectionJob?.cancel()
        bluetoothRepository.closeConnection()
        _state.update { it.copy(isConnecting = false, isConnected = false) }
    }

    private fun waitForIncomingConnections() {
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothRepository.startBluetoothServer().listen()
    }

    private fun startScan() {
        bluetoothRepository.startDiscover()
    }

    private fun stopScan() {
        bluetoothRepository.stopDiscover()
    }


    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when (result) {
                ConnectionResult.ConnectionEstablished -> _state.update {
                    it.copy(
                        isConnected = true, isConnecting = false, errorMessage = null

                    )

                }

                is ConnectionResult.Error -> _state.update {
                    it.copy(
                        isConnected = false, isConnecting = false, errorMessage = result.message
                    )
                }

                is ConnectionResult.TransferSucceeded -> bluetoothRepository.updateBluetoothMessage(
                    result.message
                )
            }
        }.catch { throwable ->
            bluetoothRepository.closeConnection()
            _state.update {
                it.copy(
                    isConnected = false,
                    isConnecting = false,
                )
            }
        }.launchIn(viewModelScope)
    }

    fun <T> onEvent(event: T) {
        when (event) {
            is DeviceScreenEvent -> {
                when (event) {
                    DeviceScreenEvent.StartScanClick -> startScan()
                    DeviceScreenEvent.StartServerClick -> waitForIncomingConnections()
                    DeviceScreenEvent.StopScanClick -> stopScan()
                    is DeviceScreenEvent.DeviceClick -> connectToDevice(event.device)
                }
            }
        }
    }

}