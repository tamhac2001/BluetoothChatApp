package com.example.bluetoothchatapp.presentation.loading_screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.bluetoothchatapp.presentation.BluetoothState
import com.example.bluetoothchatapp.utils.BluetoothChatScreen
import com.example.bluetoothchatapp.utils.currentRoute

@Composable
fun LoadingScreen(
    navController: NavController,
    state: BluetoothState,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(modifier = Modifier.wrapContentSize())
            Text(text = "Connecting")
        }
    }
    BackHandler {
        navController.navigateUp()
        onNavigateUp()
    }
    if (navController.currentRoute != BluetoothChatScreen.LoadingScreen.name) return
    if (state.isConnecting) return
    if (state.isConnected) navController.navigate(BluetoothChatScreen.ChatScreen.name)
    else navController.navigateUp()
}

@Preview
@Composable
private fun LoadingScreenPreview() {
    LoadingScreen(
        navController = rememberNavController(),
        state = BluetoothState(isConnecting = true),
        {}
    )
}