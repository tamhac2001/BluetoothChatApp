package com.example.bluetoothchatapp.presentation.chat_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetoothchatapp.domain.chat.BluetoothRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val bluetoothRepository: BluetoothRepository) :
    ViewModel() {

    private val _state = MutableStateFlow<ChatUiState>(ChatUiState())
    val state = combine(
        bluetoothRepository.isConnected,
        bluetoothRepository.bluetoothMessages,
        _state
    ) { isConnected, messages, state ->
        println(messages)
        if (isConnected) state.copy(messages = messages) else ChatUiState()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ChatUiState(emptyList()))


    override fun onCleared() {
        super.onCleared()

    }

    private fun editNewMessage(message: String?) {
        _state.update {
            it.copy(editingMessage = message)
        }
    }

    private fun sendMessage() {
        viewModelScope.launch {
            val bluetoothMessage = bluetoothRepository.trySendMessage(
                state.value.editingMessage.orEmpty()
            )
            if (bluetoothMessage != null) {
                bluetoothRepository.updateBluetoothMessage(bluetoothMessage)

            }
        }
    }

    private fun disconnect() {
        bluetoothRepository.closeConnection()
    }

    fun onEvent(event: ChatScreenEvent) {
        when (event) {
            ChatScreenEvent.DisconnectClick -> disconnect()
            is ChatScreenEvent.EditNewMessage -> editNewMessage(event.message)
            ChatScreenEvent.SendMessageClick -> sendMessage()
            ChatScreenEvent.NavigateUp -> disconnect()
        }
    }


}