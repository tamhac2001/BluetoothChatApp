package com.example.bluetoothchatapp.presentation.chat_screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.bluetoothchatapp.domain.chat.BluetoothMessage
import com.example.bluetoothchatapp.presentation.BluetoothState
import com.example.bluetoothchatapp.presentation.chat_screen.components.ChatMessage
import com.example.bluetoothchatapp.utils.BluetoothChatScreen
import com.example.bluetoothchatapp.utils.currentRoute

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen(
    chatState: ChatUiState,
    bluetoothState: BluetoothState,
    navController: NavController,
    onEvent: (ChatScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "MESSAGE") },
            navigationIcon = {
                IconButton(onClick = {
                    onEvent(ChatScreenEvent.DisconnectClick)
                    navController.popBackStack(
                        route = BluetoothChatScreen.DeviceScreen.name,
                        inclusive = false
                    )
                }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close chat")
                }
            })
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding(), start = 4.dp, end = 4.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0f)
            ) {
                items(chatState.messages) { message ->
                    ChatMessage(message = message)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            Row(Modifier.fillMaxWidth()) {
                val keyboardController = LocalSoftwareKeyboardController.current
                TextField(value = chatState.editingMessage ?: "", onValueChange = { message ->
                    onEvent(ChatScreenEvent.EditNewMessage(message))
                }, trailingIcon = {
                    if (!chatState.editingMessage.isNullOrEmpty()) {
                        IconButton(onClick = {
                            onEvent(ChatScreenEvent.EditNewMessage(""))
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = "Clear text"
                            )
                        }
                    }
                }, modifier = Modifier.weight(1.0f)
                )
                IconButton(onClick = {
                    onEvent(ChatScreenEvent.SendMessageClick)
                    keyboardController?.hide()
                    onEvent(ChatScreenEvent.EditNewMessage(""))
                }, enabled = !chatState.editingMessage.isNullOrEmpty()) {
                    Icon(imageVector = Icons.Filled.Send, contentDescription = "Send message")
                }
            }
        }
    }

    if (navController.currentRoute != BluetoothChatScreen.ChatScreen.name) return
    if (!bluetoothState.isConnected) {
        navController.popBackStack(route = BluetoothChatScreen.DeviceScreen.name, inclusive = false)
    }

    BackHandler {
        onEvent(ChatScreenEvent.NavigateUp)
    }
}

@Preview
@Composable
private fun ChatScreenPreview() {
    ChatScreen(
        chatState = ChatUiState(
            messages = listOf(
                BluetoothMessage(
                    message = "There is no one who loves pain itself, who seeks after it and wants to have it, simply because it is pain...",
                    senderName = "Unknown",
                    isFromLocalUser = false
                ),
                BluetoothMessage(
                    message = "There is no one who loves pain itself, who seeks after it and wants to have it, simply because it is pain...",
                    senderName = "Unknown",
                    isFromLocalUser = true
                )
            )
        ),
        bluetoothState = BluetoothState(isConnected = true),
        navController = rememberNavController(),
        onEvent = {},
    )
}


