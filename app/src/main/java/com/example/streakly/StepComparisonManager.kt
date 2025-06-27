package com.example.streakly

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.UUID

/**
 * Helper object to share the current step count with another device using a
 * simple Bluetooth peer-to-peer connection. This implementation uses a fixed
 * UUID and the first paired device for demonstration purposes.
 */
object StepComparisonManager {
    private const val TAG = "StepComparisonManager"
    private val APP_UUID: UUID = UUID.fromString("c7047887-54b3-48ba-b43e-b0b30a7d81c0")

    private var serverSocket: BluetoothServerSocket? = null
    private var serverThread: Thread? = null
    private var clientThread: Thread? = null

    /**
     * Send the given step count to another paired device via Bluetooth.
     * The first bonded device is used as the target for simplicity.
     */
    fun sendSteps(context: Context, steps: Int) {
        if (!hasPermission(context)) return

        val adapter = BluetoothAdapter.getDefaultAdapter() ?: return
        val device = adapter.bondedDevices.firstOrNull() ?: return

        clientThread = Thread {
            try {
                val socket = device.createRfcommSocketToServiceRecord(APP_UUID)
                socket.connect()
                socket.outputStream.use { out ->
                    out.write(steps.toString().toByteArray(Charsets.UTF_8))
                    out.flush()
                }
                socket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Error sending steps", e)
            }
        }.also { it.start() }
    }

    /**
     * Start listening for incoming step counts from another device. When a
     * value is received the [onStepsReceived] callback is invoked on the main
     * thread.
     */
    fun startReceiving(context: Context, onStepsReceived: (Int) -> Unit) {
        if (!hasPermission(context)) return

        val adapter = BluetoothAdapter.getDefaultAdapter() ?: return

        serverThread = Thread {
            try {
                serverSocket = adapter.listenUsingRfcommWithServiceRecord(
                    "StreaklySteps",
                    APP_UUID
                )
                val socket: BluetoothSocket = serverSocket!!.accept()
                val data = socket.inputStream.use { it.readBytes() }
                val steps = String(data, Charsets.UTF_8).trim().toIntOrNull()
                if (steps != null) {
                    Handler(Looper.getMainLooper()).post { onStepsReceived(steps) }
                }
                socket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Error receiving steps", e)
            } finally {
                try {
                    serverSocket?.close()
                } catch (_: IOException) {
                }
            }
        }.also { it.start() }
    }

    /**
     * Stop any running transmissions or listeners.
     */
    fun stop(context: Context) {
        try {
            serverSocket?.close()
        } catch (_: IOException) {
        }
        serverThread?.interrupt()
        clientThread?.interrupt()
    }

    private fun hasPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    }
}