package com.example.bluetoothchatapp.presentation.device_screen

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.bluetoothchatapp.R
import com.example.bluetoothchatapp.domain.chat.BluetoothDeviceDomain
import com.example.bluetoothchatapp.presentation.BluetoothState
import com.example.bluetoothchatapp.presentation.device_screen.conponents.BluetoothDevice
import com.example.bluetoothchatapp.utils.BluetoothChatScreen
import com.example.bluetoothchatapp.utils.currentRoute

@Composable
fun DeviceScreen(
    navController: NavController,
    state: BluetoothState,
    onEvent: (DeviceScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    if (state.isConnecting && navController.currentRoute == BluetoothChatScreen.DeviceScreen.name) {
        navController.navigate(BluetoothChatScreen.LoadingScreen.name)
    }
//    state.errorMessage?.let { message ->
//        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
//    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar {
                    val text = if (state.isConnected) "You're connected" else ""
                    Text(text = text)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                item {
                    Text(text = "Paired Devices")
                }
                items(state.pairedDevices) { device ->
                    BluetoothDevice(device = device) {
                        onEvent(DeviceScreenEvent.DeviceClick(it))
                    }
                }
                item {
                    Text(text = "Scanned Devices")
                }
                items(state.scannedDevices) { device ->
                    BluetoothDevice(device = device) {
                        onEvent(DeviceScreenEvent.DeviceClick(it))
                    }
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    onEvent(DeviceScreenEvent.StartScanClick)
                }) {
                    Text(text = stringResource(R.string.start_scan))
                }
                Button(onClick = {
                    onEvent(DeviceScreenEvent.StopScanClick)
                }) {
                    Text(text = stringResource(R.string.stop_scan))
                }
                Button(onClick = {
                    onEvent(DeviceScreenEvent.StartServerClick)
                }) {
                    Text(text = stringResource(R.string.start_server))
                }
            }
        }
    }
}

@Preview
@Composable
private fun DeviceScreenPreview() {
    DeviceScreen(navController = rememberNavController(), state = BluetoothState(
        scannedDevices = listOf(
            BluetoothDeviceDomain("TV SAMSUNG 6 SERIES SUPER AMOLED", "00:00:00:00:00:00")
        )
    ), onEvent = {})
}

