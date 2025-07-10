package com.example.streakly

import android.bluetooth.BluetoothDevice

interface ShareStepsManager {
    fun addOnReceiveListener(callback: (Int) -> Unit)
    fun sendSteps(steps: Int, device: BluetoothDevice)
    fun getPairedDevices (): List<BluetoothDevice>
}