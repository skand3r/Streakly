package com.example.streakly

import android.bluetooth.BluetoothDevice
import android.util.Log


class StreaklyShareStepsManager (private val bluetoothService: BluetoothService) : ShareStepsManager{
    init {
        bluetoothService.startServer()
    }

    override fun addOnReceiveListener(callback: (Int) -> Unit) {
        bluetoothService.startReceivingSteps { steps ->
            callback(steps)
        }
    }

    override fun sendSteps(steps: Int, device: BluetoothDevice) {
        //stop the server and connect to the server of the other device
        bluetoothService.stopServer()
        bluetoothService.connectToDevice(device)

        //send the steps
        bluetoothService.sendSteps(steps)

        //start the server again, to receive steps
        bluetoothService.startServer()
    }

    override fun getPairedDevices (): List<BluetoothDevice> {
        return bluetoothService.getPairedDevices()
    }
}