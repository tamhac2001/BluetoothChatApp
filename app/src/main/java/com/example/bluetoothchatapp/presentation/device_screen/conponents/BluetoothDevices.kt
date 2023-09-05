package com.example.bluetoothchatapp.presentation.device_screen.conponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.example.bluetoothchatapp.domain.chat.BluetoothDevice

@Composable
fun BluetoothDevice(device: BluetoothDevice, onDeviceClick: (BluetoothDevice) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onDeviceClick(device)
            }, horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = device.name ?: "(No name)",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
//            modifier = Modifier.fillMaxWidth(0.5f)
        )
        Text(text = device.address, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}