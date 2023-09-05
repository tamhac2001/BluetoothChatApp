package com.example.bluetoothchatapp.presentation.device_screen

import com.example.bluetoothchatapp.domain.chat.BluetoothDevice

sealed interface DeviceScreenEvent {
    data object StartScanClick : DeviceScreenEvent
    data object StopScanClick : DeviceScreenEvent
    data object StartServerClick : DeviceScreenEvent
    data class DeviceClick(val device: BluetoothDevice) : DeviceScreenEvent
}