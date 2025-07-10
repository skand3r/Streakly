package com.example.streakly

import android.bluetooth.BluetoothDevice

interface BluetoothService {
    fun sendSteps(steps: Int)
    fun startReceivingSteps(callback: (Int) -> Unit)
    fun stopReceivingSteps()
    fun getPairedDevices(): List<BluetoothDevice>
    fun connectToDevice(device: BluetoothDevice)
    fun startServer()
    fun stopServer()
}