package com.example.bluetoothchatapp.presentation

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bluetoothchatapp.presentation.chat_screen.ChatScreen
import com.example.bluetoothchatapp.presentation.chat_screen.ChatViewModel
import com.example.bluetoothchatapp.presentation.device_screen.DeviceScreen
import com.example.bluetoothchatapp.presentation.loading_screen.LoadingScreen
import com.example.bluetoothchatapp.presentation.ui.theme.BluetoothChatAppTheme
import com.example.bluetoothchatapp.utils.BluetoothChatScreen
import com.example.bluetoothchatapp.utils.isAndroidVersion12OrHigher
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BluetoothChatAppTheme {
                val navController = rememberNavController()
                val bluetoothViewModel = hiltViewModel<BluetoothViewModel>()
                val bluetoothState by bluetoothViewModel.state.collectAsState()

                val chatViewModel = hiltViewModel<ChatViewModel>()
                val chatState by chatViewModel.state.collectAsState()

                val permissionLauncher =
                    rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }
                SideEffect {
                    permissionLauncher.launch(
                        if (isAndroidVersion12OrHigher()) arrayOf(
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN
                        ) else arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                    )
                }
                val enableBluetoothLauncher =
                    rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
                LaunchedEffect(bluetoothState.isEnabled) {
                    if (!bluetoothState.isEnabled) {
                        enableBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                    }
                }
                NavHost(
                    navController = navController,
                    startDestination = BluetoothChatScreen.DeviceScreen.name
                ) {
                    composable(BluetoothChatScreen.DeviceScreen.name) {
                        DeviceScreen(
                            navController,
                            state = bluetoothState,
                            onEvent = bluetoothViewModel::onEvent
                        )
                    }
                    composable(BluetoothChatScreen.LoadingScreen.name) {
                        LoadingScreen(navController, state = bluetoothState, onNavigateUp = {
                            bluetoothViewModel.disconnectFromDevice()
                        })
                    }
                    composable(BluetoothChatScreen.ChatScreen.name) {
                        ChatScreen(
                            chatState = chatState,
                            bluetoothState = bluetoothState,
                            navController = navController,
                            onEvent = chatViewModel::onEvent
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun DeviceScreenPreview() {
    DeviceScreen(navController = rememberNavController(), state = BluetoothState(), onEvent = {})
}

