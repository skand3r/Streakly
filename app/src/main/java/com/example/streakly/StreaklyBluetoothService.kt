package com.example.streakly

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.io.IOException
import java.util.*

@SuppressLint("MissingPermission")
class StreaklyBluetoothService(
    private val context: Context
) : BluetoothService {

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val pairedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    private var connectedSocket: BluetoothSocket? = null
    private var acceptThread: AcceptThread? = null
    private val uuid = UUID.fromString("355c4449-cec4-4f71-bbd2-0c09bc71d3e6")
    private var dataCallback: (Int) -> Unit = {}

    init {
        updatePairedDevices()
    }

    private fun updatePairedDevices() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            Log.e("bluetooth", "no permission")
            return
        }
        bluetoothAdapter
            ?.bondedDevices
            ?.also { devices -> pairedDevices.update { devices.toList() } }
    }

    override fun sendSteps(steps: Int) {
        connectedSocket?.outputStream?.let { out ->
            try {
                out.write(steps)
                Log.d("Bluetooth", "sent data: $steps")
            } catch (e: IOException) {
                Log.e("Bluetooth", "Error during sendData: ${e.message}")
            }
        }
    }

    fun _startReceivingSteps() {
        val receiveThread = Thread {
            try {
                connectedSocket?.inputStream?.use { input ->
                    try {
                        val data = input.read().toInt()
                        Log.d("Bluetooth", "Received data: $data")
                        dataCallback(data)
                    } catch (e: IOException) {
                        Log.w("Bluetooth", "Tried to receive data, but socket was closed")
                    }


                }
            } catch (e: IOException) {
                Log.e("Bluetooth", "Error during receiveData", e)
            }
        }
        receiveThread.start()
    }

    override fun startReceivingSteps(callback: (Int) -> Unit) {
        dataCallback = callback
    }

    override fun stopReceivingSteps() {
        connectedSocket?.close()
    }

    override fun getPairedDevices(): List<BluetoothDevice> {
        return pairedDevices.value.toList()
    }

    override fun connectToDevice(device: BluetoothDevice) {
        try {
            val socket = device.createRfcommSocketToServiceRecord(uuid)
            bluetoothAdapter?.cancelDiscovery() // cancel discovery before connecting
            socket.connect()
            connectedSocket = socket
            _startReceivingSteps()
            Log.d("Bluetooth", "Connected to device: ${device.name}")
        } catch (e: IOException) {
            Log.e("Bluetooth", "Could not connect to device: ${e.localizedMessage}")
            connectedSocket = null
        }
    }

    override fun startServer() {
        acceptThread = AcceptThread()
        acceptThread?.start()
    }

    override fun stopServer() {
        acceptThread?.cancel()
    }

    private inner class AcceptThread : Thread() {
        private val serverSocket: BluetoothServerSocket? by lazy {
            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord("AppName", uuid)
        }

        override fun run() {
            while (true) {
                try {
                    val socket: BluetoothSocket = serverSocket?.accept() ?: break
                    serverSocket?.close()
                    manageServerConnection(socket)
                    break
                } catch (e: IOException) {
                    Log.e("Bluetooth", "Socket's accept() method failed", e)
                    break
                }
            }
        }

        fun cancel() {
            try {
                serverSocket?.close()
            } catch (e: IOException) {
                Log.e("Bluetooth", "Could not close the server socket", e)
            }
        }
    }

    private fun manageServerConnection(socket: BluetoothSocket) {
        connectedSocket = socket
        Log.d("Bluetooth", "Connection established with client")
        _startReceivingSteps()
    }

    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}