package com.example.bluetoothchatapp.presentation.device_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.bluetoothchatapp.domain.chat.BluetoothDevice

@Composable
fun DevicesList(
    title: String,
    devices: List<BluetoothDevice>,
    onDeviceClick: (BluetoothDevice) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = Modifier,userScrollEnabled = false) {
        item {
            Text(text = title)
        }
        items(devices) { device ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDeviceClick(device)
                    }, horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = device.name ?: "(No name)")
                Text(text = device.address)
            }
        }
    }
}