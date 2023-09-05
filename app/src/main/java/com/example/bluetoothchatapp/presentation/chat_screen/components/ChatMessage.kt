package com.example.bluetoothchatapp.presentation.chat_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluetoothchatapp.domain.chat.BluetoothMessage
import com.example.bluetoothchatapp.presentation.ui.theme.BluetoothChatAppTheme
import com.example.bluetoothchatapp.presentation.ui.theme.OldRose
import com.example.bluetoothchatapp.presentation.ui.theme.Vanilla
import java.time.Instant
import java.time.temporal.ChronoUnit

@Composable
fun ChatMessage(
    message: BluetoothMessage,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = if (message.isFromLocalUser) Alignment.End else Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (!message.isFromLocalUser) Text(text = message.senderName)
        BoxWithConstraints(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (message.isFromLocalUser) OldRose else Vanilla)
        ) {
            Text(
                text = message.message,
                fontSize = 16.sp,
                textAlign = TextAlign.Justify,
                modifier = Modifier
                    .wrapContentWidth()
                    .widthIn(max = maxWidth.times(0.8f))
                    .padding(horizontal = 8.dp)
            )
        }
        val timeNow = Instant.now()
        val sentTime = Instant.ofEpochMilli(message.sentTime)
        val minutesDiffBetweenSentTimeAndNow = sentTime.until(timeNow, ChronoUnit.MINUTES)
        val displayedTime =
            if (minutesDiffBetweenSentTimeAndNow > 0) "$minutesDiffBetweenSentTimeAndNow ago" else "Just sent"
        Text(text = displayedTime, fontSize = 10.sp)
    }
}

@Preview
@Composable
fun ChatMessagePreview() {
    BluetoothChatAppTheme {
        ChatMessage(
            message = BluetoothMessage(
                message = "There is no one who loves pain itself, who seeks after it and wants to have it, simply because it is pain...",
                senderName = "Unknown",
                isFromLocalUser = false
            ),
        )
    }
}