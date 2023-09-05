package com.example.bluetoothchatapp.data.chat

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BluetoothEnableStateReceiver(private val onStateChange: (isEnable: Boolean) -> Unit) :
    BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val connectState =
            intent?.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)
        when (intent?.action) {
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                if (connectState == BluetoothAdapter.STATE_ON) {
                    onStateChange(true)
                } else if (connectState == BluetoothAdapter.STATE_OFF) {
                    onStateChange(false)
                }
            }
        }
    }
}